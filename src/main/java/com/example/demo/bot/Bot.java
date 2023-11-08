package com.example.demo.bot;

import com.example.demo.service.ExchangeRatesService;
import com.example.demo.service.MoneyService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/** Управление командами бота */
@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {
    @Autowired
    ExchangeRatesService exchangeRatesService;
    @Autowired
    private MoneyService moneyService;
    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;
    private static final String START = "/start";
    private static final String USD = "/usd";
    private static final String EUR = "/eur";
    private static final String HELP = "/help";
    private static final String GET = "/getMoney";
    private static final String SET_MONEY = "/setMoney";
    private static final String EXCHANGE_MONEY = "/exchange";
//    private static final String SET_RUB = "/setRub";
//    private static final String SET_USD = "/setUsd";
//    private static final String SET_EUR = "/setEur";



    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String messageTextAfter = update.getMessage().getText();
        String[] textArray = messageTextAfter.split(" ");
        String message = textArray[0];
        var chatId = update.getMessage().getChatId();
        switch (message) {
            case START -> {
                String userName = update.getMessage().getChat().getUserName();
                startCommand(chatId, userName);
            }
            case USD -> usdCommand(chatId);
            case EUR -> eurCommand(chatId);
            case HELP -> helpCommand(chatId);
            case GET -> getMoneyCommand(chatId);
            case SET_MONEY -> setMoneyCommand(chatId,textArray);
            case EXCHANGE_MONEY -> exchangeMoneyCommand(chatId,textArray);
            default -> unknownCommand(chatId);
        }
    }

    private void exchangeMoneyCommand(Long chatId,String[] text) {
        try {
            sendMessage(chatId, moneyService.exchange(chatId,text[1],Double.parseDouble(text[2])));
        } catch (com.example.demo.exception.ServiceException e) {
            throw new RuntimeException(e);
        }

    }

    private void setMoneyCommand(Long chatId,String[] text) {
        moneyService.saveMoney(chatId,Integer.parseInt(text[1]),Double.parseDouble(text[2]));
    }

    private void getMoneyCommand(Long chatId) {
        sendMessage(chatId,moneyService.getMoney(chatId));
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
                Добро пожаловать в бот, %s!
                
                Здесь Вы сможете узнать официальные курсы валют на сегодня, установленные ЦБ РФ.
                
                Для этого воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                
                Дополнительные команды:
                /help - получение справки
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
    }

    private void usdCommand(Long chatId) {
        String formattedText;
        try {
            var usd = exchangeRatesService.getUSDExchangeRate();
            var text = "Курс доллара на %s составляет %s рублей";
            formattedText = String.format(text, LocalDate.now(), usd);
        } catch (ServiceException | com.example.demo.exception.ServiceException e) {
            log.error("Ошибка получения курса доллара", e);
            formattedText = "Не удалось получить текущий курс доллара. Попробуйте позже.";
        }
        sendMessage(chatId, formattedText);
    }
    private void eurCommand(Long chatId) {
        String formattedText;
        try {
            var usd = exchangeRatesService.getEURExchangeRate();
            var text = "Курс евро на %s составляет %s рублей";
            formattedText = String.format(text, LocalDate.now(), usd);
        } catch (ServiceException | com.example.demo.exception.ServiceException e) {
            log.error("Ошибка получения курса евро", e);
            formattedText = "Не удалось получить текущий курс евро. Попробуйте позже.";
        }
        sendMessage(chatId, formattedText);
    }

    private void helpCommand(Long chatId) {
        var text = """
                Справочная информация по боту
                
                Для получения текущих курсов валют воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                /getMoney
                /setMoney пополнить счёт, после команды номер валюты и сумма 1-rub  2-usd 3-eur
                /exchange - обмен валюты меджу счетами, после команжды номер операции и сумма
                 (через олин пробел) 1:rub-usd  2:rub-eur 3:usd-rub 4:usd-eur 
                  5:eur-rub  6:eur-usd
                """;
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        var text = "Не удалось распознать команду!";
        sendMessage(chatId, text);
    }
    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        ReplyKeyboardMarkup keyboardMarkup =new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row= new KeyboardRow();
        row.add("/getMoney");row.add("/usd");row.add("/eur ");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(keyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }
}
