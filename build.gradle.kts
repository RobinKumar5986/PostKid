plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.intellij") version "1.16.0"
}

group = "com.kgJr.postKid"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

kotlin {
    jvmToolchain(17)
}

intellij {
    version.set("2024.1.3")
    type.set("IC")
    plugins.set(listOf("java"))
}

tasks {
    patchPluginXml {
        version.set(project.version.toString())
        sinceBuild.set("241")
        untilBuild.set("242.*")
    }

    test {
        useJUnitPlatform()
    }

    runIde {
        jvmArgs = listOf("-Djb.consents.confirmation.enabled=false")
    }

    buildPlugin {
        archiveBaseName.set("PostKid")
        archiveVersion.set(project.version.toString())
    }

    // Debug task to list runtime dependencies
    register("printDependencies") {
        doLast {
            println("Runtime dependencies:")
            configurations.runtimeClasspath.get().resolvedConfiguration.resolvedArtifacts.forEach {
                println("${it.moduleVersion.id} -> ${it.file}")
            }
        }
    }

    // Inspect plugin ZIP contents
    register("inspectPluginJar") {
        dependsOn(buildPlugin)
        doLast {
            val zipFile = file("${buildDir}/distributions/PostKid-${project.version}.zip")
            if (zipFile.exists()) {
                exec {
                    commandLine("unzip", "-l", zipFile.absolutePath)
                }
                println("Inspecting ZIP contents: Check for PostKid.jar, lib/okhttp-4.12.0.jar, lib/gson-2.10.1.jar, lib/okio-3.6.0.jar")
                val tempDir = file("${buildDir}/temp")
                tempDir.mkdirs()
                exec {
                    commandLine("unzip", zipFile.absolutePath, "-d", tempDir.absolutePath)
                }
                val jarFile = file("${tempDir}/PostKid.jar")
                if (jarFile.exists()) {
                    exec {
                        commandLine("jar", "tf", jarFile.absolutePath)
                    }
                    println("Inspecting PostKid.jar contents: Check for com/kgJr/posKid/api/ApiHandler.class")
                } else {
                    println("PostKid.jar not found in ZIP")
                }
                tempDir.deleteRecursively()
            } else {
                println("Plugin ZIP not found at: ${zipFile.absolutePath}")
            }
        }
    }
}
