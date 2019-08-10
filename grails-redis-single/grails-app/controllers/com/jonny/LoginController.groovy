package com.jonny

import com.common.CookieUtil
import com.common.JsonUtil
import grails.converters.JSON
import grails.plugins.redis.RedisService
import org.apache.commons.lang.StringUtils

class LoginController {

    RedisService redisService

    UserService userService

    /**
     *  登录页面
     */
    def index() {

        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotEmpty(loginToken)) {
            String userJsonStr = redisService.getProperty(loginToken);
            User user = JsonUtil.str2Obj(userJsonStr, User.class);
            if (user) {
                // expire单位为: 秒（s）,保留30分钟，token续期
                redisService.memoize(loginToken, 1800, {
                    JsonUtil.obj2Str(user)
                })
                redirect(action: "main")
                return
            }
        }
        render view: '/login/index'
    }

    /**
     *  登录
     */
    def auth() {
        def info = [status: 500, msg: '登录失败']
        def user = userService.login(params)
        if (user) {
            // token写入cookie
            CookieUtil.writeLoginToken(response, session.getId());
            // expire单位为: 秒（s）,保留30分钟
            redisService.memoize(session.getId(), 1800, {
                JsonUtil.obj2Str(user)
            })
            info.status = 200
            info.msg = "登录成功"
            info.token = session.getId()
        }
        render info as JSON
    }

    /**
     *  登录成功页面
     */
    def main() {
        render view: '/login/main', model:[port: request.getServerPort()]
    }
}
