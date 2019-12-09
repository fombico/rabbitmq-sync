package com.fombico.discomm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
public class ApiController {

    private Random random = new Random();
    private static AtomicInteger counter = new AtomicInteger();

    @PostMapping("/sendMessage")
    public void sendMessage() {
        log.info("{} sendMessage called", counter.incrementAndGet());
        try {
            Thread.sleep(30000 + random.nextInt(30000));
        } catch (InterruptedException ignored) {
        }
    }
}
