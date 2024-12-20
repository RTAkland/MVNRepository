plugins {
    kotlin("jvm")
    id("maven-publish")
}

val testPublishVersion: String by project

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
            artifactId = "test-publish-3"
            version = testPublishVersion
        }
    }

    repositories {
        maven {
            url = uri("http://127.0.0.1:8088/releases")
            isAllowInsecureProtocol = true
            credentials {
                username = "admin"
                password = "0db30555-988e-41c2-b1f6-6d9fa9ce876b"
            }
        }
    }
}

