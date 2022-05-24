package tech.sumato.utility360.data.local.entity.utils

interface JsonOperation {


    fun <T> toJson(): T

    fun <T, K> fromJson(source: T): K

}