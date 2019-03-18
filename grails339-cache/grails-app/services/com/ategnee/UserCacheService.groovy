package com.ategnee

import grails.gorm.services.Service
import grails.plugin.cache.Cacheable

@Service(User)
abstract class UserCacheService implements IUserService {

    Long count() {
        return User.count()
    }

    @Override
    @Cacheable('user')
    User get(Serializable id) {
        return User.get(id)
    }

    @Override
    @Cacheable(value = 'list')
    List<User> list(Map args) {
        return User.list(args)
    }

    @Override
    void delete(Serializable id) {
        get(id).delete(flush: true)
    }

    @Override
    User save(User user) {
        return user.save(flush: true)
    }
}
