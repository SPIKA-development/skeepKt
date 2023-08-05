package application.configuration

import application.configuration.EnvVar.sslPassword
import application.module
import io.ktor.network.tls.certificates.*
import io.ktor.server.engine.*
import org.slf4j.LoggerFactory
import java.io.File
import java.security.KeyStore

val keyStoreFile = File("keystore.jks")
val keyStore =
    runCatching {
        KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(keyStoreFile.inputStream(), sslPassword.toCharArray())
        }
    }.getOrElse {
        buildKeyStore {
            certificate("sampleAlais") {
                password = sslPassword
                domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
            }
        }.apply { saveToFile(keyStoreFile, sslPassword) }
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
