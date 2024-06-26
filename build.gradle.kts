plugins {
	id("java")
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.5"
}

group = "com.juny"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	val compileOnly by getting {
		extendsFrom(configurations.getByName("annotationProcessor"))
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation ("com.google.guava:guava:33.2.1-jre")
	implementation ("org.springframework.security:spring-security-oauth2-authorization-server")
	implementation ("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation ("org.springframework.boot:spring-boot-starter-mail")
	implementation ("io.jsonwebtoken:jjwt-api:0.12.5")
	implementation ("io.jsonwebtoken:jjwt-impl:0.12.5")
	implementation ("io.jsonwebtoken:jjwt-jackson:0.12.5")
	implementation ("org.springframework.boot:spring-boot-starter-security")
	testImplementation ("org.springframework.security:spring-security-test")
	implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.1")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}
