package fun.cllc.redis.config.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenlei
 * @date 2019-09-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SentinelRedisConfig extends BaseRedisConfig {
    public static final String CONFIG_PREFIX = "sentinel";
    private static final String KEY_MASTER = CONFIG_PREFIX + ".master";
    private static final String KEY_NODES = CONFIG_PREFIX + ".nodes";
    private static final String KEY_PASSWORD = "password";

    private String master;
    private String nodes;
    private String password;

    public SentinelRedisConfig() {
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
        if (KEY_MASTER.equals(key)) {
            this.master = value;
        } else if (KEY_NODES.equals(key)) {
            this.nodes = value;
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
        Assert.notNull(this.nodes, "Illegal nodes of sentinel redis config, name: " + this.getName());

        List<RedisNode> nodes = new ArrayList<>();
        String[] items = this.nodes.split(",");
        for (String node : items) {
            try {
                String[] parts = StringUtils.split(node, ":");
                if (parts == null || parts.length != 2) {
                    throw new IllegalStateException("Nodes must be defined as 'host:port'");
                }

                nodes.add(new RedisNode(parts[0], Integer.valueOf(parts[1])));
            } catch (RuntimeException ex) {
                throw new IllegalStateException("Invalid redis sentinel property '" + node + "'", ex);
            }
        }

        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
        sentinelConfig.master(this.master);
        sentinelConfig.setSentinels(nodes);
        if (this.password != null) {
            sentinelConfig.setPassword(RedisPassword.of(this.password));
        }

        JedisPoolConfig jedisPoolConfig = getJedisPoolConfig();
        return new JedisConnectionFactory(sentinelConfig, jedisPoolConfig);
    }
}
