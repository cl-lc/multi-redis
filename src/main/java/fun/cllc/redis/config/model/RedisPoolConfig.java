package fun.cllc.redis.config.model;


import lombok.Data;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author chenlei
 * @date 2019-09-09
 */
@Data
public class RedisPoolConfig {
    public static final String CONFIG_PREFIX = "jedis";
    private static final String KEY_MAX_IDLE = CONFIG_PREFIX + ".max-idle";
    private static final String KEY_MIN_IDLE = CONFIG_PREFIX + ".min-idle";
    private static final String KEY_MAX_ACTIVE = CONFIG_PREFIX + ".max-active";
    private static final String KEY_MAX_WAIT = CONFIG_PREFIX + ".max-wait";

    private Integer maxIdle;
    private Integer minIdle;
    private Integer maxActive;
    private Integer maxWait;

    public RedisPoolConfig() {
        this.maxIdle = 8;
        this.minIdle = 0;
        this.maxActive = 8;
        this.maxWait = -1;
    }

    /**
     * 解析一条配置
     *
     * @param key
     * @param value
     */
    public void parseConfig(String key, String value) {
        if (KEY_MAX_IDLE.equals(key)) {
            this.maxIdle = Integer.valueOf(value);
        } else if (KEY_MIN_IDLE.equals(key)) {
            this.minIdle = Integer.valueOf(value);
        } else if (KEY_MAX_ACTIVE.equals(key)) {
            this.maxActive = Integer.valueOf(value);
        } else if (KEY_MAX_WAIT.equals(key)) {
            this.maxWait = Integer.valueOf(value);
        }
    }

    /**
     * 构造一个JedisPoolConfig
     *
     * @return
     */
    JedisPoolConfig buildJedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(this.getMaxActive());
        config.setMaxIdle(this.getMaxIdle());
        config.setMinIdle(this.getMinIdle());
        config.setMaxWaitMillis(this.getMaxWait());
        return config;
    }
}
