package nl.mvdb.heroku.oauth.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("api")
class ApiProps {
    lateinit var allowedOrigins: List<String>
}