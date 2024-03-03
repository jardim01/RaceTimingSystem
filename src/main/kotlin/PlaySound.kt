import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineEvent
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun playSound(file: File) {
    withContext(Dispatchers.IO) {
        val audioInputStream = AudioSystem.getAudioInputStream(file)
        playSound(audioInputStream)
        audioInputStream.close()
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
