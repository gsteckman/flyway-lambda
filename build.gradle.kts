repositories {
    mavenCentral()
}

plugins {
    id("java")
    id("maven-publish")
    alias(libs.plugins.io.spring.dependency.management)
    alias(libs.plugins.com.gradleup.shadow)
    alias(libs.plugins.ca.cutterslade.analyze)
}

group = "com.geekoosh"
version = "0.11.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Xlint:unchecked")
}

tasks.shadowJar {
    archiveFileName.set("flyway-all.jar")
    transform<com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer>()
}

dependencies {
    implementation(libs.flyway.core)
    runtimeOnly(libs.flyway.mysql)
    runtimeOnly(libs.flyway.postgresql)
    implementation(libs.org.eclipse.jgit)
    implementation(libs.commons.io)
    implementation(libs.json)
    runtimeOnly(libs.org.postgresql.postgresql)
    runtimeOnly(libs.mysql.connector.j)
    implementation(libs.log4j.api)
    runtimeOnly(libs.log4j.core)
    implementation(libs.commons.lang3)
    implementation(libs.aws.lambda.java.core)
    runtimeOnly(libs.aws.lambda.java.log4j2)
    implementation(libs.aws.java.sdk.core)
    implementation(libs.aws.java.sdk.s3)
    implementation(libs.aws.java.sdk.secretsmanager)

    // Jackson dependencies for Flyway config file formats
    runtimeOnly(libs.jackson.databind)
    runtimeOnly(libs.jackson.dataformat.yaml)
    runtimeOnly(libs.jackson.dataformat.toml)

    testImplementation(platform(libs.testcontainers))
    testImplementation(libs.testcontainers)
    testImplementation(Testing.junit4)
    testImplementation(Testing.mockito.core)
    testImplementation(libs.system.stubs.junit4)
    testImplementation(libs.jakarta.servlet)
    testImplementation(libs.org.eclipse.jgit.http.server)
    testImplementation(libs.org.eclipse.jgit.junit.http)
    testImplementation(libs.org.eclipse.jgit.junit)
    testImplementation(libs.org.eclipse.jgit)
    testImplementation(libs.jetty.servlet)
    testImplementation(libs.org.testcontainers.postgresql)
    testImplementation(libs.mysql)
    testImplementation(libs.localstack)
}

tasks.register<Zip>("buildZip") {
    from(tasks.named("compileJava"))
    from(tasks.named("processResources"))
    into("lib") {
        from(configurations.runtimeClasspath)
    }
}

tasks.named("build") {
    dependsOn("buildZip")
}
