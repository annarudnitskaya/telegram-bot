
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import org.telegram.telegrambots.meta.exceptions.TelegramApiException


fun main(){
    try {
        val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
        botsApi.registerBot(Bot())
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}

