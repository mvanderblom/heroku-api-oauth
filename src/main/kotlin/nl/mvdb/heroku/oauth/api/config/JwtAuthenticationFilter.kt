package nl.mvdb.heroku.oauth.api.config

import net.minidev.json.JSONArray
import org.springframework.security.core.context.SecurityContextHolder

import javax.servlet.ServletException

import java.io.IOException

import javax.servlet.FilterChain

import javax.servlet.http.HttpServletResponse

import javax.servlet.http.HttpServletRequest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

import org.springframework.stereotype.Component

import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.reactive.function.client.WebClient

@Component
class JwtAuthenticationFilter : OncePerRequestFilter() {
    @Autowired
    private lateinit var webClient: WebClient

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null) {
            val token = authentication.principal as Jwt
            val roles = token.claims.get("http://heroku-api-oauth/roles") as JSONArray
            val auths = roles.map { SimpleGrantedAuthority(it as String) }
            authentication.authorities.addAll(auths)
            print(auths)
        }

//        val userInfo =  webClient.get()
//                .uri("userinfo")
//                .header(HttpHeaders.AUTHORIZATION, req.getHeader(HttpHeaders.AUTHORIZATION))
//                .retrieve()
//                .bodyToMono(UserInfo::class.java)
//        println(userInfo)
        chain.doFilter(req, res)
    }
}