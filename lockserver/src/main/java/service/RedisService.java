package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private RedisTemplate< String, Object > template;

    @Autowired
    public RedisService(RedisTemplate<String, Object> template) {
        this.template = template;
    }

    public Object getValue(final String key) {
        return template.opsForValue().get(key);
    }

    public Boolean hasKey(final String key) {
        return template.hasKey(key);
    }

    public void setValue(final String key, final String value) {
        template.opsForValue().set(key, value);

        // set a expire for a message
        template.expire(key, 5, TimeUnit.MINUTES);
    }

}