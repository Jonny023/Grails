package com.atgenee

import com.budjb.rabbitmq.RabbitContext
import com.budjb.rabbitmq.publisher.RabbitMessagePublisher
import grails.converters.JSON

class MessageController {

    RabbitMessagePublisher rabbitMessagePublisher

    RabbitContext rabbitContext

    def index() {
        render "处理完成"
    }

    def sendMessage() {

//        render rabbitMessagePublisher.rpc {
//            exchange = "test-exchange"
//            routingKey = "test.#"
//            body = "Hello!"
//            timeout = 5000
//        }
        rabbitMessagePublisher.send {
            routingKey = "test.#"
            body = "队列测试"
        }
        render "创建一条信息"

    }

    def send() {
        render rabbitMessagePublisher.rpc {
            routingKey = "test.#"
            body = "这是一个测试队列"
        }
    }

    def user() {
        def map = [id: 1,name: "张三",ramark: "hello world"]
        render rabbitMessagePublisher.rpc {
            routingKey = "userQueue"
            body = map
        }
    }

    def report() {
        rabbitContext
        render rabbitContext.getSt
        atusReport() as JSON
    }
}
