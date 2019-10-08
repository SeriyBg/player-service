package com.explorer.player;

import java.nio.ByteBuffer;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
@EnableRedisRepositories
public class PlayerServiceApplication {

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    public Faker faker() {
        return new Faker();
    }

    @Bean
    public RedisConfiguration redisConfiguration() {
        return new RedisStandaloneConfiguration(redisHost, redisPort);
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisConfiguration());
    }

    @Bean
    public RedisTemplate<String, Long> userBoard() {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisSerializer<Long>() {
            @Override
            public byte[] serialize(Long aLong) throws SerializationException {
                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.putLong(aLong);
                return buffer.array();
            }

            @Override
            public Long deserialize(byte[] bytes) throws SerializationException {
                if (bytes == null) {
                    return 0L;
                }
                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.put(bytes);
                buffer.flip();
                return buffer.getLong();
            }
        });
        return template;
    }

    public static void main(String[] args) {
        SpringApplication.run(PlayerServiceApplication.class, args);
    }

}
