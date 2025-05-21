import java.io.IOException

/*
    1. Create an interface Transformation with a single `process` method
        taking an Image and returning another Image
    5. Parse transformations in the main app.
    6. Implement the transformation.
 */
interface Transformation {
    // fun process(image: Image): Image
    operator fun invoke(image: Image): Image

    companion object {
        /*
            3. Add a `parse` method in the Transformation companion
            taking a String and returning a Transformation instance
        - parse("crop 0 10 100 200")            -> Crop(0, 10, 100, 200)
        - parse("blend paris.jpg transparency)  -> Blend(Image(..), Transparency)
        - parse("master yoda") -> NoOp
         */
        fun parse(string: String): Transformation {
            val words = string.split(" ")   // array of strings (words)
            val command = words[0]
            return when (command) {
                "crop" ->
                    try {
                        Crop(
                            words[1].toInt(),
                            words[2].toInt(),
                            words[3].toInt(),
                            words[4].toInt()
                        )   // convert a String to a number
                    } catch (e: Exception) {
                        println("Invalid crop format.  Usage: 'crop [x] [y] [w] [h]'")
                        NoOp
                    }
                "blend" ->
                    try {
                        Blend(
                            Image.loadResource(words[1]),
                            BlendMode.parse(words[2])
                        )
                    } catch (e: IOException) {
                        println("Invalid image.")
                        NoOp
                    } catch (e: Exception) {
                        println("Invalid blend format.  Usage: 'blend [path] [mode]'")
                        NoOp
                    }
                // C. Add invert and grayscale in the Transformation.parse method.
                // D. Test them out.
                "invert"    -> Invert
                "grayscale" -> Grayscale
                "sharpen"   -> KernelFilter(Kernel.sharpen)
                "blur"      -> KernelFilter(Kernel.blur)
                "edge"      -> KernelFilter(Kernel.edge)
                "emboss"    -> KernelFilter(Kernel.emboss)
                else        -> NoOp
            }
        }
    }
}

/*
    2. Create 3 subclasses of Transformation
        - Crop  - x,y,w,h as constructor args
        - Blend - fgImage, blendMode as constructor args
        - NoOp  - does nothing
        - stub the process method e.g. printing something
 */
class Crop(val x: Int, val y: Int, val w: Int, val h: Int): Transformation {
    override fun invoke(image: Image): Image =
        // image.crop(x,y,w,h) ?: image
        try {
            image.crop(x, y, w, h)!!   // will crash if the coordinates are out of bounds
        } catch (e: Exception) {
            println("Error: coordinates are out of bounds.  Max coordinates: ${image.width} x ${image.height}")
            image
        }
    // println("Cropping the image at some coordinates")
    // return image
}

class Blend(val fgImage: Image, val mode: BlendMode): Transformation {
    override fun invoke(bgImage: Image): Image {   // Blending two images
        // 1. make sure that the images have the exact same dimensions
        if (fgImage.width != bgImage.width || fgImage.height != bgImage.height) {
            println("Error: images don't have the same sizes: ${fgImage.width} x ${fgImage.height} vs ${bgImage.width} x ${bgImage.height}")
            return bgImage
        }

        val width  = fgImage.width
        val height = fgImage.height
        // 2. create a black image from those dimensions
        val result = Image.black(width, height)
        // 3. for every pixel in fgImage with every _corresponding_ pixel in bgImage,
        //      use the blendMode to combine the colors
        //      write that pixel in those coordinates in the result image
        for (x in 0..< width)
            for (y in 0..< height)
                result.setColor(
                    x,
                    y,
                    mode.combine(
                        fgImage.getColor(x,y),
                        bgImage.getColor(x,y)
                    )
                )
        // 4. return the image
        return result
    }
}

// D. Refactor them!
abstract class PixelTransformation(val pixelFun: (Color) -> Color): Transformation {
    override fun invoke(image: Image): Image {
        val width  = image.width
        val height = image.height
        val result = Image.black(width, height)
        for (x in 0..< width)
            for (y in 0 ..< height) {
                val originalColor = image.getColor(x, y)
                val newColor = pixelFun(originalColor)
                result.setColor(x, y, newColor)
            }

        return result
    }
}

