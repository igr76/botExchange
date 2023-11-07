package com.example.demo.service;


import com.example.demo.exception.ServiceException;
/** Сервис курса валют */
public interface ExchangeRatesService {
    /** Получить курс доллара */
    String getUSDExchangeRate() throws ServiceException;
    /** Получить курс евро */
    String getEURExchangeRate() throws ServiceException;

    void clearUSDCache();

    void clearEURCache();

}
