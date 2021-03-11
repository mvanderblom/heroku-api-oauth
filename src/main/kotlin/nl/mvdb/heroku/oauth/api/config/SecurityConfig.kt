package nl.mvdb.heroku.oauth.api.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.function.client.WebClient


@EnableWebSecurity(debug = true)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var securityProps: SecurityProps

    override fun configure(http: HttpSecurity?) {
        http {
            cors {  }
            authorizeRequests {
                authorize("/api/public", permitAll)
                authorize("/api/private", authenticated)
                authorize("/api/private-scoped", hasAnyAuthority("SCOPE_read:private_resource"))
            }
            oauth2ResourceServer {
                jwt {  }
            }
        }
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = this.securityProps.auth0.allowedOrigins
        configuration.addAllowedHeader("authorization")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val audienceValidator: OAuth2TokenValidator<Jwt> = AudienceValidator(this.securityProps.auth0.audience)
        val withIssuer = JwtValidators.createDefaultWithIssuer(this.securityProps.auth0.issuerUri)
        val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)

        val jwtDecoder = JwtDecoders.fromOidcIssuerLocation(this.securityProps.auth0.issuerUri) as NimbusJwtDecoder
        jwtDecoder.setJwtValidator(withAudience)
        return jwtDecoder
    }

    @Bean
    fun client() = WebClient.builder()
            .baseUrl(this.securityProps.auth0.issuerUri)
            .build()

}


