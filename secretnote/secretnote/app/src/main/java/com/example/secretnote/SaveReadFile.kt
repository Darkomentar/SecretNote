package com.example.seretnote
import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class SaveReadFile {

    public fun saveStringToFile(context: Context, fileName: String, data: String) {
        try {
            // Открытие файла для записи данных
            val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)

            // Запись данных в файл
            fileOutputStream.write(data.toByteArray())

            // Закрытие файла
            fileOutputStream.close()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public fun readFileFromInternalStorage(context: Context, fileName: String): String {
        var content = ""
        try {
            // Открытие файла для чтения данных
            val fileInputStream = context.openFileInput(fileName)

            // Чтение данных из файла в строку
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            content = bufferedReader.readText()

            // Закрытие потоков
            inputStreamReader.close()
            bufferedReader.close()
            fileInputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return content
    }
}