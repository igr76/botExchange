package com.example.demo.service;

import com.example.demo.exception.ServiceException;

public interface MoneyService {
     String getMoney(long chatId);
     void saveMoney(long chatId,int id,double money);
     String exchange(long chatId,String id,double money) throws ServiceException;

}
