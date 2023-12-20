plugins {
    id("java")
    
}

group = "org.elyte"
version = "1.0-SNAPSHOT"



repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    implementation("com.rabbitmq:amqp-client:5.20.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.3.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0-rc1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "org.elyte.MainWorker"
    }
}