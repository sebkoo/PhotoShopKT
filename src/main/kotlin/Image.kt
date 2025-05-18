package com.rockthejvm.practice

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

// crop a picture
class Image(val buffImage: BufferedImage) {
    val width = buffImage.width
    val height = buffImage.height

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

    companion object {
        fun black(width: Int, height: Int): Image {
            val buffImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            val pixels = IntArray(width * height) { 0 }
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