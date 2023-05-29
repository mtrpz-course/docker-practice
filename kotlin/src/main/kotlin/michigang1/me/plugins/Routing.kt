package michigang1.me.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import michigang1.me.features.helloWorldRouting

fun Application.configureRouting() {
    helloWorldRouting()
}
