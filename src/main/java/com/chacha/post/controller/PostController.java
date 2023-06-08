package com.chacha.post.controller;

import com.chacha.post.dto.PostCreate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class PostController {

    @PostMapping("/posts")
    public String get(@RequestBody PostCreate params) {
        log.info("params={}", params);
        return "Hello World";
    }
}
