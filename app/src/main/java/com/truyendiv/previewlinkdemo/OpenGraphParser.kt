package com.truyendiv.previewlinkdemo

import kotlinx.coroutines.*
import org.jsoup.Jsoup
import kotlin.coroutines.CoroutineContext

class OpenGraphParser : CoroutineScope {

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun parse(url: String, onSuccess: (OpenGraphResult) -> Unit) = launch(Dispatchers.IO) {
        val result = fetchContent(url)
        result?.let {
            withContext(Dispatchers.Main) {
                onSuccess(it)
            }
        }
    }

    private fun fetchContent(url: String): OpenGraphResult? {
        return try {
            val response = Jsoup.connect(url)
                .ignoreContentType(true)
                .userAgent(AGENT)
                .referrer(REFERRER)
                .timeout(TIMEOUT)
                .followRedirects(true)
                .execute()
            val doc = response.parse()
            val ogTags = doc.select(DOC_SELECT_QUERY)
            if (ogTags.isNotEmpty()) {
                OpenGraphResult().apply {
                    ogTags.forEachIndexed { index, _ ->
                        val tag = ogTags[index]
                        when (tag.attr(PROPERTY_KEY)) {
                            OG_IMAGE -> imageUrl = tag.attr(CONTENT_KEY)
                            OG_DESCRIPTION -> description = tag.attr(CONTENT_KEY)
                            OG_URL -> this.url = tag.attr(CONTENT_KEY)
                            OG_TITLE -> title = tag.attr(CONTENT_KEY)
                            OG_SITE_NAME -> siteName = tag.attr(CONTENT_KEY)
                            OG_TYPE -> type = tag.attr(CONTENT_KEY)
                        }
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val AGENT = "Mozilla"
        private const val REFERRER = "http://www.google.com"
        private const val TIMEOUT = 10000
        private const val DOC_SELECT_QUERY = "meta[property^=og:]"
        private const val CONTENT_KEY = "content"
        private const val PROPERTY_KEY = "property"
        private const val OG_IMAGE = "og:image"
        private const val OG_DESCRIPTION = "og:description"
        private const val OG_URL = "og:url"
        private const val OG_TITLE = "og:title"
        private const val OG_SITE_NAME = "og:site_name"
        private const val OG_TYPE = "og:type"
    }
}