// A. for every pixel, return a new pixel where the r/g/b values are 255-r/g/b of the original pixel
object Invert: PixelTransformation({ color ->
    Color(
        255 - color.red,
        255 - color.green,
        255 - color.blue
    )
})
//object Invert: Transformation {
//    override fun invoke(image: Image): Image {
//        val width = image.width
//        val height = image.height
//        val result = Image.black(width, height)
//        for (x in 0..< width)
//            for (y in 0 ..< height) {
//                val originalColor = image.getColor(x,y)
//                val newColor = Color(
//                    255 - originalColor.red,
//                    255 - originalColor.green,
//                    255 - originalColor.blue
//                    )
//                result.setColor(x,y,newColor)
//            }
//
//        return result
//    }
//}

// B. for every pixel, return a new pixel with r=g=b = the average of r/g/b of the original pixel
object Grayscale: PixelTransformation({ color ->
    val avg = (color.red + color.green + color.blue) / 3
    Color(avg, avg, avg)    // last expression is the value of the lambda
})
//object Grayscale: Transformation {
//    override fun invoke(image: Image): Image {
//        val width = image.width
//        val height = image.height
//        val result = Image.black(width, height)
//        for (x in 0..< width)
//            for (y in 0 ..< height) {
//                val originalColor = image.getColor(x,y)
//                val avg = (originalColor.red + originalColor.green + originalColor.blue) / 3
//                val newColor = Color(avg, avg, avg)
//                result.setColor(x,y,newColor)
//            }
//
//        return result
//    }
//}

object NoOp: Transformation {
    override fun invoke(image: Image): Image = image
}

// kernel transformation
// 1 - window
data class Window(val width: Int, val height: Int, val values: List<Color>)
data class Kernel(val width: Int, val height: Int, val values: List<Double>) {
    // property: all the values should sum up to 1.0

    fun normalize(): Kernel {
        val sum = values.sum()
        if (sum == 0.0) return this
        return Kernel(width, height, values.map { it / sum })
    }

    // 2 - window and kernel must have the same width x height
    // multiply every pixel with every CORRESPONDING double
    // [a, b, c] * [x, y, z] = [a * x, b * y, c * z]
    // sum up all the values to a single color = a * x + b * y + c * z
    // "convolution"
    operator fun times(window: Window): Color {
        if (width != window.width || height != window.height)
            throw IllegalArgumentException("Kernel and window must have the same dimensions")

        val r = window.values
            .map { it.red }
            .zip(values) { a, b -> a * b }
            .sum()
            .toInt()
        val g = window.values
            .map { it.green }
            .zip(values) { a, b -> a * b }
            .sum()
            .toInt()
        val b = window.values
            .map { it.blue }
            .zip(values) { a, b -> a * b }
            .sum()
            .toInt()

        return Color(r, g, b)
    }

    companion object {
        val sharpen = Kernel(3,3, listOf(
            0.0, -1.0, 0.0,
            -1.0, 5.0, -1.0,
            0.0, -1.0, 0.0
        )).normalize()

        val blur = Kernel(3,3, listOf(
            1.0, 2.0, 1.0,
            2.0, 4.0, 2.0,
            1.0, 2.0, 1.0
        )).normalize()

        val edge = Kernel(3,3, listOf(
            1.0, 0.0, -1.0,
            2.0, 0.0, -2.0,
            1.0, 0.0, -1.0
        ))

        val emboss = Kernel(3,3, listOf(
            -2.0, -1.0, 0.0,
            -1.0, 1.0, 1.0,
            0.0, 1.0, 2.0
        ))
    }
}

data class KernelFilter(val kernel: Kernel): Transformation {
    override fun invoke(image: Image): Image =
        Image.fromColors(
            image.width,
            image.height,
            (0..< image.height).flatMap { y ->
                (0..<image.width).map { x ->
                    kernel * image.window(x, y, kernel.width, kernel.height)
                }
            }
        )
    // 3
    // for every pixel in the image,
    //      create a Window(x, y, kernel.width, kernel.height)
    //      multiply the kernel with the window you just made -> returns a new pixel
    //      set the resulting pixel in the resulting image
}