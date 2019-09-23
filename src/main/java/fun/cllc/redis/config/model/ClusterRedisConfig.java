package fun.cllc.redis.config.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;

/**
 * @author chenlei
 * @date 2019-09-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterRedisConfig extends BaseRedisConfig {
    public static final String CONFIG_PREFIX = "cluster";
    private static final String KEY_MAX_REDIRECTS = CONFIG_PREFIX + ".max-redirects";
    private static final String KEY_NODES = CONFIG_PREFIX + ".nodes";
    private static final String PASSWORD = "password";

    private Integer maxRedirects;
    private String nodes;
    private String password;

    public ClusterRedisConfig() {
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
        if (KEY_MAX_REDIRECTS.equals(key)) {
            this.maxRedirects = Integer.valueOf(value);
        } else if (KEY_NODES.equals(key)) {
            this.nodes = value;
        } else if (PASSWORD.equals(key)) {
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
        if (this.nodes == null || this.nodes.isEmpty()) {
            return null;
        }

        String[] items = this.nodes.split(",");
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(Arrays.asList(items));
        if (this.password != null) {
            clusterConfig.setPassword(RedisPassword.of(this.password));
        }
        if (this.maxRedirects != null) {
            clusterConfig.setMaxRedirects(this.maxRedirects);
        }

        JedisPoolConfig jedisPoolConfig = getJedisPoolConfig();
        return new JedisConnectionFactory(clusterConfig, jedisPoolConfig);
    }
}
