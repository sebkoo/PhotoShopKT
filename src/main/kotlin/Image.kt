import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

// crop a picture
class Image(private val buffImage: BufferedImage) { // NEVER expose mutable state outside this class
    val width  = buffImage.width
    val height = buffImage.height

    fun getColor(x: Int, y: Int): Color =
        Color.fromHex(buffImage.getRGB(x, y))

    fun setColor(x: Int, y: Int, color: Color) =
        buffImage.setRGB(x, y, color.toInt())

    fun draw(g: Graphics) {
        g.drawImage(buffImage,0,0,null)
    }

    fun save(path: String) =
        ImageIO.write(buffImage,"JPG", File(path))

    fun saveResource(path: String) =
        save("src/main/resources/$path")

    /*
        1. check dimensions - return null if any dimension is invalid
        2. create a black image of width x height
        3. iterate through coordinates: x0 ..< x+w, y0 ..< y+h
            - use buffImage.getRGB to get a pixel from the original image
            - use resultImage.buffImage.setRGB to set a pixel in the result
            - calculate the coordinates
        4. return the result image
     */
    fun crop(x0: Int, y0: Int, w: Int, h: Int): Image? {
        // dimension checks
        if (x0 < 0 || y0 < 0 || x0     >= width || y0     >= height) return null
        if (w  < 0 || h  < 0 || x0 + w >= width || y0 + h >= height) return null

        // happy path
        val result = Image.black(w, h)
        for (x in x0..< (x0 + w))
            for (y in y0..< (y0 + h)) {
                val originalPixel = buffImage.getRGB(x,y)
                result.buffImage.setRGB(x - x0,y - y0, originalPixel)
            }
        return result
    }

    /*
       +-------------------------------------------------------+
       |                                                       |
       |           a1 a2 a3 a4 a5                              |
       |           b1 b2 b3 b4 b5                              |
    y >|           c1 c2 XX c4 c5                              |
       |           d1 d2 d3 d4 d5                              |
       |           e1 e2 e3 e4 e5                              |
       |                                                       |
       |                                                       |
       |                                                       |
       |                                                       |
       +-------------------------------------------------------+
                          ^
                          x
       Window(5,5, [a1,a2,a3,a4,a5, b1,b2,b3,b4,b5, c1,c2,XX,c4,c5, d1,d2,d3,d4,d5, e1,e2,e3,e4,e5])
    */
    fun window(centerX: Int, centerY: Int, width: Int, height: Int): Window {
        val offsetX = (width  - 1) / 2
        val offsetY = (height - 1) / 2
        val coordinatesH = ((centerX - offsetX)..(centerX + offsetX))
            .map { x ->
                if      (x <= 0) 0
                else if (x >= this.width) this.width - 1
                else x
            }
        val coordinatesV = ((centerY - offsetY)..(centerY + offsetY))
            .map { y ->
                if      (y <= 0) 0
                else if (y >= this.height) this.height - 1
                else y
            }
        val colors = coordinatesV
            .flatMap { y ->
                coordinatesH.map { x -> getColor(x,y) }
            }
        return Window(width, height, colors)
    }

    companion object {
        fun black(width: Int, height: Int): Image {
            val buffImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            val pixels = IntArray(width * height) { 0 }
            buffImage.setRGB(0,0, width, height, pixels,0, width)
            return Image(buffImage)
        }

        fun fromColors(width: Int, height: Int, colors: List<Color>): Image {
            val buffImage = BufferedImage(width, height,BufferedImage.TYPE_INT_RGB)
            val pixels = colors.map { it.toInt() }.toIntArray()
            buffImage.setRGB(0,0, width, height, pixels,0, width)
            return Image(buffImage)
        }

        fun load(path: String) =
            Image(ImageIO.read(File(path)))

        fun loadResource(path: String) =
            load("src/main/resources/$path")
    }
}

object ImagePlayground {
    @JvmStatic
    fun main(args: Array<String>) {
        Image.black(100,100).saveResource("black.jpg")
        Image.loadResource("Sebastiano.jpg").crop(480,180,350,180)?.saveResource("cropped.jpg")
    }
}