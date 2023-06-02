import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import java.io.File
import java.io.InputStream

fun main() {
    val inputStream: InputStream = File("cod_images.txt").inputStream()
    val urls = mutableListOf<String>()

    inputStream.bufferedReader().forEachLine { urls.add(it) }

    secondTask(urls)
}

fun firstTask(urls: List<String>){
    val client = HttpClient(CIO)
        //Способ 4 из презы
    runBlocking {
        val images = urls.map { url ->
            async {
                client.get(url).body<ByteArray>()
            }
        }.map { it.await() }
        images.forEachIndexed { index, image ->
            withContext(Dispatchers.IO) {
                File("images/FirstTask/image${index + 1}.jpg").writeBytes(image)
            }
        }
    }
}

fun secondTask(urls: List<String>){
    val client = HttpClient(CIO)
        //Способ 5 из презы (сохранение названия картинки)
    runBlocking {
        val deferred = urls.map { url ->
            async {
                val imageBytes = client.get(url).body<ByteArray>()
                saveImageToFile(url, imageBytes)
            }
        }
        deferred.forEach { it.await() }
    }
    client.close()
}

fun saveImageToFile(url: String, imageBytes: ByteArray) {
    val filename = "images/SecondTask/" + url.substringAfterLast("/")
    val file = File(if (filename.endsWith(".jpg")) filename else filename + ".jpg")
    file.writeBytes(imageBytes)
}

