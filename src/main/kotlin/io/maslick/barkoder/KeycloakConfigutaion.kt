package io.maslick.barkoder

import org.keycloak.KeycloakPrincipal
import org.keycloak.KeycloakSecurityContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS
import org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Configuration
@ConditionalOnProperty(value = ["keycloak.enabled"])
class KeycloakConfigutaion {
    @Bean
    @Scope(scopeName = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    fun getKeycloakSecurityContext(): KeycloakSecurityContext? {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        return (request.userPrincipal as KeycloakPrincipal<*>).keycloakSecurityContext
    }

    @Bean
    @Scope(scopeName = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    fun getAccessToken(context: KeycloakSecurityContext?) = context?.token
}