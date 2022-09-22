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

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.UUID

abstract class PlatformMessenger {

    private val messagesRegistry = mutableSetOf<Pair<String, Class<out SerializableMessage>>>()
    private val listeners = mutableSetOf<MessageListener<out SerializableMessage>>()

    abstract fun register()

    abstract fun unregister()

    abstract fun sendMessage(
        subject: String,
        data: ByteArray,
        targetSenderObject: String?
    )

    fun sendMessage(
        message: SerializableMessage,
        targetSenderObject: String?
    ) {
        val messageSubject = messagesRegistry.firstOrNull { it.second == message::class.java }?.first ?:
            throw IllegalArgumentException("${message::class.java} is not registered as message.")

        sendMessage(
            messageSubject, message.serialize(), targetSenderObject
        )
    }

    fun receiveMessage(
        channel: String,
        subject: String,
        data: ByteArray,
        fromPlayer: UUID?,
        fromServerId: String?
    ) {
        val messageClass = messagesRegistry.firstOrNull { it.first == subject }?.second
            ?: throw IllegalStateException("Received unknown message from Messenger, channel=$channel, subject=$subject")

        val messageInstance = messageClass.getDeclaredConstructor().newInstance()
        messageInstance.deserialize(data)

        // Listeners
        for(listener in listeners) {
            tryHandle(messageClass, listener, messageInstance, fromPlayer, fromServerId)
        }
    }

    private fun tryHandle(
        clazz: Class<out SerializableMessage>,
        listener: MessageListener<out SerializableMessage>,
        message: SerializableMessage,
        fromPlayer: UUID?,
        fromServerId: String?
    ) {
        if(clazz == listener.clazz) {
            println("CLASS MATCH FOR LISTENER")
            listener.onMessageInternal(message, fromPlayer, fromServerId)
        } else {
            println("CLASS NOT MATCH: clazz=$clazz vs T=${listener.clazz}")
        }
    }

    fun packMessage(
        subject: String,
        data: ByteArray
    ): ByteArray {
        val bout = ByteArrayOutputStream()
        val out = DataOutputStream(bout)
        out.writeUTF(subject)
        out.write(data)
        out.close()
        bout.close()
        return bout.toByteArray()
    }

    fun unpackMessage(
        packedData: ByteArray
    ): Pair<String, ByteArray> {
        val bin = ByteArrayInputStream(packedData)
        val input = DataInputStream(bin)
        val subject = input.readUTF()
        val data = input.readBytes()
        input.close()
        bin.close()
        return Pair(subject, data)
    }

    fun addListener(
        listener: MessageListener<out SerializableMessage>
    ): Boolean {
        return listeners.add(listener)
    }


    fun removeListener(
        listener: MessageListener<out SerializableMessage>
    ): Boolean {
        return listeners.remove(listener)
    }

    fun registerMessage(subject: String, message: Class<out SerializableMessage>): Boolean {
        unregisterMessage(subject)
        return messagesRegistry.add(Pair(subject, message))
    }

    fun unregisterMessage(subject: String): Boolean {
        return messagesRegistry.removeIf { it.first == subject }
    }

}
