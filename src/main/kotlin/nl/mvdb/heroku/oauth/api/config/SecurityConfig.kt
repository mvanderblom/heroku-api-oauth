package nl.mvdb.heroku.oauth.api.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter


@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var securityProps: SecurityProps
    @Autowired
    private lateinit var jwtFilter: JwtAuthenticationFilter

    override fun configure(http: HttpSecurity?) {
        http {
            cors {  }
            authorizeRequests {
                authorize("/api/public", permitAll)
                authorize("/api/private", authenticated)
                authorize("/api/private-scoped", hasAuthority("read:private_resource"))
//                authorize("/api/visitor-private", hasRole("visitor"))
//                authorize("/api/admin-private", hasRole("admin"))
            }
            oauth2ResourceServer {
                jwt {  }
            }
            addFilterBefore(jwtFilter, RequestCacheAwareFilter::class.java)
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
    fun auth0WebClient() = WebClient.builder()
            .baseUrl(this.securityProps.auth0.issuerUri)
            .build()

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter? {
        val converter = JwtGrantedAuthoritiesConverter()
        converter.setAuthoritiesClaimName("permissions")
        converter.setAuthorityPrefix("")

        val jwtConverter = JwtAuthenticationConverter()
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter)
        return jwtConverter
    }


}


