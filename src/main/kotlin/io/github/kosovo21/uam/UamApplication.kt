package io.github.kosovo21.uam

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UamApplication

fun main(args: Array<String>) {
	runApplication<UamApplication>(*args)
}
