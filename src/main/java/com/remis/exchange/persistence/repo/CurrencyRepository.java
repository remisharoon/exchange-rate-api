package com.remis.exchange.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import com.remis.exchange.persistence.model.Currency;

import java.util.Date;
import java.util.List;

public interface CurrencyRepository extends CrudRepository<Currency, Long> {
        List<Currency> findByBaseccyAndCcyAndDate(String fromCcy, String toCcy, Date date);
}