package fun.cllc.redis.client;

import org.springframework.data.redis.core.RedisTemplate;


/**
 * @author chenlei
 * @date 2018/1/11
 */
public class RedisClientDecorate {
    public static final String REDIS_TEMPLATE_PROP = "redisTemplate";

    private RedisTemplate<String, String> redisTemplate;

    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * get
     *
     * @param field
     * @return
     */
    public String get(final String field) {
        return redisTemplate.opsForValue().get(field);
    }
}
