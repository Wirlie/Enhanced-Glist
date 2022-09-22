/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2022 Josue Acevedo and the Enhanced Glist contributors
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

package dev.wirlie.glist.messenger

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery

class RabbitMQMessenger(
    val host: String,
    val port: Int
): PlatformMessenger() {

    private val receiveExchangeName = "egl-proxy"
    private val sendExchangeName = "egl-servers"
    private lateinit var factory: ConnectionFactory
    private var receiveConnection: Connection? = null

    override fun register() {
        factory = ConnectionFactory()
        factory.host = "localhost"
        factory.port = port

        receiveConnection = factory.newConnection()
        val channel = receiveConnection!!.createChannel()
        channel.exchangeDeclare(receiveExchangeName, "fanout")
        val queueName = channel.queueDeclare().queue
        channel.queueBind(queueName, receiveExchangeName, "")

        val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
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
        receiveConnection?.close(100)
    }

    override fun sendMessage(subject: String, data: ByteArray, targetSenderObject: String?) {
        factory.newConnection().use { connection ->
            val channel = connection.createChannel()
            channel.exchangeDeclare(sendExchangeName, "fanout")
            channel.basicPublish(sendExchangeName, "", null, packMessage(subject, data));
        }
    }
}
