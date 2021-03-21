package nl.mvdb.heroku.oauth.api.web

import nl.mvdb.heroku.oauth.api.config.UserInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import org.springframework.web.bind.annotation.RequestMethod

import org.springframework.security.access.prepost.PreAuthorize




@RestController()
@RequestMapping("/api")
class HelloWorldController {

    @Autowired
    private lateinit var client: WebClient

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

    @PreAuthorize("hasRole('Visitor')")
    @GetMapping("/visitor-private")
    fun userPing(): HelloWorldResponse? {
        return HelloWorldResponse(hello = "visitor world")
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/admin-private")
    fun adminPing(): HelloWorldResponse? {
        return HelloWorldResponse(hello = "admin world")
    }

    @GetMapping("/user")
    fun userInfo(token: JwtAuthenticationToken): Mono<UserInfo> {
        return client.get()
                .uri("userinfo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${token.token.tokenValue}")
                .retrieve()
                .bodyToMono(UserInfo::class.java)
    }

}


data class HelloWorldResponse(val hello: String)

