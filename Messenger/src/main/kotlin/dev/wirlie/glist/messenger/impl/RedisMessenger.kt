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

package dev.wirlie.glist.messenger.impl

import dev.wirlie.glist.messenger.PlatformMessenger
import dev.wirlie.glist.messenger.api.MessengerLogger
import dev.wirlie.glist.messenger.util.DataUtil
import dev.wirlie.glist.messenger.util.NettyFixUtil
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.pubsub.RedisPubSubListener
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands
import io.netty.util.AttributeKey

class RedisMessenger(
    logger: MessengerLogger,
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
    val proxy: Boolean
): PlatformMessenger(logger), RedisPubSubListener<String, String> {

    val incomingChannel = if(proxy) "egl-proxy" else "egl-server"
    val outgoingChannel = if(proxy) "egl-server" else "egl-proxy"

    var receiveConnection: RedisPubSubAsyncCommands<String, String>? = null
    var sendConnection: RedisPubSubAsyncCommands<String, String>? = null

    var client = RedisClient.create(
        RedisURI.Builder.redis(host, port).also {
            if(user.isNotEmpty()) {
                it.withAuthentication(user, password.toCharArray())
            } else if(password.isNotEmpty()) {
                it.withPassword(password.toCharArray())
            }
        }.build()
    )

    override fun register() {
        //Uncomment if ['RedisURI' is already used] is a problem to resolve...
        //NettyFixUtil.unregisterLettuceRedisURI()

        try {
            logger!!.info("[Redis] Registering connections...")
            client.connect().sync().ping()
            receiveConnection = client.connectPubSub().async()
            receiveConnection!!.statefulConnection.addListener(this)
            receiveConnection!!.subscribe(incomingChannel)
            sendConnection = client.connectPubSub().async()
            logger.info("[Redis] Connection established.")
        } catch (ex: Throwable) {
            // Always unregister on exception
            unregister()
            throw ex
        }
    }

    override fun unregister() {
        logger!!.info("[Redis] Closing connection...")
        try {
            client.shutdown()
            receiveConnection?.unsubscribe(incomingChannel)
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override fun sendMessage(subject: String, data: ByteArray, targetSenderObject: String?) {
        val dataToSend = DataUtil.bytesToString(packMessage(subject, data))
        sendConnection!!.publish(outgoingChannel, dataToSend)
    }

    override fun message(channel: String, message: String) {
        val dataReceived = DataUtil.stringToBytes(message)
        val unpacked = unpackMessage(dataReceived)

        receiveMessage(
            channel,
            unpacked.first,
            unpacked.second,
            null,
            null
        )
    }

    override fun message(pattern: String, channel: String, message: String) {

    }

    override fun subscribed(channel: String, count: Long) {
        logger!!.info("[Redis] Awaiting incoming messages...")
    }

    override fun psubscribed(pattern: String, count: Long) {

    }

    override fun unsubscribed(channel: String, count: Long) {
        logger!!.info("[Redis] Unsubscribed from channel...")
    }

    override fun punsubscribed(pattern: String, count: Long) {

    }


}
