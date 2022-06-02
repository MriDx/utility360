package tech.sumato.utility360.izzy_parser_wrapper

import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.JsonDocument
import okhttp3.ResponseBody
import retrofit2.Converter

class MyIzzyCollectionResponseBodyConverter<T : IzzyResource>(val izzy: MyIzzy) :
    Converter<ResponseBody, JsonDocument<List<T>>> {
    override fun convert(value: ResponseBody): JsonDocument<List<T>>? =
        izzy.deserializeToCollection(value.charStream().readText())
}