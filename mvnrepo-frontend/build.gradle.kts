import com.github.gradle.node.npm.task.NpmTask

node {
    version = "20.17.0"
    download = true
}

val buildTask = tasks.register<NpmTask>("buildFrontend") {
    args.set(listOf("run", "build"))
    dependsOn(tasks.npmInstall)
    inputs.dir(project.fileTree("src"))
    inputs.dir("node_modules")
    inputs.files("vite.config.js", "index.html")
    val distDir = file(project.layout.projectDirectory.dir("dist"))
    val generatedDir = file(project(":mvnrepo-backend").layout.buildDirectory.dir("generated"))
    outputs.dir(distDir)
    doLast {
        if (generatedDir.exists()) {
            generatedDir.deleteRecursively()
        }
        distDir.copyRecursively(generatedDir, overwrite = true)
    }
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

file("dist").mkdirs()