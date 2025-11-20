package io.github.kosovo21.uam.controller

import io.github.kosovo21.uam.entity.User
import io.github.kosovo21.uam.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(private val service : UserService) {

    @PostMapping
    fun register(@RequestBody user: User) = service.register(user)

    @GetMapping
    fun findAll() = service.findAll()

}