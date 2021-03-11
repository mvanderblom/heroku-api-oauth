package nl.mvdb.heroku.oauth.api.config

import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt

class AudienceValidator(val targetAudience: String): OAuth2TokenValidator<Jwt> {
    private val error = OAuth2Error("invalid_token", "The required audience is missing", null)

    override fun validate(optionalToken: Jwt?): OAuth2TokenValidatorResult {
        return optionalToken
                 ?.let { token -> token.audience }
                 ?.let { audiences ->
                     audiences.contains(targetAudience)
                     OAuth2TokenValidatorResult.success()
                }?: OAuth2TokenValidatorResult.failure(error)
    }
}