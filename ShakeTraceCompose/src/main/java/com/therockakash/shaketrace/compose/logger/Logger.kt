package com.therockakash.shaketrace.compose.logger

import okhttp3.internal.platform.Platform
import okhttp3.internal.platform.Platform.Companion.INFO

/**
 * Created by akash on 7/20/2023.
 * Know more about author on <a href="https://akash.cloudemy.in">...</a>
 */

interface Logger {
    fun log(level: Int = INFO, tag: String? = null, msg: String? = null)

    companion object {
        val DEFAULT: Logger = object : Logger {
            override fun log(level: Int, tag: String?, msg: String?) {
                Platform.get().log("$msg", level, null)
            }
        }
    }
}