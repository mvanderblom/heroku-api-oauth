package nl.mvdb.heroku.oauth.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("api.security")
class SecurityProps {
    val auth0 = Auth0Props()
}

class Auth0Props {
    lateinit var issuerUri: String
    lateinit var audience: String
    lateinit var allowedOrigins: List<String>
}