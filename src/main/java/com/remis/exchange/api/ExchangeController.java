package com.remis.exchange.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remis.exchange.api.exception.CurrencyIdMismatchException;
import com.remis.exchange.api.exception.CurrencyNotFoundException;
import com.remis.exchange.persistence.model.RateFetchCounter;
import com.remis.exchange.persistence.repo.RateFetchCounterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.remis.exchange.persistence.model.Currency;
import com.remis.exchange.persistence.repo.CurrencyRepository;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeController {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private RateFetchCounterRepo rateFetchCounterRepo;

    @Value("${spring.application.exchange.baseccy}")
    private String baseCcy;

    @GetMapping("/")
    public RateResponse findByCcyAndBaseccyAndDate(@RequestParam("from") String fromCcy, @RequestParam("to") String toCcy, @RequestParam(name="date", required = false) String dateStr) throws ParseException, IOException {
        Date date = new Date();
        if (dateStr != null) {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        }
        RateFetchCounter counter1 = rateFetchCounterRepo.findByCcy(fromCcy);
        incrementCounter(fromCcy, counter1);
        RateFetchCounter counter2 = rateFetchCounterRepo.findByCcy(toCcy);
        incrementCounter(toCcy, counter2);

        Currency ccyRate = getRates(fromCcy, toCcy, date);
        return buildResponse(ccyRate);
    }

    private Currency getRates(String fromCcy, String toCcy, Date date) throws IOException {
        Currency ccyRate = null;

        if(fromCcy.equals(baseCcy)){
            List<Currency> ccyList = fetchRatesFromDB(fromCcy, toCcy, date);
            ccyRate = ccyList.get(0);
        }else if(toCcy.equals(baseCcy)){
            List<Currency> ccyList = fetchRatesFromDB(toCcy, fromCcy, date);
            ccyRate = ccyList.get(0);
        }else{
            List<Currency> ccyList1 = fetchRatesFromDB(baseCcy, fromCcy, date);
            List<Currency> ccyList2 = fetchRatesFromDB(baseCcy, toCcy, date);
            ccyRate = calculateRate(ccyList1.get(0), ccyList2.get(0));
        }
        return ccyRate;
    }

    private List<Currency> fetchRatesFromDB(String baseCcy, String toCcy, Date date){
        List<Currency> ccyList = currencyRepository.findByBaseccyAndCcyAndDate(baseCcy, toCcy, date);
        if (ccyList.isEmpty()){
            throw new CurrencyNotFoundException("Currency Rate Not Found");
        }
        return ccyList;
    }

    private Currency calculateRate(Currency fromccyRate, Currency toccyRate) throws IOException {
        Currency ccyRate = new Currency();
        Double spread = getSpread(fromccyRate.getCcy(), toccyRate.getCcy());
        Double rate = (toccyRate.getRate()/fromccyRate.getRate()) * ( (100 - spread) / 100);
        ccyRate.setBaseccy(fromccyRate.getCcy());
        ccyRate.setCcy(toccyRate.getCcy());
        ccyRate.setRate(rate);
        return ccyRate;
    }

    private Double getSpread(String fromCCy, String toCCy) throws IOException {
        File jsonFile = new ClassPathResource("ccy_spread/spread.json").getFile();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(jsonFile);
        Double fromspread = node.has(fromCCy) ? node.get(fromCCy).asDouble() : node.get("___").asDouble();
        Double tospread = node.has(toCCy) ? node.get(toCCy).asDouble() : node.get("___").asDouble();
        Double spread = java.lang.Math.max(fromspread, tospread);
        return spread;
    }

    private synchronized void incrementCounter(String ccy, RateFetchCounter counter){
        if (counter == null){
            counter = new RateFetchCounter();
            counter.setCcy(ccy);
        }
        counter.setFetchCount(counter.getFetchCount()+1);
        rateFetchCounterRepo.save(counter);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Currency create(@RequestBody Currency currency) {
        Currency currency1 = currencyRepository.save(currency);
        return currency1;
    }

    @PutMapping("/{id}")
    public Currency updatecurrency(@RequestBody Currency currency, @PathVariable long id) {
        if (currency.getId() != id) {
            throw new CurrencyIdMismatchException();
        }
        currencyRepository.findById(id)
                .orElseThrow(CurrencyNotFoundException::new);
        return currencyRepository.save(currency);
    }

    public class RateResponse{
        String from;
        String to;
        Double exchange;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public Double getExchange() {
            return exchange;
        }

        public void setExchange(Double exchange) {
            this.exchange = exchange;
        }
    }

    private RateResponse buildResponse(Currency ccyRate){
        RateResponse rateResponse = new RateResponse();
        rateResponse.from = ccyRate.getBaseccy();
        rateResponse.to = ccyRate.getCcy();
        rateResponse.exchange = ccyRate.getRate();
        return rateResponse;
    }
}
