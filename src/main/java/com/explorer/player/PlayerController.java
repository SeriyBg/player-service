package com.explorer.player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("ConstantConditions")
@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/player")
public class PlayerController {

    private final Faker faker;
    private RedisTemplate<String, Long> userBoard;

    @GetMapping("")
    public String health() {
        return "Ok";
    }

    @GetMapping("/name")
    public String name() {
        String uniqueName = generateUniqueName();
        userBoard.opsForValue().set(uniqueName, 0L);
        return uniqueName;
    }

    @GetMapping("/score")
    public Long playerScore(@RequestHeader("user") String user) {
        if (user == null || user.isBlank()) {
            return 0L;
        }
        return userBoard.opsForValue().get(user);
    }

    @PostMapping("/score/{score}")
    public void updateScore(@PathVariable Long score, @RequestHeader("user") String user) {
        userBoard.opsForValue().set(user, score);
    }

    @GetMapping("/leaderboard")
    public Map<String, Long> getLeaderBoard(@RequestParam(defaultValue = "3") Integer top) {
        Map<String, Long> collect = Objects.requireNonNull(userBoard.keys("*"))
                .stream()
                .collect(Collectors.toMap(Function.identity(), key -> userBoard.opsForValue().get(key)));
        return collect.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(top)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    private String generateUniqueName() {
        var name = faker.ancient().god() + " " + faker.space().company();
        if(userBoard.hasKey(name)) {
            name = faker.ancient().hero() + " " + faker.space().company();
            if(userBoard.hasKey(name)) {
                name = generateUniqueName();
            }
        }
        return name;
    }
}
