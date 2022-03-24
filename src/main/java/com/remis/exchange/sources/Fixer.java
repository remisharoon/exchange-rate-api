package com.remis.exchange.sources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remis.exchange.persistence.model.Currency;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class Fixer extends RateSource {

    private static final Properties properties;
    static {
        properties = new Properties();
        try {
            ClassLoader classLoader = Fixer.class.getClassLoader();
            InputStream applicationPropertiesStream = classLoader.getResourceAsStream("application.properties");
            properties.load(applicationPropertiesStream);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    private static final String URL_TEMPLATE = "http://data.fixer.io/api/latest?access_key=%s";
    private String URL;
    private void setUrl(){
        String apiKey = properties.getProperty("spring.application.ratesource.apikey");
        URL = String.format(URL_TEMPLATE, apiKey);
    }

    public Fixer() {
        setUrl();
    }


    @Override
    public List<Currency> getLatestRates() {
        String jsonResponse = getResponse(URL);
        List<Currency> ccyList = parseJsonString(jsonResponse);
        return ccyList;
    }

    private String getResponse(String uri){
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        System.out.println(result);
        return result;
    }

    private List<Currency> parseJsonString(String jsonString){
        List<Currency> ccyList = new ArrayList<>();
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            String base = jsonNode.get("base").asText();
            Long timestamp = jsonNode.get("timestamp").asLong();
            java.sql.Date date = new java.sql.Date(timestamp * 1000);
            System.out.println(base);
            Map<String, Double> rates = objectMapper.convertValue(jsonNode.get("rates"), new TypeReference<Map<String, Double>>(){});
            System.out.println(rates);

            for (Map.Entry<String, Double> entry:rates.entrySet()) {
                String ccy = entry.getKey();
                Double rate = entry.getValue();
                Currency currency = new Currency();
                currency.setBaseccy(base);
                currency.setCcy(ccy);
                currency.setRate(rate);
                currency.setDate(date);
                ccyList.add(currency);
            }
        }catch (Exception e){

        }
        return ccyList;
    }

    public static void main(String[] args) {
        Fixer f = new Fixer();
        List<Currency> ccyList = f.getLatestRates();
        ccyList.forEach(ccy -> System.out.println(ccy));
        f.saveRates(ccyList);
    }
}
