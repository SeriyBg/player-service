package com.explorer.player;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Health {
    @GetMapping("/")
    public String health() {
        return "Ok";
    }
}
