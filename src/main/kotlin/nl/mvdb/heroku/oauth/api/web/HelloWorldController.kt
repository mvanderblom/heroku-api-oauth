package nl.mvdb.heroku.oauth.api.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class HelloWorldResponse (val hello: String)

@RestController()
@RequestMapping("/api")
class HelloWorldController {

    @GetMapping("/public")
    fun publicHello(): HelloWorldResponse {
        return HelloWorldResponse(hello = "world")
    }

    @GetMapping("/private")
    fun privateHello(): HelloWorldResponse {
        return HelloWorldResponse(hello = "private world")
    }

    @GetMapping("/private-scoped")
    fun privateScopedHello(): HelloWorldResponse {
        return HelloWorldResponse(hello = "private scoped world")
    }
}