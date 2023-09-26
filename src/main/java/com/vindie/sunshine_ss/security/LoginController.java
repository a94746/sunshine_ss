package com.vindie.sunshine_ss.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/login")
public class LoginController {

    @GetMapping("/test")
    public String test() {
        return "Hello sunshine!";
    }
}
