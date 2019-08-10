package com.jonny

import com.common.CookieUtil
import grails.plugins.redis.RedisService

/**
 *  退出登陆了
 */
class LogoutController {

    RedisService redisService

    def index() {
        String loginToken = CookieUtil.readLoginToken(request)
        if (loginToken) {
            // 移除cookie
            CookieUtil.delLoginToken(request, response)
            // 移除redis
            redisService.deleteKeysWithPattern(loginToken)
        }
        render view: '/login/index'
    }
}