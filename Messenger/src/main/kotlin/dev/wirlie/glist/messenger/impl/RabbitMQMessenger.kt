/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2024 Josue Acevedo and the Enhanced Glist contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: wirlie.dev@gmail.com
 */

package dev.wirlie.glist.messenger.impl

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import dev.wirlie.glist.messenger.PlatformMessenger
import dev.wirlie.glist.messenger.api.MessengerLogger

class RabbitMQMessenger(
    logger: MessengerLogger,
    val host: String,
    val port: Int,
    private val userName: String,
    val password: String,
    isProxy: Boolean
): PlatformMessenger(logger) {

    private val receiveExchangeName = if(isProxy) "egl-proxy" else "egl-servers"
    private val sendExchangeName = if(isProxy) "egl-servers" else "egl-proxy"
    private lateinit var factory: ConnectionFactory
    private var receiveConnection: Connection? = null

    override fun register() {
        factory = ConnectionFactory()
        factory.host = host
        factory.port = port
        factory.username = userName
        factory.password = password

        logger!!.info("[RabbitMQ] Starting communication...")

        receiveConnection = factory.newConnection()
        val channel = receiveConnection!!.createChannel()
        channel.exchangeDeclare(receiveExchangeName, "fanout")
        val queueName = channel.queueDeclare().queue
        channel.queueBind(queueName, receiveExchangeName, "")

        logger.info("[RabbitMQ] Channels created, waiting for incoming messages...")

        val deliverCallback = DeliverCallback { _, delivery: Delivery ->
            val unpacked = unpackMessage(delivery.body)
            receiveMessage(
                "",
                unpacked.first,
                unpacked.second,
                null,
                null
            )
        }

        channel.basicConsume(queueName, true, deliverCallback) { _: String -> }
    }

    override fun unregister() {
        logger!!.info("[RabbitMQ] Removing connection...")
        receiveConnection?.close(100)
    }

    override fun sendMessage(subject: String, data: ByteArray, targetSenderObject: String?) {
        factory.newConnection().use { connection ->
            val channel = connection.createChannel()
            channel.exchangeDeclare(sendExchangeName, "fanout")
            channel.basicPublish(sendExchangeName, "", null, packMessage(subject, data))
        }
    }
}
