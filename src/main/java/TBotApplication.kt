import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class Bot : TelegramLongPollingBot() {
    override fun getBotToken(): String = "5748221174:AAGXp_jZh_Wpy2BgX_Vai_cOZppFQwtAUpE"
    override fun getBotUsername(): String = "PharmacyBot_kotlinR_bot"

    override fun onUpdateReceived(update: Update?) {
        if (update != null) {
            if (update.hasMessage()) {
                val message = update!!.message
                val chatId = message.chatId
                val responseText = if (message.hasText()) {
                    var messageText = message.text
                    when (messageText) {
                        "/start" -> {
                            "Категорически приветствуем!"
                        }
                        else -> "Вы ввели: $messageText"

                    }
                } else {
                    "Извините, но я понимаю только текст"
                }
                sendNotification(chatId, responseText)
            }
        }
    }

    private fun sendNotification(chatId: Long, responseText: String) {
        val responseMessage = SendMessage(chatId.toString(), responseText)
        responseMessage.enableMarkdown(true)
        responseMessage.replyMarkup = getReplyMarkup(
            listOf(
                listOf("Жаропонижающее", "Антигистоминное"),
                listOf("Обезболивающее", "Седативное")
            )
        )
        execute(responseMessage)
    }

    private fun getReplyMarkup(allButtons: List<List<String>>): ReplyKeyboardMarkup {
        val markup = ReplyKeyboardMarkup()
        markup.keyboard = allButtons.map { rowButtons ->
            val row = KeyboardRow()
            rowButtons.forEach { rowButton -> row.add(rowButton) }
            row
        }
        return markup
    }
}