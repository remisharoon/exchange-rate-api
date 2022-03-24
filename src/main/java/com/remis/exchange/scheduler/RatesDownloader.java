package com.remis.exchange.scheduler;

import com.remis.exchange.persistence.model.Currency;
import com.remis.exchange.sources.Fixer;
import com.remis.exchange.sources.RateSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component("RatesDownloader")
public class RatesDownloader {

    @Scheduled(cron = "0 5 0 * * *")
    private void downloadLatestRates(){
        System.out.println("--------downloadLatestRates-----------");
        RateSource source = new Fixer();
        List<Currency> ccyList = source.getLatestRates();
        source.saveRates(ccyList);
    }

}
