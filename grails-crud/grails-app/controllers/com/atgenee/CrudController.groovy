package com.atgenee

import grails.converters.JSON
import org.hibernate.Session

class CrudController {

    /**
     *  纯SQL查询并按昵称升序
     *  英文最先排序，然后是中文
     */
    def list = {
        def lists = User.withSession { Session session ->
            session.createSQLQuery("select * from user u order by convert(u.nickname using gbk) ").addEntity(User)
        }.list()
        render lists as JSON
    }

    /**
     *  SQL查询并按昵称升序
     *  英文最先排序，然后是中文
     */
    def enAndZh = {
        def lists = User.withSession { Session session ->
            session.createSQLQuery("select * from user u order by convert(u.nickname using gbk) collate gbk_chinese_ci").addEntity(User)
        }.list()
        render lists as JSON
    }

    /**
     *  H自定义方言实现HQL昵称排序
     *  英文最先排序，然后是中文
     */
    def sort = {
        def list = User.executeQuery("from User u order by convert_gbk(u.nickname) asc")
        render list as JSON
    }

}
