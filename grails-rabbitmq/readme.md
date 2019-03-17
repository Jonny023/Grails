# Grails-RabbitMQ demo

[windows rabbitmq安装](https://github.com/Jonny023/Study/blob/master/rabbit/Windows%20RabbitMQ%E5%AE%89%E8%A3%85.md)

* `Grails 4`
* `rabbitmq-native 3.3.3`

### 测试的时候用`3.4.5`和`3.4.4`报错

[参考1](http://guides.grails.org/grails-rabbitmq/guide/index.html)

[参考2](http://budjb.github.io/grails-rabbitmq-native/3.x/latest/)

> 创建消费命令

```
create-consumer com.atgenee.User
```


> 创建自定义转换器

```
grails create-converter com.atgenee.CustomConverter
```
