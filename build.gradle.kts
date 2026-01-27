import java.util.*

plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

val baseBuild = gradle.includedBuild("pylon-base")
val coreBuild = gradle.includedBuild("rebar")

tasks.runServer {
    dependsOn(baseBuild.task(":shadowJar"), coreBuild.task(":rebar:shadowJar"))

    doFirst {
        val runFolder = project.projectDir.resolve("run")
        runFolder.mkdirs()
        runFolder.resolve("eula.txt").writeText("eula=true")

        val pluginsDir = runFolder.resolve("plugins")
        if (!System.getProperty("io.github.pylonmc.rebar.disableConfigReset").toBoolean()) {
            pluginsDir.resolve("RebarCore").deleteRecursively()
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
            from(coreBuild.projectDir.resolve("rebar/build/libs")) {
                include("rebar-1.0.0-SNAPSHOT.jar")
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

    val rebarVersion = readMinecraftVersion(coreBuild)
    val baseVersion = readMinecraftVersion(baseBuild)
    if (rebarVersion != baseVersion) {
        throw GradleException("Minecraft version mismatch between Rebar ($rebarVersion) and pylon-base ($baseVersion)")
    }
    minecraftVersion(rebarVersion)
}

tasks.register("runStableServer") {
    dependsOn(baseBuild.task(":runServer"))
    group = "run paper"
}

tasks.register("runTests") {
    dependsOn(coreBuild.task(":test:runServer"))
    group = "run paper"
}
