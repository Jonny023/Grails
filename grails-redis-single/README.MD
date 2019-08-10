## Grails + Redis + Cookie实现单点登录

参考地址：https://juejin.im/post/5c64dafb6fb9a04a0f65aed9

#### 原理说明

* 用户首次登录，登录成功，生成`token`，将`token`存入`cookie`作为`cookie`的`value`
* 将`token`作为`redis`的`key`，用户对象转为`json`字符串存到`redis`中，过期时间设置为：30分钟
* 添加拦截器，获取`cookie`，拿到`cookie`到`redis`中获取用户信息，若有值则代表已经登录，否则跳转登录页面
* 注销登录，同时清除`cookie`、移除`redis`

#### 打包为jar说明

* 注释掉`build.gradle`中的`apply plugin:"war"`
* 运行`grails war`打包`jar`

> `cookie`设置的`domain`为：`127.0.0.1`，所以测试只能访问`127.0.0.1`进行测试

#### 运行测试
```shell
java -jar -Dserver.port=8081 grails-redis-single.jar
java -jar -Dserver.port=8082 grails-redis-single.jar
java -jar -Dserver.port=8083 grails-redis-single.jar
```