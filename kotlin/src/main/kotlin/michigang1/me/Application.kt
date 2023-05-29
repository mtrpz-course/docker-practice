package michigang1.me

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import michigang1.me.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
