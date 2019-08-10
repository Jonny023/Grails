package com.jonny

/**
 * @description 用户表
 * @author Jonny
 * @date 2019/8/10
 */
class User {

    String username

    String password

    static constraints = {
        username nullable: false, unique: true, maxSize: 60
        password nullable: false, maxSize: 100
    }
}
