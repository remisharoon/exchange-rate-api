package com.remis.exchange.persistence.repo;

import com.remis.exchange.persistence.model.RateFetchCounter;
import org.springframework.data.repository.CrudRepository;

public interface RateFetchCounterRepo extends CrudRepository<RateFetchCounter, Long> {
    RateFetchCounter findByCcy(String ccy);
}