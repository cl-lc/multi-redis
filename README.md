
默认的spring boot只会注入spring.redis下面的配置，注入为RedisTemplate。
此框架通过扫描yml，支持注入多个Redis bean。

使用方式：在yml中配置多个redis（见application.yml）格式为
* app.redis.abc: redis配置
* app.redis.def: redis配置

代码中注入方式为：
@Autowired
RedisClientDecorate abcRedisClient;

@Autowired
RedisClientDecorate defRedisClient;

如果配置了spring.redis，则默认注入：
@Autowired
RedisClientDecorate redisClient;

RedisClientDecorate为装饰者模式，可以自行添加其他redis的方法
