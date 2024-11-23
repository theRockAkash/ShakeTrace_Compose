package com.therockakash.shaketrace.compose.logger


import okhttp3.Request
import java.io.IOException

/**
 * Created by akash on 7/20/2023.
 * Know more about author on <a href="https://akash.cloudemy.in">...</a>
 */
interface BufferListener {
    @Throws(IOException::class)
    fun getJsonResponse(request: Request?): String?
}