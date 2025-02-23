package com.thstudio.project.domainModel;

import com.thstudio.project.fixture.CustomerFixture;
import com.thstudio.project.fixture.DoctorFixture;
import com.thstudio.project.fixture.MedicalExamFixture;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class MedicalExamTest {

    @Test
    void genMedicalExam() {
        Doctor doctor = DoctorFixture.genDoctor();
        MedicalExamFixture.genMedicalExam(doctor);
    }

    @Test
    void genMedicalExamWithStartTimeAfterEndTime() {
        IllegalArgumentException thrown = assertThrowsExactly(IllegalArgumentException.class,
                () -> {
                    new MedicalExam(0, "2020-10-01 16:30:00", "2020-10-01 15:30:00", "desc", "title", 120);
                }
        );
        assertEquals(thrown.getMessage(), "endTime must be after startTime");
    }

    @Test
    void setInvalidStartTime() {
        Doctor doctor = DoctorFixture.genDoctor();
        Customer customer = CustomerFixture.genCustomer();
        MedicalExam medicalExam = MedicalExamFixture.genMedicalExam(doctor, customer);
        IllegalArgumentException thrown = assertThrowsExactly(IllegalArgumentException.class,
                () -> {
                    medicalExam.setStartTime(medicalExam.getEndTime().plusHours(1));
                }
        );
        assertEquals(thrown.getMessage(), "startTime must be before endTime");
    }

    @Test
    void setInvalidEndTime() {
        Doctor doctor = DoctorFixture.genDoctor();
        Customer customer = CustomerFixture.genCustomer();
        MedicalExam medicalExam = MedicalExamFixture.genMedicalExam(doctor, customer);
        IllegalArgumentException thrown = assertThrowsExactly(IllegalArgumentException.class,
                () -> {
                    medicalExam.setEndTime(medicalExam.getStartTime().minusHours(1));
                }
        );
        assertEquals(thrown.getMessage(), "endTime must be after startTime");
    }

    @Test
    void setInvalidStartTimeString() {
        Doctor doctor = DoctorFixture.genDoctor();
        Customer customer = CustomerFixture.genCustomer();
        MedicalExam medicalExam = MedicalExamFixture.genMedicalExam(doctor, customer);
        IllegalArgumentException thrown = assertThrowsExactly(IllegalArgumentException.class,
                () -> {
                    medicalExam.setStartTimeFromString(medicalExam.getEndTime().plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
        );
        assertEquals(thrown.getMessage(), "startTime must be before endTime");
    }

    @Test
    void setInvalidEndTimeString() {
        Doctor doctor = DoctorFixture.genDoctor();
        Customer customer = CustomerFixture.genCustomer();
        MedicalExam medicalExam = MedicalExamFixture.genMedicalExam(doctor, customer);
        IllegalArgumentException thrown = assertThrowsExactly(IllegalArgumentException.class,
                () -> {
                    medicalExam.setEndTimeFromString(medicalExam.getStartTime().minusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
        );
        assertEquals(thrown.getMessage(), "endTime must be after startTime");
    }
}
