package michigang1.me.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import michigang1.me.features.helloWorldJson

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
    helloWorldJson()
}
