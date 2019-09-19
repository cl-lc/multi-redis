package fun.cllc.redis.config.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author chenlei
 * @date 2019-09-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StandaloneRedisConfig extends BaseRedisConfig {
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private static final String KEY_PASSWORD = "password";

    private String host;
    private Integer port;
    private String password;

    public StandaloneRedisConfig() {
        super();
    }

    /**
     * 解析一条配置
     *
     * @param key
     * @param value
     */
    @Override
    public void parseConfig(String key, String value) {
        if (KEY_HOST.equals(key)) {
            this.host = value;
        } else if (KEY_PORT.equals(key)) {
            this.port = Integer.valueOf(value);
        } else if (KEY_PASSWORD.equals(key)) {
            this.password = value;
        } else {
            super.parseConfig(key, value);
        }
    }

    /**
     * 构造JedisConnectionFactory
     *
     * @return
     */
    @Override
    public JedisConnectionFactory buildJedisConnectionFactory() {
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
        standaloneConfig.setHostName(this.host);
        standaloneConfig.setPassword(RedisPassword.of(this.password));
        standaloneConfig.setPort(this.port);

        JedisPoolConfig jedisPoolConfig = getJedisPoolConfig();
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().
                poolConfig(jedisPoolConfig).build();
        return new JedisConnectionFactory(standaloneConfig, jedisClientConfiguration);
    }
}
