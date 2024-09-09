package com.ivancaez.auth.services

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@Service
class FileStorageService {

    private val uploadDir = Paths.get("uploads/")

    init {
        Files.createDirectories(uploadDir)
    }

    /**
     * Assigns a random UUID + the original filename to the file and saves it in the uploads directory.
     * @param file the file to save.
     * @return the path to the saved file.
     */
    fun saveFile(file: MultipartFile): String {
        val fileName = UUID.randomUUID().toString() + "_" + file.originalFilename
        val filePath = uploadDir.resolve(fileName)

        Files.copy(file.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)

        return filePath.toString()
    }

    /**
     * Deletes a file from the filesystem.
     * @param filePath the path to the file to delete.
     */
    fun deleteFile(filePath: String) {
        try {
            val path = Paths.get(filePath)
            Files.deleteIfExists(path)
        } catch (e: IOException) {
            println("Error deleting file: $filePath, error: ${e.message}")
        }
    }
}