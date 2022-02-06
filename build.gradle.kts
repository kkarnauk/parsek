plugins {
    kotlin("multiplatform") version "1.6.10"

    id("signing")
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.6.10"
}

group = "io.github.kkarnauk"
version = "0.1"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    targets.all {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = true
            }
        }
    }

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(BOTH) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}

// Publishing

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

publishing {
    val myUsername = findProperty("ossrhUsername")?.toString()
    val myPassword = findProperty("ossrhPassword")?.toString()
    val projectGitUrl = findProperty("parsekGitUrl")?.toString()

    repositories {
        maven {
            name = "sonatypeStaging"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = myUsername
                password = myPassword
            }
        }
    }

    publications.withType<MavenPublication> {
        artifact(javadocJar.get())

        pom {
            name.set("parsek")
            description.set("Convenient parser combinators library for Kotlin Multiplatform.")
            url.set("https://github.com/kkarnauk/parsek")

            licenses {
                license {
                    name.set("Apache-2.0 License")
                    url.set("https://opensource.org/licenses/Apache-2.0")
                }
            }
            developers {
                developer {
                    id.set("kkarnauk")
                    name.set("Kirill Karnaukhov")
                    email.set("bitikit@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:git://$projectGitUrl")
                developerConnection.set("scm:git:ssh://$projectGitUrl.git")
                url.set("https://$projectGitUrl")
            }
        }

        signing.sign(this)
    }
}
