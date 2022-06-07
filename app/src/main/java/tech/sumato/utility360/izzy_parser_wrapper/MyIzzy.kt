package tech.sumato.utility360.izzy_parser_wrapper

import android.util.Log
import com.undabot.izzy.models.Errors
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.JsonDocument
import com.undabot.izzy.parser.*

class MyIzzy(private val izzyJsonParser: IzzyJsonParser) {


    private val deserializeLinks = DeserializeLinks()
    private val customDeserializeLinks = CustomDeserializeLinks()
    private val validateJsonDocument = ValidateJsonDocument()
    private val deserializeErrors = DeserializeErrors()
    private val deserializeMeta = DeserializeMeta()
    //private val deserializeData = DeserializeData(izzyJsonParser)
    private val customDeserializeData = CustomDeserializeData(izzyJsonParser)


    fun <T : IzzyResource> deserializeToDocument(json: String): JsonDocument<T> {
        val jsonTree = izzyJsonParser.parseToJsonElements(json)
        validate(jsonTree)

        var resource: T? = null
        var errors: Errors? = null

        if (jsonTree.has(DATA)) {
            //resource = deserializeData.forSingleResource(jsonTree)
            resource = customDeserializeData.forSingleResource(jsonTree)
        } else if (jsonTree.has(ERRORS)) {
            errors = deserializeErrors.from(jsonTree)
        }

        val links = customLinksFrom(jsonTree)
        //val links = linksFrom(jsonTree)

        return JsonDocument(
            data = resource,
            links = links,
            errors = errors,
            meta = metaFrom(jsonTree)
        )
    }

    fun <T : IzzyResource> deserializeToCollection(json: String): JsonDocument<List<T>> {
        val jsonTree = izzyJsonParser.parseToJsonElements(json)
        validate(jsonTree)

        var resourceCollection: List<T>? = null
        var errors: Errors? = null

        if (jsonTree.has(DATA)) {
            //resourceCollection = deserializeData.forResourceCollection(jsonTree)
            resourceCollection = customDeserializeData.forResourceCollection(jsonTree)
        } else if (jsonTree.has(ERRORS)) {
            errors = deserializeErrors.from(jsonTree)
        }

        val links = customLinksFrom(jsonTree)
        //val links = linksFrom(jsonTree)

        return JsonDocument(
            data = resourceCollection,
            links = links,
            errors = errors,
            meta = metaFrom(jsonTree)
        )
    }

    fun <T : IzzyResource> serializeItem(item: T): String {
        val document = JsonDocument(
            ResourceToSerializableDocumentMapper(RelationshipFieldMapper()).mapFrom(item)
        )
        return izzyJsonParser.documentToJson(document).replace(nullableField(), nullValue())
    }


    fun <T : IzzyResource> serializeItemCollection(item: List<T>): String {
        val mapper = ResourceToSerializableDocumentMapper(RelationshipFieldMapper())
        val document = JsonDocument(item.map { mapper.mapFrom(it) })
        return izzyJsonParser.documentCollectionToJson(document)
            .replace(nullableField(), nullValue())
    }

    @SuppressWarnings("FunctionOnlyReturningConstant")
    private fun nullValue(): String = "null"

    private fun nullableField() = "\"" + DataWrapper.NULLABLE_FIELD + "\""

    private fun linksFrom(jsonTree: JsonElements) = deserializeLinks.from(jsonTree)

    private fun customLinksFrom(jsonTree: JsonElements) = customDeserializeLinks.from(jsonTree)

    private fun metaFrom(jsonTree: JsonElements) = deserializeMeta.fromRoot(jsonTree)

    private fun validate(jsonTree: JsonElements) = validateJsonDocument.from(jsonTree)


}