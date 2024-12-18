plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "MVNRepository"
include("test-publish")
include("test-impl")
