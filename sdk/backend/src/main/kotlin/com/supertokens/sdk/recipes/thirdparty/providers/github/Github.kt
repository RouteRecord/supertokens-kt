package com.supertokens.sdk.recipes.thirdparty.providers.github

import com.supertokens.sdk.SuperTokens
import com.supertokens.sdk.recipes.thirdparty.ThirdPartyRecipe
import com.supertokens.sdk.recipes.thirdparty.providers.OAuthProvider
import com.supertokens.sdk.recipes.thirdparty.providers.OAuthProviderConfig
import com.supertokens.sdk.recipes.thirdparty.providers.ProviderBuilder
import com.supertokens.sdk.recipes.thirdparty.providers.ThirdPartyEmail
import com.supertokens.sdk.recipes.thirdparty.providers.ThirdPartyProviderException
import com.supertokens.sdk.recipes.thirdparty.providers.ThirdPartyUserInfo
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class GithubConfig : OAuthProviderConfig()

class GithubProvider(
    private val superTokens: SuperTokens,
    config: GithubConfig,
) : OAuthProvider<GithubConfig>(config) {

    override val id = ID
    override val authUrl = AUTH_URL
    override val tokenUrl = TOKEN_URL
    override val defaultScopes = listOf(
        "read:user",
        "user:email",
    )

    override suspend fun getUserInfo(accessToken: String): ThirdPartyUserInfo {
        val response = superTokens.client.get(USER_URL) {
            bearerAuth(accessToken)
            contentType(HEADER_CONTENT_TYPE)
        }

        if (response.status != HttpStatusCode.OK) {
            throw ThirdPartyProviderException(response.bodyAsText())
        }

        val body = response.body<GithubGetUserResponse>()

        return ThirdPartyUserInfo(
            id = body.id.toString(),
            email = getEmail(accessToken)?.let {
                ThirdPartyEmail(
                    id = it.email,
                    isVerified = it.verified
                )
            }
        )
    }

    private suspend fun getEmail(accessToken: String): GithubGetEmailResponse? {
        val response = superTokens.client.get(EMAIL_URL) {
            bearerAuth(accessToken)
            contentType(HEADER_CONTENT_TYPE)
        }

        if (response.status != HttpStatusCode.OK) {
            return null
        }

        val body = response.body<List<GithubGetEmailResponse>>()

        return (body.firstOrNull { it.primary } ?: body.firstOrNull())
    }

    companion object {
        const val ID = "github"

        const val AUTH_URL = "https://github.com/login/oauth/authorize"
        const val TOKEN_URL = "https://github.com/login/oauth/access_token"
        const val USER_URL = "https://api.github.com/user"
        const val EMAIL_URL = "https://api.github.com/user/emails"

        val HEADER_CONTENT_TYPE = ContentType("application", "vnd.github.v3+json")
    }

}

val Github = object : ProviderBuilder<GithubConfig, GithubProvider>() {

    override fun install(configure: GithubConfig.() -> Unit): (SuperTokens, ThirdPartyRecipe) -> GithubProvider {
        val config = GithubConfig().apply(configure)

        return { superTokens, _ ->
            GithubProvider(
                superTokens, config,
            )
        }
    }

}