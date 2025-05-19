import com.rockthejvm.practice.Image
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants

// Java Swing
object UI {
    private lateinit var frame: JFrame
    private lateinit var imagePanel: ImagePanel

    class ImagePanel(private var image: Image): JPanel() {
        override fun paintComponents(g: Graphics) {
            super.paintComponents(g)
            // render the picture inside this "graphics"
            // g.drawImage(image.buffImage,0,0,null)
            image.draw(g)
        }

        override fun getPreferredSize(): Dimension =
            Dimension(image.width, image.height)

//        private fun beautify() {
//            // should not be allowed
//            image.buffImage.setRGB(10,10,20,20,IntArray(400) { 0 },0,20)
//        }

        fun replaceImage(newImage: Image) {
            image = newImage
            // by mistake
            revalidate()
            repaint()
        }
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

    @JvmStatic
    fun main(args: Array<String>) {
        loadResource("Sebastiano.jpg")
        Thread.sleep(3000)
        loadResource("cropped.jpg")
    }
}