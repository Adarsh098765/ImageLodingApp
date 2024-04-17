package com.adarsh.imagelodingproject.utils

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?
) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }

    // Implement equals and hashCode for proper comparison
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Resource<*>) return false

        if (status != other.status) return false
        if (data != other.data) return false
        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + (message?.hashCode() ?: 0)
        return result
    }
}