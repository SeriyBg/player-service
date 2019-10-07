package com.explorer.player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("ConstantConditions")
@AllArgsConstructor
@RestController
@CrossOrigin
public class PlayerController {

    private final Faker faker;
    private RedisTemplate<String, Long> userBoard;
    private static String some = null;

    @GetMapping("/name")
    public String name() {
        String uniqueName = generateUniqueName();
        userBoard.opsForValue().set(uniqueName, 0L);
        return uniqueName;
    }

    @PostMapping("/name/{name}/{score}")
    public void updateScore(@PathVariable String name, @PathVariable Long score) {
        userBoard.opsForValue().set(name, score, 5, TimeUnit.SECONDS);
    }

    @GetMapping("/leaderboard")
    public Map<String, Long> getLeaderBoard() {
        Map<String, Long> collect = Objects.requireNonNull(userBoard.keys("*"))
                .stream()
                .collect(Collectors.toMap(Function.identity(), key -> userBoard.opsForValue().get(key)));
        return collect.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    private String generateUniqueName() {
        var name = faker.ancient().god() + " " + faker.space().company();
        if(userBoard.hasKey(name)) {
            name = generateUniqueName();
        }
        return name;
    }
}
