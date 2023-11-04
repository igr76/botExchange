package com.example.demo.scheduler;

import com.example.demo.service.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InvalidationScheduler {

    @Autowired
    private ExchangeRatesService service;

    @Scheduled(cron = "* 0 0 * * ?")
    public void invalidateCache() {
        service.clearUSDCache();
        service.clearEURCache();
    }
}
