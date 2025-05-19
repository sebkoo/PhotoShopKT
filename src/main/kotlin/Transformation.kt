package com.rockthejvm.practice

import java.io.IOException

/*
    1. Create an interface Transformation with a single `process` method
        taking an Image and returning another Image
 */
interface Transformation {
    fun process(image: Image): Image

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
                else -> NoOp
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
    override fun process(image: Image): Image {
        // TODO - actually crop the image
        println("Cropping the image at some coordinates")
        return image
    }
}

class Blend(val fgImage: Image, val mode: BlendMode): Transformation {
    override fun process(bgImage: Image): Image {
        // TODO - actually blend the image
        println("Blending two images")
        return bgImage
    }
}

object NoOp: Transformation {
    override fun process(image: Image): Image = image
}