package com.github.kr328.webapi.bot.bot

import com.github.kr328.webapi.bot.bot.network.Client
import com.github.kr328.webapi.bot.bot.network.updates.Update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class Bot(token: String) : CoroutineScope {
    override val coroutineContext = SupervisorJob()
    private val client = Client(token)

    suspend fun exec() {
        coroutineScope {
            var offset = 0L

            while (client.isActive) {
                val updates = client.getUpdates(offset)

                for (update in updates) {
                    launch {
                        handleUpdate(update)
                    }
                }

                offset = updates.lastOrNull()?.updateId?.and(1) ?: offset
            }
        }
    }

    fun shutdown() {
        coroutineContext.cancel()
        client.shutdown()
    }

    private suspend fun handleUpdate(update: Update) {
        
    }
}