package com.jonny

import grails.converters.JSON
import grails.gorm.transactions.Transactional

@Transactional
class UserService {

    /**
     *  初始化用户数据
     */
    void init() {
        if (User.count() == 0) {
            new User(username: "admin", password: "admin".encodeAsMD5()).save(flush: true)
        }
    }

    def login(params) {
        def user = User.where {
            username == params.username && password == params.password.encodeAsMD5()
        }.get()
        return user
    }
}
