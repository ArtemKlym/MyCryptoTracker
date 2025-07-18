package com.artemklymenko.mycryptotracker.core.data.networking

import com.artemklymenko.mycryptotracker.core.domain.util.NetworkError
import com.artemklymenko.mycryptotracker.core.domain.util.Result
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.channels.UnresolvedAddressException

class NetworkUnitTest {

    @Test
    fun `should return Success when response is in 200 range`() = runBlocking {
        val mockResponse = mockk<HttpResponse>()

        coEvery { mockResponse.status } returns HttpStatusCode.OK
        coEvery { mockResponse.body<String>() } returns "Success Response"

        val result = responseToResult<String>(mockResponse)

        assertEquals(Result.Success("Success Response"), result)
    }

    @Test
    fun `should return SerializationError when body transformation fails`() = runBlocking {
        val result = safeCall<String> { throw SerializationException() }
        assertEquals(Result.Error(NetworkError.SERIALIZATION_ERROR), result)
    }

    @Test
    fun `should return RequestTimeout for 408 status`() = runBlocking {
        val mockResponse = mockk<HttpResponse> {
            every { status } returns HttpStatusCode.RequestTimeout
        }
        val result = responseToResult<String>(mockResponse)
        assertEquals(Result.Error(NetworkError.REQUEST_TIMEOUT), result)
    }

    @Test
    fun `should return TooManyRequests for 429 status`() = runBlocking {
        val mockResponse = mockk<HttpResponse> {
            every { status } returns HttpStatusCode(429, "Too Many Requests")
        }
        val result = responseToResult<String>(mockResponse)
        assertEquals(Result.Error(NetworkError.TOO_MANY_REQUESTS), result)
    }

    @Test
    fun `should return ServerError for 500-599 status range`() = runBlocking {
        val mockResponse = mockk<HttpResponse> {
            every { status } returns HttpStatusCode.InternalServerError
        }
        val result = responseToResult<String>(mockResponse)
        assertEquals(Result.Error(NetworkError.SERVER_ERROR), result)
    }

    @Test
    fun `should return Unknown for other statuses`() = runBlocking {
        val mockResponse = mockk<HttpResponse> {
            every { status } returns HttpStatusCode(418, "Matrix is attacking")
        }
        val result = responseToResult<String>(mockResponse)
        assertEquals(Result.Error(NetworkError.UNKNOWN), result)
    }

    @Test
    fun `should return NoInternet status`() = runBlocking {
        val result = safeCall<String> {
            throw UnresolvedAddressException()
        }
        assertEquals(Result.Error(NetworkError.NO_INTERNET), result)
    }

    @Test
    fun constructUrl() {
        val url1 = "/assets"
        val url2 = "assets"
        val url3 = "https://api.coincap.io/v2/"
        val result1 = constructUrl(url1)
        val result2 = constructUrl(url2)
        val result3 = constructUrl(url3)
        assertEquals("https://api.coincap.io/v2/assets", result1)
        assertEquals("https://api.coincap.io/v2/assets", result2)
        assertEquals("https://api.coincap.io/v2/", result3)
    }
}