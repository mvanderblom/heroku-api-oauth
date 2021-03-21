package nl.mvdb.heroku.oauth.api.config

import net.minidev.json.JSONArray
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class CustomJwtAuthConverter : Converter<Jwt, Collection<GrantedAuthority>> {
    override fun convert(jwt: Jwt): Collection<GrantedAuthority>? {
        val permissions = jwt.getClaim<String>("scope").split(" ")
                .map { SimpleGrantedAuthority(it) }
        val roles = jwt.claims.get("http://heroku-api-oauth/roles") as JSONArray
        val authorities = roles
                .map { SimpleGrantedAuthority(it as String) }
                .union(permissions)
        println(authorities)
        return authorities
    }
}