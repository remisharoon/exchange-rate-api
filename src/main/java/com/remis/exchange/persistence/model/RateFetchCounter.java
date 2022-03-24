package com.remis.exchange.persistence.model;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueCurrencyCount", columnNames = {"ccy", "fetchCount"})})
public class RateFetchCounter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String ccy;

    @Column(nullable = false)
    private Integer fetchCount = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public Integer getFetchCount() {
        return fetchCount;
    }

    public void setFetchCount(Integer fetchCount) {
        this.fetchCount = fetchCount;
    }
}
