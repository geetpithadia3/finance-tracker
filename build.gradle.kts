import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "3.2.2"
  id("io.spring.dependency-management") version "1.1.4"
  kotlin("jvm") version "1.9.22"
  kotlin("plugin.spring") version "1.9.22"
  kotlin("plugin.jpa") version "1.9.22"
}

group = "com"

version = "0.0.1-SNAPSHOT"

java { sourceCompatibility = JavaVersion.VERSION_17 }

repositories { mavenCentral() }

dependencies {
  implementation(platform("org.axonframework:axon-bom:4.9.3"))
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-web")
  //    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
  implementation("org.springframework.boot:spring-boot-starter-integration")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.1")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.axonframework:axon-spring-boot-starter")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("org.springframework.integration:spring-integration-jpa")
  //	implementation("org.springframework.integration:spring-integration-mongodb")
  implementation("org.springframework.integration:spring-integration-webflux")
  runtimeOnly("org.postgresql:postgresql")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.springframework.integration:spring-integration-test")
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
  implementation("com.squareup.okhttp3:okhttp:4.10.0")

  // Persistence
  implementation("org.springframework.boot:spring-boot-starter-actuator:3.2.2")

  compileOnly("org.projectlombok:lombok:1.18.30")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs += "-Xjsr305=strict"
    jvmTarget = "17"
  }
}

tasks.withType<Test> { useJUnitPlatform() }
