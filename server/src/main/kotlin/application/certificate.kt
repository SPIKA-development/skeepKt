package application

import application.EnvVar.sslPassword
import io.ktor.network.tls.certificates.*
import io.ktor.server.engine.*
import org.slf4j.LoggerFactory
import java.io.File

val keyStoreFile = File("build/keystore.jks")
val keyStore = buildKeyStore {
    certificate("ssl") {
        println(sslPassword)
        password = sslPassword
        domains = EnvVar.domains
    }
}.apply { saveToFile(keyStoreFile, sslPassword) }
val environment = applicationEngineEnvironment {
    log = LoggerFactory.getLogger("ktor.application")
    connector {
        port = EnvVar.serverPort
    }
    sslConnector(
        keyStore = keyStore,
        keyAlias = "ssl",
        keyStorePassword = { sslPassword.toCharArray() },
        privateKeyPassword = { sslPassword.toCharArray() }) {
        port = EnvVar.sslPort
        keyStorePath = keyStoreFile
    }
    module { module() }
}
