package nl.mvdb.heroku.oauth.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.*
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*


@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Value("\${auth0.audience}")
    private val audience: String? = null

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private val issuer: String? = null

    override fun configure(http: HttpSecurity?) {
        http {
            authorizeRequests {
                authorize("/api/public", permitAll)
                authorize("/api/private", authenticated)
                authorize("/api/private-scoped", hasAnyAuthority("SCOPE_read:private_resource"))
            }
            oauth2ResourceServer {
                jwt {  }
            }
            cors {  }
        }
    }


    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = Arrays.asList("https://heroku-react-oauth.herokuapp.com")
        configuration.allowedMethods = Arrays.asList("GET", "POST")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer) as NimbusJwtDecoder
        val audienceValidator: OAuth2TokenValidator<Jwt> = AudienceValidator(audience!!)
        val withIssuer = JwtValidators.createDefaultWithIssuer(issuer!!)
        val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)
        jwtDecoder.setJwtValidator(withAudience)
        return jwtDecoder
    }
}


class AudienceValidator(val targetAudience: String): OAuth2TokenValidator<Jwt> {

    override fun validate(optionalToken: Jwt?): OAuth2TokenValidatorResult {
        val error = OAuth2Error("invalid_token", "The required audience is missing", null)

        return optionalToken
                 ?.let { token -> token.audience }
                 ?.let { audiences ->
                     audiences.contains(targetAudience)
                     OAuth2TokenValidatorResult.success()
                }?: OAuth2TokenValidatorResult.failure(error)
    }

}