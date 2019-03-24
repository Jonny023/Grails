# grails-flowable流程管理demo


## 引入依赖

* 原包为`flowable-spring-boot-starter-basic`，`mvnrepository`仓库提示此包将迁移至`flowable-spring-boot-starter-process`，所以用了如下依赖

```
compile "org.flowable:flowable-spring-boot-starter-process:6.4.1"
compile 'org.flowable:flowable-json-converter:6.4.1'
```

## 配置DSL

* 注意下面数据库链接用的开发环境`grailsApplication.config.environments.development.dataSource.url`,线上用`production`

```groovy
beans = {

    // flowable配置
    processEngineConfiguration(org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration) {
        jdbcUrl = grailsApplication.config.environments.development.dataSource.url
        jdbcDriver = grailsApplication.config.dataSource.driverClassName
        jdbcUsername = grailsApplication.config.dataSource.username
        jdbcPassword = grailsApplication.config.dataSource.password
        //databaseSchemaUpdate = "update" // "true" or "create-drop"
        //asyncExecutorActivate = false
    }
}
```

## `application.yml`中新增配置

* `database-schema-update`配置为update，首次启动配置为`create-drop`，否则报如下错误


## 错误提示

```
Caused by: org.flowable.common.engine.api.FlowableWrongDbException: version mismatch: library version is '6.4.1.3', db version is 5.99.0.0 Hint: Set <property name="databaseSchemaUpdate" to value="true" or value="create-drop" (use create-drop for testing only!) in bean processEngineConfiguration in flowable.cfg.xml for automatic schema creation
```

```yaml

flowable:
    #关闭定时任务JOB
    async-executor-activate: false
    database-schema-update: update
    process:
        definition-cache-limit: 100
```

## 在`src/main/groovy`下新建一个`BaseService`为程序提供基础服务类

```groovy
package com.base

import org.flowable.engine.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 *  flowable基础服务类
 * @Author Lee* @Description
 * @Date 2019年03月18日 0:00
 *
 */

@Service
class BaseService {

    @Autowired
    private RepositoryService repositoryService

    @Autowired
    private RuntimeService runtimeService

    @Autowired
    private TaskService taskService

    @Autowired
    private IdentityService identityService

    @Autowired
    private HistoryService historyService

    @Autowired
    private ManagementService managementService


}

```

## `Application`类加上扫描注解，不然找不到`service`

```groovy
package grails.flowable

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import groovy.transform.CompileStatic
import org.springframework.context.annotation.ComponentScan

@CompileStatic
@ComponentScan("com.*")
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}
```

## 程序中注入使用

```groovy
// 注入基础服务 或者BaseService baseService
def baseService

def index() {
    println baseService.repositoryService
    println baseService.runtimeService
    println baseService.taskService
    println baseService.identityService
    println baseService.historyService
    println baseService.managementService
    render "test"
}
```

## 启动项目

* 访问[http://localhost:8080/flow/index?modelId=50](http://localhost:8080/flow/index?modelId=50)

## 查看流程图

> `processId`是`act_ru_execution`表中的`PROC_INST_ID_`字段

* [http://localhost:8080/flow/details?processId=e6b30dd0-4d74-11e9-9315-005056c00008](http://localhost:8080/flow/details?processId=e6b30dd0-4d74-11e9-9315-005056c00008)
* [表结构关系详见](https://blog.csdn.net/hj7jay/article/details/51302829)
* 查询拥有`PROC_INST_ID_`字段的表

    ```mysql
    SELECT DISTINCT TABLE_NAME FROM information_schema.COLUMNS WHERE COLUMN_NAME = 'PROC_INST_ID_' AND TABLE_SCHEMA='flowable'
    ```

## 流程图

[参考1](https://github.com/hs-web/hsweb-flowable-modeler)

[参考2](https://blog.csdn.net/qq_38425662/article/details/82898198)

[参考3](https://tkjohn.github.io/flowable-userguide/#_introduction)

[参考4](http://www.pianshen.com/article/626055363/)


## 发布定义报错

* 流程id直接定义为数字出现问题

```
cvc-datatype-valid.1.2.1: '1' 不是 'NCName' 的有效值。. Stacktrace follows:
```

* 原因
    * `NCName 要以字母或下划线 () 字符开头，后接 XML 规范中允许的任意字母、数字、重音字符、变音符号、句点 (.)、连字符 (-) 和下划线 () 的组合`
