/*
 * MIT License
 *
 * Copyright (c) 2022 NSTDA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nstda.hii.webservice.app.webcache

import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import jakarta.ws.rs.container.ResourceInfo
import jakarta.ws.rs.core.CacheControl
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.ext.Provider

@Provider
class CacheFilter : ContainerResponseFilter {
    @Context
    private lateinit var resourceInfo: ResourceInfo

    override fun filter(requestContext: ContainerRequestContext, responseContext: ContainerResponseContext) {
        if (!isCacheable(responseContext)) {
            responseContext.headers.add("Cache-Control", "max-age=0")
            return
        }
        val cache: Cache? = resourceInfo.resourceMethod.getAnnotation(Cache::class.java)
            ?: resourceInfo.resourceClass.getAnnotation(Cache::class.java)

        if (cache != null) {
            responseContext.headers.add(
                "Cache-Control",
                cache.toCacheControl().combineWith(requestContext.cacheControl).toString()
            )
        }
    }

    private fun isCacheable(responseContext: ContainerResponseContext): Boolean {
        val status = responseContext.status
        return status in 200..299 || status == 304
    }
}

private val ContainerRequestContext.cacheControl: CacheControl
    get() {
        val header = headers["Cache-Control"]
        return try {
            if (header != null) {
                CacheControl.valueOf(header.first())
            } else {
                CacheControl().apply { isNoTransform = false }
            }
        } catch (illegal: IllegalArgumentException) {
            CacheControl().apply { isNoTransform = false }
        }
    }
