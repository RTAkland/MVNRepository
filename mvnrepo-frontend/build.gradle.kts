val buildTask = tasks.register("buildFrontend") {
    val distDir = file(project.layout.projectDirectory.dir("src"))
    val generatedDir = file(project(":mvnrepo-backend").layout.buildDirectory.dir("generated"))
    outputs.dir(distDir)
    if (generatedDir.exists()) {
        generatedDir.deleteRecursively()
    }
    distDir.copyRecursively(generatedDir, overwrite = true)
}

sourceSets {
    java {
        main {
            resources {
                srcDir(buildTask)
            }
        }
    }
}