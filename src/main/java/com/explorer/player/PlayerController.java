package com.explorer.player;

import com.github.javafaker.Faker;
import com.hazelcast.map.IMap;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/player")
public class PlayerController {

    private final Faker faker;
    private final IMap<String, Long> leaderboard;

    @GetMapping("")
    public String health() {
        return "Ok";
    }

    @GetMapping("/name")
    public String name() {
        String uniqueName = generateUniqueName();
        leaderboard.put(uniqueName, 0L);
        return uniqueName;
    }

    @GetMapping("/score")
    public Long playerScore(@RequestHeader("user") String user) {
        if (user == null || user.isBlank()) {
            return 0L;
        }
        return leaderboard.get(user);
    }

    @PostMapping("/score/{score}")
    public void updateScore(@PathVariable Long score, @RequestHeader("user") String user) {
        leaderboard.put(user, score);
    }

    @GetMapping("/leaderboard")
    public Map<String, Long> getLeaderBoard(@RequestParam(defaultValue = "3") Integer top) {
        return leaderboard.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(top)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    private String generateUniqueName() {
        var name = faker.ancient().god() + " " + faker.space().company();
        if(leaderboard.containsKey(name)) {
            name = faker.ancient().hero() + " " + faker.space().company();
            if(leaderboard.containsKey(name)) {
                name = generateUniqueName();
            }
        }
        return name;
    }
}
