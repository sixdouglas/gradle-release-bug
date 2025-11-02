dependencies {
    api("org.springframework.boot:spring-boot-starter-web")
}


tasks.withType(Test::class) {
    jvmArgs("-javaagent:${configurations.testRuntimeClasspath.get().find { it.name.contains("mockito-core") }?.absolutePath}")
}
