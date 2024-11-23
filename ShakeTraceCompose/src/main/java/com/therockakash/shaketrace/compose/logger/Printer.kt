package com.therockakash.shaketrace.compose.logger


import android.util.Log
import okhttp3.Headers
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.EOFException
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * Created by akash on 7/20/2023.
 * Know more about author on <a href="https://akash.cloudemy.in">...</a>
 */
class Printer private constructor() {

    companion object {
        private lateinit var logFile: File

        private const val JSON_INDENT = 3
        private val LINE_SEPARATOR = System.getProperty("line.separator")
        private val DOUBLE_SEPARATOR = LINE_SEPARATOR + LINE_SEPARATOR
        private const val N = "\n"
        private const val T = "\t"
        private const val REQUEST_UP_LINE =
            "╔═══════╣ Request ║════════════════════════════════════════════════════════════════════════"
        private const val END_LINE =
            "╚════════════════════════════════════════════════════════════════════════════════════════╝"
        private const val RESPONSE_UP_LINE =
            "╔═══════╣ Response ║════════════════════════════════════════════════════════════════════════"
        private const val BODY_TAG = "Body:"
        private const val URL_TAG = "URL: "
        private const val METHOD_TAG = "Method: @"
        private const val HEADERS_TAG = "Headers:"
        private const val STATUS_CODE_TAG = "Status Code: "
        private const val RECEIVED_TAG = "Received in: "
        private const val DEFAULT_LINE = "║  "
        private val OOM_OMITTED = LINE_SEPARATOR + "Output omitted because of Object size."
        private fun isEmpty(line: String): Boolean {
            return line.isEmpty() || N == line || T == line || line.trim { it <= ' ' }.isEmpty()
        }

        fun printJsonRequest(
            builder: PrettyLoggingInterceptor.Builder,
            body: RequestBody?,
            url: String,
            header: Headers,
            method: String,
            dir: File
        ) {

            val requestBody = body?.let {
                LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + bodyToString(body, header)
            } ?: ""
            val reqBody = requestBody.split(LINE_SEPARATOR).toTypedArray()
            val reqHead = getRequest(builder.level, header, method)
            var log = "╔═╣ Request ║════════════════"
            var logd = "╔═╣ Request ║ $url ║════════════════════════════════════"

            log = "$log\n║ $url"
            for (line in reqBody)
                if (line.isNotBlank()) {
                    log = "$log\n║ $line"
                    logd = "$logd\n║ $line"
                }


            for (line in reqHead)
                if (line.isNotBlank()) {
                    logd = "$logd\n║ $line"
                }
            log = "$log\n╚════════════════════════════\n\n"
            logd =
                "$logd\n╚════════════════════════════════════════════════════════════════════════════════\n\n"
            Log.i("API Request", logd)
            appendLog(log, dir)


        }

        fun printJsonResponse(
            isSuccessful: Boolean,
            code: Int,
            response: Response,
            ms: Long,
            responseUrl: String,
            dir: File
        ) {

            val responseBody =
                LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + getResponseBody(response)

            val resBody = responseBody.split(LINE_SEPARATOR).toTypedArray()

            var log = "╔═╣ Response ║════════════════"
            var logd =
                "╔═╣ Response ║ $responseUrl ║ $ms ms | Code: $code | Success: $isSuccessful ║═══════════════════════"
            //  Log.w("API Response", "╔═╣ Response ║ $responseUrl ║ $ms ms | Code: $code | Success: $isSuccessful ║═══════════════════════")
            log = "$log\n║ $responseUrl"
            for (line in resBody) {
                if (line.isNotBlank()) {
                    log = "$log\n║ $line"
                    logd = "$logd\n║ $line"
                }
                //  Log.w("API Response", "║ $line")
            }
            //  Log.w("API Response", "╚═════════════════════════════════════════════════════════════════════════════════════════")
            log = "$log\n╚════════════════════════════\n\n"
            logd =
                "$logd\n╚═════════════════════════════════════════════════════════════════════════════════════════\n\n"

            printLog(logd)
            appendLog(log, dir)


        }

        private fun printLog(logd: String) {
            val maxLogSize = 2000
            var s = "";
            for (i in 0..logd.length / maxLogSize) {
                val start = i * maxLogSize
                var end = (i + 1) * maxLogSize
                end = if (end > logd.length) logd.length else end

                var str = s + logd.substring(start, end)
                if (i != logd.length / maxLogSize) {
                    s = str.substring(str.lastIndexOf("║"))
                    str = str.substring(0, str.lastIndexOf("║"))
                }
                Log.w("API Response", str)
            }
        }


        private fun getResponseBody(response: Response): String {
            val responseBody = response.body!!
            val headers = response.headers
            val contentLength = responseBody.contentLength()
            if (!response.promisesBody()) {
                return "End request - Promises Body"
            } else if (bodyHasUnknownEncoding(response.headers)) {
                return "encoded body omitted"
            } else {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                var buffer = source.buffer

                var gzippedLength: Long? = null
                if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                    gzippedLength = buffer.size
                    GzipSource(buffer.clone()).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }

                val contentType = responseBody.contentType()
                val charset: Charset = contentType?.charset(StandardCharsets.UTF_8)
                    ?: StandardCharsets.UTF_8

                if (!buffer.isProbablyUtf8()) {
                    return "End request - binary ${buffer.size}:byte body omitted"
                }

                if (contentLength != 0L) {
                    return getJsonString(buffer.clone().readString(charset))
                }

                return if (gzippedLength != null) {
                    "End request - ${buffer.size}:byte, $gzippedLength-gzipped-byte body"
                } else {
                    "End request - ${buffer.size}:byte body"
                }
            }
        }

