package com.driver.model;

import javax.persistence.*;
import java.sql.Driver;

@Entity
@Table
public class Cab {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int perKmRate;

    boolean available;

    @OneToOne(mappedBy = "cab",cascade = CascadeType.ALL)
    private Driver driver;

    public Cab(int id, int perKmRate, boolean available, Driver driver) {
        this.id = id;
        this.perKmRate = perKmRate;
        this.available = available;
        this.driver = driver;
    }

    public Cab() {
    }
    public Cab(int perKmRate){
        this.perKmRate = 10;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(int perKmRate) {
        this.perKmRate = perKmRate;
    }

    public boolean isAvailable() {
        return available;
    }
    public boolean getAvailable(){
        return this.available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}