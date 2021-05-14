package com.explorer.player;

import com.github.javafaker.Faker;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PlayerServiceApplication {

    @Bean
    public Faker faker() {
        return new Faker();
    }

    @Bean
    public IMap<String, Long> leaderboard(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap("leaderboard");
    }

    public static void main(String[] args) {
        SpringApplication.run(PlayerServiceApplication.class, args);
    }

}
