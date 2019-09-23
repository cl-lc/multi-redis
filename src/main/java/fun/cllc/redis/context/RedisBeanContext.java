package fun.cllc.redis.context;

import fun.cllc.redis.client.RedisClientDecorate;
import fun.cllc.redis.config.RedisConfigParser;
import fun.cllc.redis.config.model.BaseRedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @author chenlei
 * @date 2019-09-09
 */
@Slf4j
@Configuration
public class RedisBeanContext implements EnvironmentAware, BeanFactoryPostProcessor {
    private static final String REDIS_CLIENT_NAME_POSTFIX = "RedisClient";
    private static final String REDIS_CLIENT_DEFAULT_NAME = "redisClient";

    private DefaultListableBeanFactory beanFactory;
    private List<BaseRedisConfig> redisConfigs;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
        this.redisConfigs.forEach(this::injectRedis);
    }

    /**
     * 注入redis client
     *
     * @param redisConfig
     */
    private void injectRedis(BaseRedisConfig redisConfig) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        RedisConnectionFactory factory = redisConfig.buildJedisConnectionFactory();
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RedisClientDecorate.class);
        builder.addPropertyValue(RedisClientDecorate.REDIS_TEMPLATE_PROP, template);

        String name = redisConfig.getName();
        name = name.isEmpty() ? REDIS_CLIENT_DEFAULT_NAME : name + REDIS_CLIENT_NAME_POSTFIX;
        this.beanFactory.registerBeanDefinition(name, builder.getBeanDefinition());

        log.info("Injected redis: {}", name);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.redisConfigs = RedisConfigParser.parse(environment);
    }
}
