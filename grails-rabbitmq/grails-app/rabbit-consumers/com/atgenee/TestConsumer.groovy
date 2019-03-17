package com.atgenee

import com.budjb.rabbitmq.consumer.MessageContext

class TestConsumer {

    static rabbitConfig = [
        queue: "test.#"
//        binding: "test.#"
    ]

    /**
     * Handle an incoming RabbitMQ message.
     *
     * @param body    The converted body of the incoming message.
     * @param context Properties of the incoming message.
     * @return
     */
    def handleMessage(def body, MessageContext messageContext) {
        println "Hello to you, too!"
        println body
        body
    }
}
