plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

dependencies {
    // Ktor клиент
    implementation("io.ktor:ktor-client-core:2.3.10")
    implementation("io.ktor:ktor-client-cio:2.3.10")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.10")
    implementation("io.ktor:ktor-client-serialization:2.3.10")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.10")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}


tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.withType<JavaExec> {
    jvmArgs("-Dfile.encoding=UTF-8")
}