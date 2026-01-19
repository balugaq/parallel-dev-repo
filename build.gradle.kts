import java.util.*

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
            pluginsDir.resolve("PylonCore").deleteRecursively()
            pluginsDir.resolve("PylonBase").deleteRecursively()
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

    fun readMinecraftVersion(build: IncludedBuild): String {
        val props = Properties()
        build.projectDir.resolve("gradle.properties").bufferedReader().use(props::load)
        return props["minecraft.version"] as String
    }

    val coreVersion = readMinecraftVersion(coreBuild)
    val baseVersion = readMinecraftVersion(baseBuild)
    if (coreVersion != baseVersion) {
        throw GradleException("Minecraft version mismatch between pylon-core ($coreVersion) and pylon-base ($baseVersion)")
    }
    minecraftVersion(coreVersion)
}

tasks.register("runStableServer") {
    dependsOn(baseBuild.task(":runServer"))
    group = "run paper"
}

tasks.register("runTests") {
    dependsOn(coreBuild.task(":test:runServer"))
    group = "run paper"
}
