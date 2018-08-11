package com.anemonesdk.general.storage.file

import com.anemonesdk.general.promise.Promise
import com.anemonesdk.general.storage.IFileStorage
import java.io.*

/**
 * Created by agarcia on 09/02/2017.
 */

class FileStorage(private val directory: String) : IFileStorage {

    @Throws(IOException::class)
    override fun writeData(data: ByteArray, file: String) {
        try {
            synchronized(this) {
                val stream = FileOutputStream(getFile(file))
                stream.write(data)
                stream.flush()
                stream.close()
            }
        } catch (e: FileNotFoundException) {
            throw IOException(e)
        }
    }

    override fun readData(file: String): ByteArray? {
        try {
            synchronized(this) {
                val stream = FileInputStream(getFile(file))
                val data = stream.readBytes()
                stream.close()
                return data
            }
        } catch (e: Exception) {
            return null
        }
    }

    private fun getFile(name: String): File = File(directory, name)

    override fun writeDataAsync(data: ByteArray, file: String): Promise<Unit> =
            Promise(executeInBackground = true) { fulfill, _ ->
                writeData(data, file)
                fulfill(Unit)
            }

    override fun readDataAsync(file: String): Promise<ByteArray> =
            Promise(executeInBackground = true) { fulfill, reject ->
                val data = readData(file)
                if (data != null) {
                    fulfill(data)
                } else {
                    reject(IOException())
                }
            }

}
