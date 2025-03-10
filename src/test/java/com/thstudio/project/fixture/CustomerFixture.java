package com.thstudio.project.fixture;

import com.github.javafaker.Faker;
import com.thstudio.project.domainModel.Customer;


public class CustomerFixture {
    private static final Faker faker = new Faker();

    public static Customer genCustomer() {
        Customer customer = new Customer(PersonFixture.genPerson());
        customer.setLevel(faker.number().numberBetween(1, 5));
        return customer;
    }
    public static Customer genTestCustomer() {
        Customer customer = new Customer(PersonFixture.genTestPerson());
        customer.setLevel(1);
        return customer;
    }
}
