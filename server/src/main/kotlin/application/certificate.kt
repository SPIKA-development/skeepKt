package application

import application.EnvVar.sslPassword
import io.ktor.network.tls.certificates.*
import io.ktor.network.tls.extensions.*
import io.ktor.server.engine.*
import org.slf4j.LoggerFactory
import java.io.File
import java.security.KeyStore

val keyStoreFile = File("build/keystore.jks")
val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
    load(keyStoreFile.inputStream(), sslPassword.toCharArray())
}
val environment = applicationEngineEnvironment {
    log = LoggerFactory.getLogger("ktor.application")
    connector {
        port = EnvVar.serverPort
    }
    sslConnector(
        keyStore = keyStore,
        keyAlias = keyStore.aliases().nextElement(),
        keyStorePassword = { sslPassword.toCharArray() },
        privateKeyPassword = { sslPassword.toCharArray() }) {
        port = EnvVar.sslPort
        keyStorePath = keyStoreFile
    }
    module { module() }
}
