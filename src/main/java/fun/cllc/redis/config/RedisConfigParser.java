package fun.cllc.redis.config;

import fun.cllc.redis.config.model.BaseRedisConfig;
import org.springframework.core.env.*;

import java.util.*;

/**
 * @author chenlei
 * @date 2019-09-10
 */
public class RedisConfigParser {
    private static final String CONFIG_PREFIX_APP_REDIS = "app.redis";
    private static final String CONFIG_PREFIX_SPRING_REDIS = "spring.redis";

    /**
     * 解析环境变量
     *
     * @param env
     * @return
     */
    public static List<BaseRedisConfig> parse(Environment env) {
        Map<String, String> redisConfigs = getAllRedisConfigs(env);
        Map<String, Map<String, String>> combinedRedisConfig = combineRedisConfig(redisConfigs);

        List<BaseRedisConfig> ret = new ArrayList<>();
        combinedRedisConfig.forEach((name, values) -> {
            BaseRedisConfig config = RedisConfigFactory.getInstance(values.keySet());
            config.setName(name);
            values.forEach(config::parseConfig);
            ret.add(config);
        });

        return ret;
    }

    /**
     * 根据name进行分组
     *
     * @param redisConfigs
     * @return
     */
    private static Map<String, Map<String, String>> combineRedisConfig(Map<String, String> redisConfigs) {
        Map<String, Map<String, String>> combinedRedisConfig = new HashMap<>(8);
        redisConfigs.forEach((key, value) -> {
            String name;
            String keyPrefix;
            if (key.startsWith(CONFIG_PREFIX_SPRING_REDIS)) {
                name = "";
                keyPrefix = CONFIG_PREFIX_SPRING_REDIS + ".";
            } else {
                String[] terms = key.split("\\.");
                name = terms[2];
                keyPrefix = CONFIG_PREFIX_APP_REDIS + "." + name + ".";
            }

            Map<String, String> config = combinedRedisConfig.computeIfAbsent(name, k -> new HashMap<>(4));
            config.put(key.substring(keyPrefix.length()), value);
        });

        return combinedRedisConfig;
    }

    /**
     * 获取所有跟Redis相关的配置
     *
     * @param env
     * @return
     */
    private static Map<String, String> getAllRedisConfigs(Environment env) {
        Map<String, String> configs = new HashMap<>(8);
        for (PropertySource<?> propertySource : ((AbstractEnvironment) env).getPropertySources()) {
            if (propertySource instanceof PropertiesPropertySource) {
                MapPropertySource tmp = (MapPropertySource) propertySource;
                configs.putAll(getPropertiesInMapPropertySource(tmp));
            }
            if (propertySource instanceof CompositePropertySource) {
                configs.putAll(getPropertiesInCompositePropertySource((CompositePropertySource) propertySource));
            }
        }

        return configs;
    }

    /**
     * 从composite property source中获取配置
     *
     * @param propertySource
     * @return
     */
    private static Map<String, String> getPropertiesInCompositePropertySource(CompositePropertySource propertySource) {
        Map<String, String> configs = new HashMap<>(8);
        propertySource.getPropertySources().forEach(source -> {
            if (source instanceof MapPropertySource) {
                configs.putAll(getPropertiesInMapPropertySource((MapPropertySource) source));
            }
            if (source instanceof CompositePropertySource) {
                configs.putAll(getPropertiesInCompositePropertySource((CompositePropertySource) source));
            }
        });
        return configs;
    }

    /**
     * 从map property source中获取配置
     *
     * @param propertySource
     * @return
     */
    private static Map<String, String> getPropertiesInMapPropertySource(MapPropertySource propertySource) {
        Map<String, String> configs = new HashMap<>(8);
        propertySource.getSource().forEach((k, v) -> {
            if (k.startsWith(CONFIG_PREFIX_APP_REDIS) || k.startsWith(CONFIG_PREFIX_SPRING_REDIS)) {
                configs.put(k, String.valueOf(v));
            }
        });

        return configs;
    }
}
