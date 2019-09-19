package fun.cllc.redis.config.model;


import lombok.Data;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author chenlei
 * @date 2019-09-09
 */
@Data
public abstract class BaseRedisConfig {
    private String name;
    private RedisPoolConfig pool;

    BaseRedisConfig() {
        this.pool = new RedisPoolConfig();
    }

    /**
     * 解析一条配置
     *
     * @param key
     * @param value
     */
    public void parseConfig(String key, String value) {
        this.pool.parseConfig(key, value);
    }

    /**
     * 获取JedisPoolConfig
     *
     * @return
     */
    JedisPoolConfig getJedisPoolConfig() {
        return this.pool.buildJedisPoolConfig();
    }

    /**
     * 构造JedisConnectionFactory
     *
     * @return
     */
    public abstract JedisConnectionFactory buildJedisConnectionFactory();
}