        private fun getRequest(level: Level, headers: Headers, method: String): Array<String> {
            val log: String
            val loggableHeader = level == Level.HEADERS || level == Level.BASIC
            log = METHOD_TAG + method + DOUBLE_SEPARATOR +
                    if (isEmpty("$headers")) "" else if (loggableHeader) HEADERS_TAG + LINE_SEPARATOR + dotHeaders(
                        headers
                    ) else ""
            return log.split(LINE_SEPARATOR).toTypedArray()
        }

        private fun getResponse(
            headers: Headers, tookMs: Long, code: Int, isSuccessful: Boolean,
            level: Level, segments: List<String>, message: String
        ): Array<String> {
            val log: String
            val loggableHeader = level == Level.HEADERS || level == Level.BASIC
            val segmentString = slashSegments(segments)
            log = ((if (segmentString.isNotEmpty()) "$segmentString - " else "") + "[is success : "
                    + isSuccessful + "] - " + RECEIVED_TAG + tookMs + "ms" + DOUBLE_SEPARATOR + STATUS_CODE_TAG +
                    code + " / " + message + DOUBLE_SEPARATOR + when {
                isEmpty("$headers") -> ""
                loggableHeader -> HEADERS_TAG + LINE_SEPARATOR +
                        dotHeaders(headers)

                else -> ""
            })
            return log.split(LINE_SEPARATOR).toTypedArray()
        }

        private fun slashSegments(segments: List<String>): String {
            val segmentString = StringBuilder()
            for (segment in segments) {
                segmentString.append("/").append(segment)
            }
            return segmentString.toString()
        }

        private fun dotHeaders(headers: Headers): String {
            val builder = StringBuilder()
            headers.forEach { pair ->
                builder.append("${pair.first}: ${pair.second}").append(N)
            }
            return builder.dropLast(1).toString()
        }


        private fun appendLog(text: String?, dir: File) {

            if (dir== File("/data/user/0/com.therockakash.shaketrace/cache"))
            {
                Log.e("ShakeTrace", "Pass cashe dir path to view logs in app.")
                return
            }
                if (!this::logFile.isInitialized)
                    logFile = File(dir, "log.file")
                if (!logFile.exists())
                    logFile.createNewFile()


                if (text != null) {
                    logFile.appendText(text)
                    logFile.appendText("\n")
                }

        }

        private fun bodyToString(requestBody: RequestBody?, headers: Headers): String {
            return requestBody?.let {
                return try {
                    when {
                        bodyHasUnknownEncoding(headers) -> {
                            return "encoded body omitted)"
                        }

                        requestBody.isDuplex() -> {
                            return "duplex request body omitted"
                        }

                        requestBody.isOneShot() -> {
                            return "one-shot body omitted"
                        }

                        else -> {
                            val buffer = Buffer()
                            requestBody.writeTo(buffer)

                            val contentType = requestBody.contentType()
                            val charset: Charset = contentType?.charset(StandardCharsets.UTF_8)
                                ?: StandardCharsets.UTF_8

                            return if (buffer.isProbablyUtf8()) {
                                getJsonString(buffer.readString(charset)) + LINE_SEPARATOR + "${requestBody.contentLength()}-byte body"
                            } else {
                                "binary ${requestBody.contentLength()}-byte body omitted"
                            }
                        }
                    }
                } catch (e: IOException) {
                    "{\"err\": \"" + e.message + "\"}"
                }
            } ?: ""
        }

        private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
            val contentEncoding = headers["Content-Encoding"] ?: return false
            return !contentEncoding.equals("identity", ignoreCase = true) &&
                    !contentEncoding.equals("gzip", ignoreCase = true)
        }

        private fun getJsonString(msg: String): String {
            val message: String
            message = try {
                when {
                    msg.startsWith("{") -> {
                        val jsonObject = JSONObject(msg)
                        jsonObject.toString(JSON_INDENT)
                    }

                    msg.startsWith("[") -> {
                        val jsonArray = JSONArray(msg)
                        jsonArray.toString(JSON_INDENT)
                    }

                    else -> {
                        msg
                    }
                }
            } catch (e: JSONException) {
                msg
            } catch (e1: OutOfMemoryError) {
                OOM_OMITTED
            }
            return message
        }

        fun printFailed(tag: String, builder: PrettyLoggingInterceptor.Builder) {
            I.log(builder.type, tag, RESPONSE_UP_LINE, builder.isLogHackEnable)
            I.log(builder.type, tag, DEFAULT_LINE + "Response failed", builder.isLogHackEnable)
            I.log(builder.type, tag, END_LINE, builder.isLogHackEnable)
        }
    }

    init {
        throw UnsupportedOperationException()
    }
}


internal fun Buffer.isProbablyUtf8(): Boolean {
    try {
        val prefix = Buffer()
        val byteCount = size.coerceAtMost(64)
        copyTo(prefix, 0, byteCount)
        for (i in 0 until 16) {
            if (prefix.exhausted()) {
                break
            }
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                return false
            }
        }
        return true
    } catch (_: EOFException) {
        return false // Truncated UTF-8 sequence.
    }
}