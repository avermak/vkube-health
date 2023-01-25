plugins {
    id("java")
    id("idea")
    id("io.vertx.vertx-plugin") version "1.1.1"
}

group = "com.avermak.vkube.health"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.vertx:vertx-grpc-server:4.3.6")
    implementation("io.vertx:vertx-web:4.3.6")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

vertx {
    mainVerticle = "com.avermak.vkube.health.MainVerticle"
}
