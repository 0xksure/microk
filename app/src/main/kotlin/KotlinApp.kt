package start

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.http.*


fun Application.module() {
routing {
    route("/hello",HttpMethod.Get){
        handle {
            val result = KotlinApp("hello").doExpensiveMath("you")
            result.onSuccess(
              {i: String -> call.respondText("Hello $i")})
        }
    }
}
}

class KotlinApp(initArg: String) {
    val prop = "this is a property $initArg".also(::println)

    init {
        println("First initializer block that prints $initArg")
    }

    fun doExpensiveMath(inpu: String): Result<String>{
        return Result.success("this is a result $inpu")
    }
}



fun main() {
    embeddedServer(Netty, 8080,module = Application::module).start(wait = true)
}