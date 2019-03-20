package com.atgenee

class User {

    String nickname
    String sex

    static constraints = {
        sex inList: ['男','女']
    }
}
