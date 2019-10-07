package com.explorer.player;

import java.nio.ByteBuffer;
import com.github.javafaker.Faker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
@EnableRedisRepositories
public class PlayerServiceApplication {

    @Bean
    public Faker faker() {
        return new Faker();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
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
