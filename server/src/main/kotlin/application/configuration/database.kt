package application.configuration

import org.jetbrains.exposed.sql.Database

fun configureDatabase() {
//    if (developmentMode) {
        Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver")
//    } else {
//        Database.connect(EnvVar.DB_URL, "oracle.jdbc.OracleDriver", EnvVar.DB_USERNAME, EnvVar.DB_PASSWORD)
//    }
}