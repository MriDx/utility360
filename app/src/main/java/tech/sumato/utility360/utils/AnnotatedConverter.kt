package tech.sumato.utility360.utils

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class AnnotatedConverter() : Converter.Factory() {

    var factoryMap: Map<Class<*>, Converter.Factory>? = null

    constructor(factoryMap: Map<Class<*>, Converter.Factory>?) : this() {
        this.factoryMap = factoryMap
    }


    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        for (annotation in annotations) {
            val factory = factoryMap?.let { it -> it[annotation.annotationClass.java] }
            if (factory != null) {
                return factory.responseBodyConverter(type, annotations, retrofit)
            }
        }
        return null
    }

}