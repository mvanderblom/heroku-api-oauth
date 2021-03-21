package nl.mvdb.heroku.oauth.api.config

data class UserInfo(
        var sub: String? = null,
        var nickname: String? = null,
        var name: String? = null,
        var picture: String? = null,
        var updatedAt: String? = null,
        var email: String? = null,
        var emailVerified: Boolean = false
)