# Kotlin PhotoShop 🎨

A lightweight, command-line image processing application built with Kotlin and Java Swing. This project provides a simple yet powerful interface for applying various image transformations, filters, and blend modes.

## 🌟 Features

### Image Transformations
- **Crop** - Extract specific regions from images
- **Invert** - Create negative images by inverting colors
- **Grayscale** - Convert colored images to grayscale

### Blend Modes
- **Transparency** - Blend images with adjustable opacity
- **Multiply** - Darken images by multiplying color values
- **Screen** - Lighten images using screen blend algorithm
- **NoBlend** - Use foreground image only

### Kernel Filters
- **Sharpen** - Enhance image details and edges
- **Blur** - Apply Gaussian blur for smoothing
- **Edge Detection** - Highlight edges using Sobel operator
- **Emboss** - Create 3D relief effect

### Core Capabilities
- Real-time preview with Java Swing GUI
- Command-line interface for all operations
- Support for common image formats (JPG, PNG)
- Color manipulation with RGB channels
- Window-based convolution operations

## 📋 Prerequisites

- JDK 11 or higher
- Kotlin 1.5 or higher
- Gradle or Maven (for dependency management)

## 🚀 Getting Started

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/kotlin-photoshop.git
cd kotlin-photoshop
```

2. Build the project:
```bash
./gradlew build
```

3. Run the application:
```bash
./gradlew run
```

Or compile and run directly:
```bash
kotlinc -cp . *.kt -include-runtime -d photoshop.jar
java -jar photoshop.jar
```

## 💻 Usage

The application runs in interactive command-line mode with a GUI preview window.

### Basic Commands

```bash
# Load an image
> load image.jpg

# Save the current image
> save output.jpg

# Exit the application
> exit
```

### Transformations

```bash
# Crop image (x, y, width, height)
> crop 100 100 300 200

# Invert colors
> invert

# Convert to grayscale
> grayscale

# Apply filters
> sharpen
> blur
> edge
> emboss
```

### Blending Images

```bash
# Blend with another image using transparency
> blend overlay.jpg transparency

# Multiply blend mode
> blend texture.jpg multiply

# Screen blend mode
> blend light.jpg screen
```

## 📁 Project Structure

```
kotlin-photoshop/
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/rockthejvm/practice/photoShop/
│       │       ├── App.kt              # Main application and UI
│       │       ├── Color.kt            # Color representation
│       │       ├── Image.kt            # Core image operations
│       │       ├── BlendMode.kt        # Blend mode implementations
│       │       └── Transformation.kt   # Image transformations
│       └── resources/                  # Sample images and outputs
└── README.md
```

## 🏗️ Architecture

### Core Components

1. **Color Class**
   - RGB color representation
   - Color clamping (0-255)
   - Hex color conversion
   - Predefined color constants

2. **Image Class**
   - Wrapper around BufferedImage
   - Pixel manipulation methods
   - Window extraction for convolution
   - Load/save functionality

3. **Transformation Interface**
   - Strategy pattern for image processing
   - Composable transformations
   - String parsing for commands

4. **BlendMode Interface**
   - Various blending algorithms
   - Color combination logic
   - Configurable blend factors

### Design Patterns

- **Strategy Pattern**: Transformations and blend modes
- **Command Pattern**: Parse and execute user commands
- **Factory Pattern**: Create transformations from strings
- **Immutability**: Color and kernel objects are immutable

## 🧪 Examples

### Creating Custom Transformations

```kotlin
// Custom pixel transformation
object Sepia : PixelTransformation({ color ->
    val tr = (0.393 * color.red + 0.769 * color.green + 0.189 * color.blue).toInt()
    val tg = (0.349 * color.red + 0.686 * color.green + 0.168 * color.blue).toInt()
    val tb = (0.272 * color.red + 0.534 * color.green + 0.131 * color.blue).toInt()
    Color(tr, tg, tb)
})
```

### Adding Custom Kernels

```kotlin
// Custom kernel for motion blur
val motionBlur = Kernel(5, 5, listOf(
    1.0, 0.0, 0.0, 0.0, 0.0,
    0.0, 1.0, 0.0, 0.0, 0.0,
    0.0, 0.0, 1.0, 0.0, 0.0,
    0.0, 0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, 0.0, 1.0
)).normalize()
```

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Areas for Contribution

- Additional blend modes (Overlay, Soft Light, etc.)
- More kernel filters (Gaussian blur variants, custom kernels)
- Support for more image formats
- Performance optimizations
- Unit tests
- Batch processing capabilities
- Undo/redo functionality

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built as part of Rock the JVM Kotlin practice exercises
- Inspired by professional image editing software
- Uses Java Swing for cross-platform GUI support

## 📞 Contact

For questions or suggestions, please open an issue on GitHub or contact the maintainers.

---

**Note**: This is an educational project demonstrating Kotlin programming concepts including object-oriented design, functional programming, and image processing algorithms.
