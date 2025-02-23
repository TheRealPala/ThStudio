package com.thstudio.project.fixture;

import com.github.javafaker.Faker;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.Document;


public class NotificationFixture {
    private static final Faker faker = new Faker();

    public static String genTitle() {
        return faker.file().fileName();
    }

}
