package com.atgenee

import com.budjb.rabbitmq.consumer.MessageContext

class UserConsumer {

    static rabbitConfig = [
        queue: "userQueue"
    ]

    /**
     * Handle an incoming RabbitMQ message.
     *
     * @param body    The converted body of the incoming message.
     * @param context Properties of the incoming message.
     * @return
     */
    def handleMessage(def body, MessageContext messageContext) {

        println messageContext.channel
        println messageContext.consumerTag
        println messageContext.properties
        // TODO: Handle messages
        body
    }
}
