package tech.sumato.utility360.izzy_parser_wrapper

import com.undabot.izzy.isCollection
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.JsonDocument
import com.undabot.izzy.rawType
import com.undabot.izzy.retrofit.IzzyInvalidJsonResponseBodyConverter
import com.undabot.izzy.retrofit.IzzyRetrofitConverter
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class MyIzzyConverter(val izzy: MyIzzy) : Converter.Factory() {

    private val jsonDocumentType = JsonDocument<Any>()::class.java.rawType

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? =
        when (getJsonType(type)) {
            IzzyRetrofitConverter.JsonType.COLLECTION -> MyIzzyCollectionResponseBodyConverter<IzzyResource>(
                izzy
            )
            IzzyRetrofitConverter.JsonType.SINGLE -> MyIzzyResponseBodyConverter<IzzyResource>(
                izzy
            )
            IzzyRetrofitConverter.JsonType.UNKNOWN -> IzzyInvalidJsonResponseBodyConverter()
        }


    override fun stringConverter(
        type: Type?,
        annotations: Array<out Annotation>?,
        retrofit: Retrofit?
    ): Converter<*, String>? = null


    private fun getJsonType(type: Type): IzzyRetrofitConverter.JsonType =
        if (isJsonDocument(type)) {
            val isJsonCollection = (type as ParameterizedType).actualTypeArguments[0].isCollection()
            if (isJsonCollection) IzzyRetrofitConverter.JsonType.COLLECTION else IzzyRetrofitConverter.JsonType.SINGLE
        } else {
            IzzyRetrofitConverter.JsonType.UNKNOWN
        }

    private fun isJsonDocument(type: Type): Boolean = type.rawType == jsonDocumentType

}