package com.athena.skripsi

import android.content.Context
import android.net.Uri
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiClient(private val context: Context) {

    private val BASE_URL = "API"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun scan(
        uri: Uri,
        fileName: String,
        onSuccess: (ScanResult) -> Unit,
        onError: (String) -> Unit
    ) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return onError("Cannot read file")

        val fileBytes = inputStream.readBytes()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                fileName,
                RequestBody.create(
                    "application/vnd.android.package-archive".toMediaType(),
                    fileBytes
                )
            )
            .build()

        val request = Request.Builder()
            .url("$BASE_URL/scan")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError("Connection failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (!response.isSuccessful || body == null) {
                    onError("Server error: ${response.code}")
                    return
                }

                try {
                    val json = JSONObject(body)
                    val result = ScanResult(
                        isMalware = json.getBoolean("is_malware"),
                        confidence = "${json.getDouble("confidence")}%",
                        threatType = json.getString("threat_type")
                    )
                    onSuccess(result)
                } catch (e: Exception) {
                    onError("Failed to parse response")
                }
            }
        })
    }

    data class ScanResult(
        val isMalware: Boolean,
        val confidence: String,
        val threatType: String
    )
}