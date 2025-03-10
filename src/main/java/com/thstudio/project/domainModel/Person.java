package com.thstudio.project.domainModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Person {
    protected String name;
    protected String surname;
    protected LocalDate dateOfBirth;
    protected int id;
    protected double balance;
    protected String email;
    protected String password;

    public Person(int id, String name, String surname, String dateOfBirth, double balance, String email, String password) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = parseDate(dateOfBirth);
        this.balance = balance;
        this.email = email;
        this.password = password;
    }

    public Person(String name, String surname, String dateOfBirth, double balance, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = parseDate(dateOfBirth);
        this.balance = balance;
        this.email = email;
        this.password = password;
    }

    public Person(Person that) {
        this.id = that.id;
        this.name = that.name;
        this.surname = that.surname;
        this.dateOfBirth = that.dateOfBirth;
        this.balance = that.balance;
        this.email = that.email;
        this.password = that.password;
    }

    private LocalDate parseDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDateOfBirth() {
        return dateOfBirth.toString();
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = parseDate(dateOfBirth);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return name + " " + surname;
    }
}
