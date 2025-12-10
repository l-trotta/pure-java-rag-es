plugins {
    id("java")
}

group = "demo.elastic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("co.elastic.clients:elasticsearch-java:9.2.2")
    implementation("org.springframework.ai:spring-ai-pdf-document-reader:1.1.0")
    implementation("com.google.genai:google-genai:1.29.0")
}

tasks.test {
    useJUnitPlatform()
}
