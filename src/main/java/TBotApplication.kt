import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class Bot : TelegramLongPollingBot() {
    override fun getBotToken(): String = "5748221174:AAGXp_jZh_Wpy2BgX_Vai_cOZppFQwtAUpE"
    override fun getBotUsername(): String = "PharmacyBot_kotlinR_bot"

    private val connection: Connection
    init {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
        val connectionUrl =
                "jdbc:sqlserver://localhost:1433;encrypt=true;database=Pharmacy;trustServerCertificate=true;"
        connection = DriverManager.getConnection(connectionUrl, "KotlinR", "12345")
    }

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

                        "Все препараты" -> {
                            getPreparations(chatId)
                            return
                        }

                        "Узнать цену препарата" -> {
                            getPrice(chatId)
                            return
                        }

                        "Выбрать тип препарата" -> {
                            "Доступные варианты:"
                        }

                        "Жаропонижающее"->  {
                            getPreparationByType(chatId, 1)
                            return
                        }

                        "Антигистоминное"->  {
                            getPreparationByType(chatId, 2)
                            return
                        }

                        "Обезболивающее"->  {
                            getPreparationByType(chatId, 3)
                            return
                        }

                        "Седативное"->  {
                            getPreparationByType(chatId, 4)
                            return
                        }

                        else -> "Вы ввели: $messageText"

                    }
                }
                else {
                        "Извините, но я понимаю только текст"
                    }
                sendNotification(chatId, responseText)
            }

        }
    }

    private fun sendNotification(chatId: Long, responseText: String) {
        val responseMessage = SendMessage(chatId.toString(), responseText)
        responseMessage.enableMarkdown(true)
        responseMessage.replyMarkup =
                when (responseText) {
                    "Выбрать тип препарата" -> getReplyMarkup(
                            listOf(
                                    listOf("Жарапонижающее", "Антигистоминное"),
                                    listOf("Обезболивающее", "Седативное")
                            )
                    )
                else -> getReplyMarkup(
            listOf(
                listOf("Все препараты", "Выбрать тип препарата"),
                listOf( "Узнать цену препарата")
            )
        )
        }
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

    private fun getPreparations(chatId: Long) {
        val statement: Statement = connection.createStatement()
        val result: ResultSet = statement.executeQuery("SELECT  Name, Availability From phrm.PharmaceuticalPreparations")
        var output: String = ""
        var i:Int=1
        while (result.next()) {
            output += i.toString()+") "+ result.getString(1) + ", в наличии - " + result.getInt(2)  + '\n'
            i++
        }
        println(output)
        execute(SendMessage(chatId.toString(), output))
    }

    private fun getPreparationByType(chatId: Long, Type: Int) {
        val statement: Statement = connection.createStatement()
        val result: ResultSet = statement.executeQuery("SELECT  Name, Availability, Price, TypeOfExposureID From phrm.PharmaceuticalPreparations")
        var output: String = ""
        var i:Int=1
        while (result.next()) {
            if (result.getInt(4) == Type){
            output += i.toString()+") "+ result.getString(1) + ", в наличии - " + result.getInt(2) + ", цена - " + result.getInt(3) +" руб. "  + '\n'
            i++
            }
        }
        println(output)
        execute(SendMessage(chatId.toString(), output))
    }


    private fun getPrice(chatId: Long) {

        val statement: Statement = connection.createStatement()
        val result: ResultSet = statement.executeQuery("SELECT  Name, Availability, Price From phrm.PharmaceuticalPreparations")
        var output: String = ""
        var i:Int=1

        while (result.next()) {
            output += i.toString()+") " + result.getString(1) + ", в наличии - " + result.getInt(2) + ", цена - " + result.getInt(3) +" руб. " + '\n'
            i++
        }
        println(output)
        execute(SendMessage(chatId.toString(), output))
    }
}