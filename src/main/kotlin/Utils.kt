import java.io.File

fun getResource(resourcePath: String): File? {
    // Get the class loader
    val classLoader = object {}.javaClass.classLoader
    // Get the resource URL
    val resourceUrl = classLoader.getResource(resourcePath)
    // Check if the resource exists
    return if (resourceUrl != null) File(resourceUrl.file) else null
}
