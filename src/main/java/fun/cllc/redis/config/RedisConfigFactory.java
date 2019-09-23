package fun.cllc.redis.config;


import fun.cllc.redis.config.model.*;

import java.util.Set;

/**
 * @author chenlei
 * @date 2019-09-10
 */
public class RedisConfigFactory {
    /**
     * 根据配置key进行实例化
     *
     * @param keys
     * @return
     */
    public static BaseRedisConfig getInstance(Set<String> keys) {
        boolean hasClusterConfig = false;
        boolean hasSentinelConfig = false;
        for (String key : keys) {
            if (key.startsWith(RedisPoolConfig.CONFIG_PREFIX)) {
                continue;
            }

            if (key.startsWith(ClusterRedisConfig.CONFIG_PREFIX)) {
                hasClusterConfig = true;
                break;
            } else if (key.startsWith(SentinelRedisConfig.CONFIG_PREFIX)) {
                hasSentinelConfig = true;
                break;
            }
        }

        if (hasClusterConfig) {
            return new ClusterRedisConfig();
        } else if (hasSentinelConfig) {
            return new SentinelRedisConfig();
        } else {
            return new StandaloneRedisConfig();
        }
    }
}
