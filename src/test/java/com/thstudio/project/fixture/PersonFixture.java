package com.thstudio.project.fixture;

import com.github.javafaker.Faker;
import com.thstudio.project.domainModel.Person;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PersonFixture {
    private static final Faker faker = new Faker();

    public static Person genPerson() {
        Date tmp= faker.date().past(50*365, TimeUnit.DAYS);
        LocalDateTime dateOfBirth = tmp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return new Person(faker.name().firstName(), faker.name().lastName(), dateOfBirth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), faker.number().randomDouble(5, 5000, 99999));
    }
}
