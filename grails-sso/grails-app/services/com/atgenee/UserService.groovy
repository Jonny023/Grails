package com.atgenee

import grails.gorm.transactions.Transactional

@Transactional
class UserService {

    def initData() {
        if(User.count() == 0) {

        }
    }
}
