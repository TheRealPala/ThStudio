package com.thstudio.project.fixture;

import com.github.javafaker.Faker;
import com.thstudio.project.domainModel.Customer;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.MedicalExam;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class MedicalExamFixture {
    private static final Faker faker = new Faker();

    public static MedicalExam genMedicalExam(Doctor doctor) {
        Date tmp = faker.date().future(24 * 365, TimeUnit.DAYS);
        LocalDateTime dateOfStart = tmp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime dateOfFinish = dateOfStart.plusHours(1);
        return new MedicalExam(doctor.getId(), dateOfStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), dateOfFinish.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), faker.lorem().characters(55), faker.file().fileName(), 300);
    }

    public static MedicalExam genMedicalExam(Doctor doctor, int numberOfHours) {
        assert numberOfHours > 0;
        Date tmp = faker.date().future(24 * 365, TimeUnit.DAYS);
        LocalDateTime dateOfStart = tmp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime dateOfFinish = dateOfStart.plusHours(numberOfHours);
        return new MedicalExam(doctor.getId(), dateOfStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), dateOfFinish.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), faker.lorem().characters(55), faker.file().fileName(), 300);
    }

    public static MedicalExam genMedicalExam(Doctor doctor, Customer customer) {
        Date tmp = faker.date().future(24 * 365, TimeUnit.DAYS);
        LocalDateTime dateOfStart = tmp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime dateOfFinish = dateOfStart.plusHours(1);
        MedicalExam medicalExam = new MedicalExam(doctor.getId(), dateOfStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), dateOfFinish.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), faker.lorem().characters(55), faker.file().fileName(), customer.getBalance() - 300);
        medicalExam.setIdCustomer(customer.getId());
        return medicalExam;
    }

    public static MedicalExam genMedicalExam(Doctor doctor, LocalDateTime dateOfStart, LocalDateTime dateOfFinish) {
        return new MedicalExam(doctor.getId(), dateOfStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), dateOfFinish.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), faker.lorem().characters(55), faker.file().fileName(), 300);
    }

    public static MedicalExam genMedicalExam(Doctor doctor, Customer customer, LocalDateTime dateOfStart, int numberOfHours) {
        assert numberOfHours > 0;
        LocalDateTime dateOfFinish = dateOfStart.plusHours(numberOfHours);
        MedicalExam medicalExam = new MedicalExam(doctor.getId(), dateOfStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), dateOfFinish.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), faker.lorem().characters(55), faker.file().fileName(), customer.getBalance() - 300);
        medicalExam.setIdCustomer(customer.getId());
        return medicalExam;
    }

    public static MedicalExam genMedicalExam(Doctor doctor, Customer customer, LocalDateTime dateOfStart, LocalDateTime dateOfFinish) {
        MedicalExam medicalExam = new MedicalExam(doctor.getId(), dateOfStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), dateOfFinish.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), faker.lorem().characters(55), faker.file().fileName(), customer.getBalance() - 300);
        medicalExam.setIdCustomer(customer.getId());
        return medicalExam;
    }

    public static MedicalExam genMedicalExam(Doctor doctor, Customer customer, double price) {
        Date tmp = faker.date().future(24 * 365, TimeUnit.DAYS);
        LocalDateTime dateOfStart = tmp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime dateOfFinish = dateOfStart.plusHours(1);
        MedicalExam medicalExam = new MedicalExam(doctor.getId(), dateOfStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), dateOfFinish.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), faker.lorem().characters(55), faker.file().fileName(), price);
        medicalExam.setIdCustomer(customer.getId());
        return medicalExam;
    }
}
