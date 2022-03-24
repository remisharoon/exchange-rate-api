package com.remis.exchange.persistence.model;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueDailyRate", columnNames = {"baseccy", "ccy", "date"})})
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String ccy;

    @Column(nullable = false)
    private String baseccy;

    @Column(nullable = false)
    private Double rate ;

    @Column(nullable = false)
    private Date date;

    public Currency() {
        super();
    }

    public Currency(String baseccy, String ccy, Double rate, Date date) {
        super();
        this.baseccy = baseccy;
        this.ccy = ccy;
        this.rate = rate;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", ccy='" + ccy + '\'' +
                ", baseccy='" + baseccy + '\'' +
                ", rate=" + rate +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;
        Currency currency = (Currency) o;
        return getCcy().equals(currency.getCcy()) && getBaseccy().equals(currency.getBaseccy()) && getRate().equals(currency.getRate()) && getDate().equals(currency.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCcy(), getBaseccy(), getRate(), getDate());
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getBaseccy() {
        return baseccy;
    }

    public void setBaseccy(String baseccy) {
        this.baseccy = baseccy;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
