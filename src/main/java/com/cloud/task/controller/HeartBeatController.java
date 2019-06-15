package com.cloud.task.controller;

/**
 * 〈容器存活心跳检测〉<br> 
 *
 * @author number68
 * @date 2019/5/17
 * @since 0.1
 */

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeartBeatController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
