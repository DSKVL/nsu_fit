plugins {
    id("java")
}

group = "ru.nsu.fit.dskvl.gfx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.formdev:flatlaf:3.0")
}

tasks.test {
    useJUnitPlatform()
}