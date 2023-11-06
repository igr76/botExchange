package com.example.demo;

import com.example.demo.exception.ElemNotFound;
import com.example.demo.exception.ServiceException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ExchangeRatesService;
import com.example.demo.service.MoneyService;
import com.example.demo.service.impl.ExchangeRatesServiceImpl;
import com.example.demo.service.impl.MoneyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MoneyServiceImplTest {
    @Mock
    private com.example.demo.repository.UserRepository userRepository;
    @Mock
    private ExchangeRatesServiceImpl exchangeRatesService;
    @InjectMocks
    private MoneyService moneyService = new MoneyServiceImpl(userRepository,  exchangeRatesService);


    private String balance = "Ваш счёт : 123 p \n123 u \n123 e";
    @Test
    void getMoneyTest() {
    when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(greatUser()));
    assertThat(moneyService.getMoney(1)).isEqualTo("Ваш счёт : 123.0 p \n123.0 u \n123.0 e");
    verify(userRepository, times(1)).findById(any());
    }
    @Test
    void getMoneyNegativeTest() {
        when(userRepository.findById(anyLong())).thenThrow(ElemNotFound.class);
        assertThat(moneyService.getMoney(1)).isEqualTo("Ваш счёт ещё не создан");
        verify(userRepository, times(1)).findById(any());
    }
    @Test
    void saveMoneyTest() {
        User user = greatUser();
        user.setBalanceRub(124);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(greatUser()));
        when(userRepository.save(any())).thenReturn(user);
        moneyService.saveMoney(1,1,1);
        verify(userRepository, times(1)).findById(any());
    }
//    @Test
//    void exchangeTest() throws ServiceException {
//        User user1 = greatUser();
//        user1.setBalanceRub(113);
//        user1.setBalanceUsd(123.1);
//        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(greatUser()));
//        when(exchangeRatesService.getUSDExchangeRate()).thenReturn("10");
//        when(userRepository.save(any())).thenReturn(user1);
//        when(moneyService.exchange(1,"1",1)).thenReturn("обмен произведен");
//        verify(userRepository, times(1)).findById(any());
//    }

    User greatUser() {
        User user=new User();
        user.setChatId(1);
        user.setBalanceRub(123);
        user.setBalanceUsd(123);
        user.setBalanceEur(123);
        return user;
    }
}
