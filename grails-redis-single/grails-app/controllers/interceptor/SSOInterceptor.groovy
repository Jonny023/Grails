package interceptor

import com.common.CookieUtil
import com.common.JsonUtil
import com.jonny.User
import grails.plugins.redis.RedisService
import org.apache.commons.lang.StringUtils

/**
 *  单点登录拦截器
 */
class SSOInterceptor {

    RedisService redisService

    SSOInterceptor() {
        matchAll().excludes(controller: "login", action: "index|auth").excludes(controller: "logout", action: "index")
    }

    boolean before() {
        log.debug("=======================SSO Interceptor===========================")
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotEmpty(loginToken)) {
            String userJsonStr = redisService.getProperty(loginToken);
            User user = JsonUtil.str2Obj(userJsonStr, User.class);
            if (user) {
                // expire单位为: 秒（s）,保留30分钟，token续期
                redisService.memoize(loginToken, 1800, {
                    JsonUtil.obj2Str(user)
                })
            } else {
                redirect(controller: "login", action: 'index')
            }
        } else {
            redirect(controller: "login", action: 'index')
        }
        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
