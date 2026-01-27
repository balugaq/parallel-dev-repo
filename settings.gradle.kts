run {
    Runtime.getRuntime().exec(arrayOf("git", "clone", "https://github.com/pylonmc/rebar")).waitFor()
}

run {
    Runtime.getRuntime().exec(arrayOf("git", "clone", "https://github.com/pylonmc/pylon")).waitFor()
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "pylon"
includeBuild("rebar")
includeBuild("pylon")
