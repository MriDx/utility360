package tech.sumato.utility360.izzy_parser_wrapper

import com.undabot.izzy.models.Links
import com.undabot.izzy.parser.DeserializeLink
import com.undabot.izzy.parser.JsonElements
import com.undabot.izzy.parser.LINKS

class CustomDeserializeLinks(private val deserializeLink: DeserializeLink = DeserializeLink()) {

    fun from(jsonElements: JsonElements): Links? =
        when (hasLinks(jsonElements)) {
            //true -> parseLinksFrom(jsonElements.jsonElement(LINKS))
            true -> parseLinksFrom(jsonElements)
            false -> null
        }

    private fun parseLinksFrom(jsonElements: JsonElements) = Links(
        self = linkFrom(jsonElements, "self"),
        first = linkFrom(jsonElements, "first"),
        last = linkFrom(jsonElements, "last"),
        prev = linkFrom(jsonElements, "prev"),
        next = linkFrom(jsonElements, "next"),
        related = linkFrom(jsonElements, "related")
    )

    private fun hasLinks(jsonElements: JsonElements) =
        jsonElements.has(LINKS) && jsonElements.hasNonNull(LINKS)

    /*private fun linkFrom(jsonElements: JsonElements, forKey: String) =
        deserializeLink.from(jsonElements.jsonElement(forKey))*/

    private fun linkFrom(jsonElements: JsonElements, forKey: String) =
        deserializeLink.from(jsonElements.jsonElement(LINKS).jsonElement(forKey))
}