plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

val baseBuild = gradle.includedBuild("pylon-base")
val coreBuild = gradle.includedBuild("pylon-core")

tasks.runServer {
    dependsOn(baseBuild.task(":shadowJar"), coreBuild.task(":pylon-core:shadowJar"))

    doFirst {
        val runFolder = project.projectDir.resolve("run")
        runFolder.mkdirs()
        runFolder.resolve("eula.txt").writeText("eula=true")

        val pluginsDir = runFolder.resolve("plugins")
        if (!System.getProperty("io.github.pylonmc.pylon.disableConfigReset").toBoolean()) {
            pluginsDir.deleteRecursively()
        }
        pluginsDir.mkdirs()
        copy {
            from(baseBuild.projectDir.resolve("build/libs")) {
                include("pylon-base-1.0.0-SNAPSHOT.jar")
            }
            into(pluginsDir)
        }
        copy {
            from(coreBuild.projectDir.resolve("pylon-core/build/libs")) {
                include("pylon-core-1.0.0-SNAPSHOT.jar")
            }
            into(pluginsDir)
        }
    }

    maxHeapSize = "4G"
    minecraftVersion("1.21.8")
}

tasks.register("runSnapshotServer") {
    dependsOn(tasks.runServer)
    group = "run paper"
}

tasks.register("runStableServer") {
    dependsOn(baseBuild.task(":runServer"))
    group = "run paper"
}

tasks.register("runLiveTests") {
    dependsOn(coreBuild.task(":test:runServer"))
    group = "run paper"
}
