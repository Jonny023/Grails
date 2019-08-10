package com.jonny

import grails.plugins.redis.RedisService

class PostController {

    // 注入redis服务
    RedisService redisService

    def save() {
        // expire单位为: 秒（s）
        render redisService.memoize("user:1", 20, {
            "abc"
        })
    }

    def index() { }
}
