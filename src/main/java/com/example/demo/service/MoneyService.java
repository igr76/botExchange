package com.example.demo.service;

import com.example.demo.exception.ServiceException;
/** Сервис счетов */
public interface MoneyService {
     /** Получить состояние счетов */
     String getMoney(long chatId);
     /** Положить средства */
     void saveMoney(long chatId,int id,double money);
     /** Обмен средств счетов по курсу */
     String exchange(long chatId,String id,double money) throws ServiceException;

}
