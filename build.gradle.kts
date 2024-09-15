plugins {
	java
	id("org.springframework.boot") version "3.2.8"
	id("io.spring.dependency-management") version "1.1.6"
}

configurations.all {
	resolutionStrategy {
		eachDependency {
			if (requested.group == "io.swagger.core.v3") {
				useVersion("2.2.22")
				because("Swagger UI incompatible dependency io.swagger.core.v3:swagger-annotations")
			}
		}
	}
}

group = "com.document.manager.authors"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(22)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

ext {
	set("testcontainers.version", "1.19.8")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation:3.2.8")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
	implementation("io.springfox:springfox-boot-starter:3.0.0")
	implementation ("io.springfox:springfox-swagger-ui:3.0.0")
	implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
	implementation("org.springframework.kafka:spring-kafka:3.2.0")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation ("org.testcontainers:junit-jupiter")
	testImplementation ("org.testcontainers:postgresql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("io.rest-assured:json-path:5.5.0")
	testImplementation ("io.rest-assured:rest-assured:5.5.0")


}

tasks.withType<Test> {
	useJUnitPlatform()
}
