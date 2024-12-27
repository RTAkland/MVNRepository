plugins {
    kotlin("jvm")
    id("maven-publish")
}

val testPublishVersion = "0.0.2"

group = "cn.rtast"
version = testPublishVersion

repositories {
    mavenCentral()
}

val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

artifacts {
    archives(sourceJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourceJar)
            artifactId = "test-publish-5"
            version = testPublishVersion
        }
    }

    repositories {
        maven {
            url = uri("http://127.0.0.1:8088/releases")
            isAllowInsecureProtocol = true
            credentials {
                username = "admin"
                password = "4565271c-e128-4fde-bfd8-daad4c286da2"
            }
        }
    }
}

