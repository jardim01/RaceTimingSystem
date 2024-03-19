import androidx.compose.ui.res.useResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineEvent
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun playSound(resourcePath: String) {
    withContext(Dispatchers.IO) {
        useResource(resourcePath) { inputStream ->
            // required after packaging
            val bufferedInputStream = BufferedInputStream(inputStream)
            val audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream)
            playSound(audioInputStream)
            audioInputStream.close()
        }
    }
}

suspend fun playSound(stream: AudioInputStream) {
    return suspendCoroutine { continuation ->
        try {
            val clip = AudioSystem.getClip()
            clip.open(stream)
            clip.addLineListener { event ->
                if (event.type == LineEvent.Type.STOP) {
                    clip.close()
                    continuation.resume(Unit)
                }
            }
            clip.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
