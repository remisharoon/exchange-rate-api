package com.remis.exchange.sources;

import com.remis.exchange.persistence.model.Currency;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public abstract class RateSource{

    public RateSource(){

    }

    protected List<Currency> getRates(String uri){
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        System.out.println(result);
        return null;
    };

    public void saveRates(List<Currency> ccyList){
        ccyList.forEach(ccy -> System.out.println(ccy));
        final String uri = "http://localhost:8080/api/exchange/";
        RestTemplate restTemplate = new RestTemplate();
        ccyList.forEach( ccy -> restTemplate.postForEntity(uri, ccy, Currency.class));
    }

    public abstract List<Currency> getLatestRates();

}
