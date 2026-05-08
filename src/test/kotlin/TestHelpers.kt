// Test helpers for use within test classes

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.client.plugins.cookies.HttpCookies
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

suspend fun ApplicationTestBuilder.authenticatedClient(): HttpClient {
    val client = createClient {
        install(HttpCookies)
    }

    client.post("/_test/sign-in") {
        setBody(
            listOf(
                "userID" to "1",
                "username" to "regulardude123"
            ).formUrlEncode()
        )
        headers {
            append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }
    }
    return client
}
