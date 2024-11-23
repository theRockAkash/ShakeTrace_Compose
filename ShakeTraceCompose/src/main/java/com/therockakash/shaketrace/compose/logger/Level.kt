package com.therockakash.shaketrace.compose.logger

/**
 * Created by akash on 7/20/2023.
 * Know more about author on <a href="https://akash.cloudemy.in">...</a>
 */
enum class Level {
    /**
     * No logs.
     */
    NONE,

    /**
     *
     * Example:
     * <pre>`- URL
     * - Method
     * - Headers
     * - Body
    `</pre> *
     */
    BASIC,

    /**
     *
     * Example:
     * <pre>`- URL
     * - Method
     * - Headers
    `</pre> *
     */
    HEADERS,

    /**
     *
     * Example:
     * <pre>`- URL
     * - Method
     * - Body
    `</pre> *
     */
    BODY
}