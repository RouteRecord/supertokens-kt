package com.supertokens.sdk.recipes.emailpassword

import com.supertokens.sdk.SuperTokensClient
import com.supertokens.sdk.common.RECIPE_EMAIL_PASSWORD
import com.supertokens.sdk.common.models.User
import com.supertokens.sdk.handlers.SignInProvider
import com.supertokens.sdk.handlers.SignInProviderConfig
import com.supertokens.sdk.handlers.SignUpProvider
import com.supertokens.sdk.handlers.SignUpProviderConfig
import com.supertokens.sdk.recipes.Recipe
import com.supertokens.sdk.recipes.RecipeBuilder
import com.supertokens.sdk.recipes.RecipeConfig
import com.supertokens.sdk.recipes.core.usecases.CheckEmailExistsUseCase
import com.supertokens.sdk.recipes.emailpassword.usecases.EmailPasswordSignInUseCase
import com.supertokens.sdk.recipes.emailpassword.usecases.EmailPasswordSignUpUseCase

class EmailPasswordConfig: RecipeConfig

class EmailPasswordRecipe(
    private val superTokens: SuperTokensClient,
    private val config: EmailPasswordConfig,
) : Recipe<EmailPasswordConfig> {

    private val emailPasswordSignInUseCase by lazy {
        EmailPasswordSignInUseCase(
            client = superTokens.apiClient,
            authRepository = superTokens.authRepository,
            tenantId = superTokens.tenantId,
        )
    }

    private val emailPasswordSignUpUseCase by lazy {
        EmailPasswordSignUpUseCase(
            client = superTokens.apiClient,
            authRepository = superTokens.authRepository,
            tenantId = superTokens.tenantId,
        )
    }

    private val checkEmailExistsUseCase by lazy {
        CheckEmailExistsUseCase(
            client = superTokens.apiClient,
            tenantId = superTokens.tenantId,
            recipeId = RECIPE_EMAIL_PASSWORD,
        )
    }

    suspend fun signUp(email: String, password: String) = emailPasswordSignUpUseCase.signUp(email = email, password = password)
    suspend fun signIn(email: String, password: String) = emailPasswordSignInUseCase.signIn(email = email, password = password)
    suspend fun checkEmailExists(email: String) = checkEmailExistsUseCase.checkEmailExists(email = email)

}

object EmailPassword : RecipeBuilder<EmailPasswordConfig, EmailPasswordRecipe>(), SignInProvider<EmailPassword.Config, User>,
    SignUpProvider<EmailPassword.Config, User> {

    override fun install(configure: EmailPasswordConfig.() -> Unit): (SuperTokensClient) -> EmailPasswordRecipe {
        val config = EmailPasswordConfig().apply(configure)

        return {
            EmailPasswordRecipe(it, config)
        }
    }

    data class Config(var email: String? = null, var password: String? = null) : SignInProviderConfig, SignUpProviderConfig

    override suspend fun signIn(superTokensClient: SuperTokensClient, configure: Config.() -> Unit): User {
        val config = Config().apply(configure)

        val email = config.email
        val password = config.password

        if(email == null || password == null) {
            throw IllegalStateException("'email' and 'password' must be provided")
        }

        return superTokensClient.getRecipe<EmailPasswordRecipe>().signIn(
            email = email,
            password = password,
        )
    }

    override suspend fun signUp(superTokensClient: SuperTokensClient, configure: Config.() -> Unit): User {
        val config = Config().apply(configure)

        val email = config.email
        val password = config.password

        if(email == null || password == null) {
            throw IllegalStateException("'email' and 'password' must be provided")
        }

        return superTokensClient.getRecipe<EmailPasswordRecipe>().signUp(
            email = email,
            password = password,
        )
    }

}