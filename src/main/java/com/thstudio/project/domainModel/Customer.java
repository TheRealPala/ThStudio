package com.thstudio.project.domainModel;

public class Customer extends Person {
    private int level;

    public Customer(String name, String surname, String dateOfBirth, int id, int level, double balance) {
        super(id, name, surname, dateOfBirth, balance);
        this.level = level;
    }

    public Customer(String name, String surname, String dateOfBirth, int level, double balance) {
        super(name, surname, dateOfBirth, balance);
        this.level = level;
    }

    public Customer(Person p) {
        super(p);
        this.level = 1;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;
        return customer.id == this.id && customer.name.equals(this.name) && customer.surname.equals(this.surname) && customer.dateOfBirth.equals(this.dateOfBirth) && customer.level == this.level && customer.balance == this.balance;
    }
}
