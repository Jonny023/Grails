// Place your Spring DSL code here
beans = {

    // flowable配置
    processEngineConfiguration(org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration) {
        jdbcUrl = grailsApplication.config.environments.development.dataSource.url
        jdbcDriver = grailsApplication.config.dataSource.driverClassName
        jdbcUsername = grailsApplication.config.dataSource.username
        jdbcPassword = grailsApplication.config.dataSource.password
//        databaseSchemaUpdate = "update" // "true" or "create-drop"
//        asyncExecutorActivate = false
    }
}