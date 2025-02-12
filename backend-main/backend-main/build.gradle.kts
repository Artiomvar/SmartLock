plugins {
    id("java")
    id("application")
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "be.kdg"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)  // Adjust Java version if necessary
    }
}

application {
    mainClass.set("be.kdg.Main")  // Specify the fully qualified name of the class with the main method
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation ("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")
    // Spring Data JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // PostgreSQL Database Driver
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.webjars:bootstrap:5.3.3")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    //Spring Security
    implementation ("org.springframework.boot:spring-boot-starter-security")
// https://mvnrepository.com/artifact/org.thymeleaf.extras/thymeleaf-extras-springsecurity5
    implementation ("org.thymeleaf.extras:thymeleaf-extras-springsecurity6:3.1.3.RELEASE")


        implementation("org.springframework.boot:spring-boot-starter-cache")
        implementation("org.ehcache:ehcache")
    



}


//tasks.named<JavaExec>("run") {
//    standardInput = System.`in`
//}

tasks.withType<Test> {
    useJUnitPlatform()
}
