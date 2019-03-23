package com.atgenee

import grails.gorm.transactions.Transactional

@Transactional
class UserService {

    def initData() {
        if(User.count() == 0) {
            def user = new User("admin", "admin").save()
            def role = new Role("ROLE_ADMIN").save()
            UserRole.create(user, role, true)
        }
    }
}
