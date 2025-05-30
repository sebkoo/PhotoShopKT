import java.awt.Dimension
import java.awt.Graphics
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants
import kotlin.system.exitProcess

// Java Swing
object App {
    private lateinit var frame: JFrame
    private lateinit var imagePanel: ImagePanel

    class ImagePanel(private var image: Image): JPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            // render the picture inside this "graphics"
            // g.drawImage(image.buffImage,0,0,null)   // TODO - exercise - make this work
            image.draw(g)
        }

        override fun getPreferredSize(): Dimension =
            Dimension(image.width, image.height)

//        private fun beautify() {
//            // should not be allowed
//            image.buffImage.setRGB(10,10,20,20,IntArray(400) { 0 }, 0, 20)
//        }

        fun replaceImage(newImage: Image) {
            image = newImage
            // by mistake
            revalidate()
            repaint()
        }

        fun getImage() = image
    }

    fun loadResource(path: String) {
        val image = Image.loadResource(path)
        if (!this::frame.isInitialized) {
            frame = JFrame("Kotlin Image App")
            imagePanel = ImagePanel(image)

            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.contentPane.add(imagePanel)
            frame.pack()
            frame.isVisible = true
        } else {
            imagePanel.replaceImage(image)
            frame.pack()    // resizes the window to the "preferred dimensions"
        }
    }
    // 4. Application main method.
    @JvmStatic
    fun main(args: Array<String>) {
//        loadResource("Sebastiano.jpg")
//        Thread.sleep(3000)
//        loadResource("cropped.jpg")
        val scanner = Scanner(System.`in`)
        while (true) {
            print("> ")
            val command = scanner.nextLine()
            val words = command.split(" ")
            val action = words[0]
            when (action) {
                "load" -> try { // load this path into the UI
                    loadResource(words[1])
                } catch (e: Exception) {
                    println("Error: cannot load image at path ${words[1]}.")
                }
                "save" ->       // save the current image in the window to this path
                    if (!this::frame.isInitialized)
                        println("Error: No image loaded.")
                    else
                        imagePanel.getImage().saveResource(words[1])
                "exit" -> exitProcess(0)
                else -> {
                    // 1. use Transformation.parse(command) -> a Transformation instance
                    val transformation = Transformation.parse(command)
                    // 2. run the transformation on the current image
                    val newImage = transformation.invoke(imagePanel.getImage())
                    // 3. Use imagePanel.replaceImage to replace the current image with the new image
                    imagePanel.replaceImage(newImage)
                    frame.pack()
                }
            }
        }
    }
}