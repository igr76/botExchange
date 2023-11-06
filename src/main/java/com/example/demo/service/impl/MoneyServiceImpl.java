package com.example.demo.service.impl;

import com.example.demo.exception.ElemNotFound;
import com.example.demo.exception.ServiceException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MoneyService;
import org.springframework.stereotype.Service;

@Service
public class MoneyServiceImpl implements MoneyService {
    UserRepository userRepository;
    ExchangeRatesServiceImpl exchangeRatesService;

    public MoneyServiceImpl(UserRepository userRepository, ExchangeRatesServiceImpl exchangeRatesService) {
        this.userRepository = userRepository;
        this.exchangeRatesService = exchangeRatesService;
    }

    @Override
    public String getMoney(long chatId) {
        User user=new User();
        try {
            user= userRepository.findById(chatId).orElseThrow(ElemNotFound::new);
        } catch (ElemNotFound e) {return "Ваш счёт ещё не создан";}

        return "Ваш счёт : "+ user.getBalanceRub() +" p \n"+ user.getBalanceUsd()+" u \n"+
                user.getBalanceEur()+" e";
    }


    @Override
    public void saveMoney(long chatId, int id, double money) {
        User user=new User();
        try {
            user = userRepository.findById(chatId).orElseThrow(ElemNotFound::new);
        }catch (ElemNotFound e){user.setChatId(chatId);}

        if (id == 1) {
            user.setBalanceRub(user.getBalanceRub()+money);
            userRepository.save(user);
        } else if (id == 2) {
            user.setBalanceUsd(user.getBalanceUsd()+money);
            userRepository.save(user);
        } else if (id == 3) {
            user.setBalanceEur(user.getBalanceEur()+money);
            userRepository.save(user);
        }
    }

    @Override
    public String exchange(long chatId, String id, double money)  {
        User user=new User();
        user = userRepository.findById(chatId).orElseThrow(ElemNotFound::new);
        try {
        switch (id) {
            case "1" ->{if (user.getBalanceRub()-money>=0){  /* rub-usd */
                user.setBalanceUsd(user.getBalanceUsd()+money/getUsd());
                user.setBalanceRub(user.getBalanceRub()-money);
                // userRepository.save(user);
                return "обмен произведен";}
            else return "для операции нехватает средств";}
            case "2" ->{if (user.getBalanceRub()-money>=0){/* rub-eur */
                user.setBalanceEur(user.getBalanceEur()+money/getEur());
                user.setBalanceRub(user.getBalanceRub()-money);
                userRepository.save(user);return "обмен произведен";}
            else return "для операции нехватает средств";}
            case "3" ->{if (user.getBalanceUsd()-money>=0){  /* usd-rub */
                user.setBalanceRub(user.getBalanceRub()+money*getUsd());
                user.setBalanceUsd(user.getBalanceUsd()-money);
                userRepository.save(user);return "обмен произведен";}
            else return "для операции нехватает средств";}
            case "4" ->{if (user.getBalanceUsd()-money>=0){  /* usd-eur */
                user.setBalanceEur(user.getBalanceEur()+money*getUsd()/getEur());
                user.setBalanceUsd(user.getBalanceUsd()-money);
                userRepository.save(user);return "обмен произведен";}
            else return "для операции нехватает средств";}
            case "5" ->{if (user.getBalanceEur()-money>=0){ /* eur-rub */
                user.setBalanceRub(user.getBalanceRub()+money*getUsd());
                user.setBalanceEur(user.getBalanceEur()-money);
                userRepository.save(user);return "обмен произведен";}
            else return "для операции нехватает средств";}
            case "6" ->{if (user.getBalanceEur()-money>=0){ /* eur-usd */
                user.setBalanceUsd(user.getBalanceUsd()+money/getUsd()/getEur());
                user.setBalanceEur(user.getBalanceEur()-money);
                userRepository.save(user);return "обмен произведен";}
            else return "для операции нехватает средств";}
        }} catch (ServiceException e) {
            throw new RuntimeException(e);
        }

        return "Неверный формат команды: /exchange 1 1234.00 (с одним пробелом между)";
    }

    String response() {
        return "Недостаточно средств на счете для операции";
    }

    double getUsd() throws ServiceException {
      return   Double.parseDouble(exchangeRatesService.getUSDExchangeRate());
    }
    double getEur() throws ServiceException {
        return   Double.parseDouble(exchangeRatesService.getEURExchangeRate());
    }
}
