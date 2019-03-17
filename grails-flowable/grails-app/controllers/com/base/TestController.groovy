package com.base

class TestController {

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
}
