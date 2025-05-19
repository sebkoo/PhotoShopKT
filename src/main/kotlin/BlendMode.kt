package com.rockthejvm.practice

interface BlendMode {
    // 1. Create a BlendMode interface with a single method to combine two colors (fg and bg) and return another.
    fun combine(fg: Color, bg: Color): Color    // foreground / background
    /*
     * 4. Create a `parse` method in the companion of BlendMode that takes a String arg and returns a BlendMode
     *  - if the string is "transparency" => Transparency with 0.5
     *  - "multiply" => Multiply
     *  - "screen" => Screen
     *  - anything else => NoBlend
     */
    companion object {
        fun parse(string: String): BlendMode = when(string) {
            "transparency" -> Transparency(0.5)
            "multiply"     -> Multiply
            "screen"       -> Screen
            else           -> NoBlend
        }
    }
}

/*  2. Create the following BlendMode types:
 *  - takes a factor f (Double between 0 and 1) as a constructor argument
 *  - the combine method has the following formula:
 *      result = fg * factor + bg * (1-factor)
 *      which applies on all channels (red, green, blue)
 *      Transparency(0.9).combine(white,black) == gray
 */
class Transparency(f: Double): BlendMode {
    val factor =
        if      (f <= 0.0) 0.0
        else if (f >= 1.0) 1.0
        else f

    override fun combine(fg: Color, bg: Color): Color =
        Color(
            (fg.red   * factor + bg.red   * (1 - factor)).toInt(),
            (fg.green * factor + bg.green * (1 - factor)).toInt(),
            (fg.blue  * factor + bg.blue  * (1 - factor)).toInt()
        )
}
// the combine method formula:
//  result = fg * bg / 255 on all channels
object Multiply: BlendMode {
    override fun combine(fg: Color, bg: Color): Color =
        Color(
            (fg.red   * bg.red   / 255.0).toInt(),
            (fg.green * bg.green / 255.0).toInt(),
            (fg.blue  * bg.blue  / 255.0).toInt()
        )
}
// the combine method formula is opposite to multiply:
//  result = 255 - (255 - fg) * (255 - bg) / 255
object Screen: BlendMode {
    override fun combine(fg: Color, bg: Color): Color =
        Color(
            (255 - (255 - fg.red  ) * (255 - bg.red  ) / 255.0).toInt(),
            (255 - (255 - fg.green) * (255 - bg.green) / 255.0).toInt(),
            (255 - (255 - fg.blue ) * (255 - bg.blue ) / 255.0).toInt()
        )
}
// combine just returns the foreground
object NoBlend: BlendMode {
    override fun combine(fg: Color, bg: Color): Color =
        fg
}

/*
    3. Test the blends
    - Transparency(0.5).combine(red,blue) should give you a dark magenta
    - create a Gray color with 128 on all channels.
    - Multiply(red, gray) should give you a dark red.
    - Multiply(red, blue) should give you exactly black!
    - Screen(red, gray) should give you a light red (pink-ish).
 */
object BlendPlayground {
    @JvmStatic
    fun main(args: Array<String>) {
        Transparency(0.5).combine(Color.RED, Color.BLUE).draw(100,100,"src/main/resources/darkmagenta.jpg")
        Multiply.combine(Color.RED, Color.GRAY).draw(100,100,"src/main/resources/darkred.jpg")
        Multiply.combine(Color.RED, Color.BLUE).draw(100,100,"src/main/resources/black.jpg")
        Screen.combine(Color.RED, Color.GRAY).draw(100,100,"src/main/resources/lightred.jpg")
        BlendMode.parse("scren").combine(Color.RED, Color.GRAY).draw(100,100,"src/main/resources/lightred.jpg")
    }
}