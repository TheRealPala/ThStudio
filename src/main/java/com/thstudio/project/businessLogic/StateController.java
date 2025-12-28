package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.CustomerDao;
import com.thstudio.project.dao.DoctorDao;
import com.thstudio.project.dao.NotificationDao;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.Notification;
import com.thstudio.project.domainModel.State.Available;
import com.thstudio.project.domainModel.State.Booked;
import com.thstudio.project.domainModel.State.Completed;
import com.thstudio.project.domainModel.Customer;
import com.thstudio.project.domainModel.MedicalExam;
import com.thstudio.project.dao.MedicalExamDao;
import com.thstudio.project.security.Authz;

import java.time.LocalDateTime;

public class StateController {
    private final MedicalExamDao medicalExamDao;
    private final CustomerDao customerDao;
    private final DoctorDao doctorDao;
    private final NotificationDao notificationDao;
    private final Authz authz;

    public StateController(MedicalExamDao medicalExamDao, CustomerDao customerDao,
                           DoctorDao doctorDao, NotificationDao notificationDao, Authz authz) {
        this.medicalExamDao = medicalExamDao;
        this.customerDao = customerDao;
        this.doctorDao = doctorDao;
        this.notificationDao = notificationDao;
        this.authz = authz;
    }

    /**
     * a client book a medical exam
     *
     * @param medicalExamId The medical exam id
     * @param customerId    The customer id
     * @return true if the medical exam is canceled, false otherwise
     */
    public boolean bookMedicalExam(int medicalExamId, int customerId, String token) throws Exception {
        
        this.authz.requireAnyRole(token, "customer", "admin");
        MedicalExam me = this.medicalExamDao.get(medicalExamId);
        Customer c = this.customerDao.get(customerId);
        if (me.getState() instanceof Booked) {
            throw new RuntimeException("The exam you want to book is already booked");
        }
        if (c.getBalance() < me.getPrice()) {
            throw new RuntimeException("not enough money");
        } else {
            me.setState(new Booked(LocalDateTime.now()));
            this.medicalExamDao.bookMedicalExam(me, customerId);
            //pay exam
            Doctor d = this.doctorDao.get(me.getIdDoctor());
            c.setBalance(c.getBalance() - me.getPrice());
            customerDao.update(c);
            d.setBalance(d.getBalance() + me.getPrice());
            doctorDao.update(d);
            Notification nd = new Notification("Booked exam " + me.getTitle() + " by:" + c.getName(), me.getIdDoctor());
            notificationDao.insert(nd);
        }
        return true;
    }

    /**
     * cancel a medical exam booking
     *
     * @param medicalExamId The medical exam id
     * @param customerId    The customer id
     * @return true, if the medical exam booking is canceled, raise up RuntimeExceptions otherwise
     */
    public boolean cancelMedicalExamBooking(int medicalExamId, int customerId, String token) throws Exception {
        this.authz.requireAnyRole(token, "customer", "admin");
        MedicalExam me = this.medicalExamDao.get(medicalExamId);
        Customer c = this.customerDao.get(customerId);
        if (me.getIdCustomer() != c.getId()) {
            throw new RuntimeException("Unauthorized request");
        }
        if (!(me.getState() instanceof Booked)) {
            throw new RuntimeException("Can't cancel a booking for an exam which is not booked");
        }
        if (LocalDateTime.now().isAfter(me.getStartTime())) {
            throw new RuntimeException("Can't cancel an exam already started");
        }
        //refund
        double medicalExamPrice = me.getPrice();
        Doctor d = this.doctorDao.get(me.getIdDoctor());
        d.setBalance(d.getBalance() - medicalExamPrice);
        c.setBalance(c.getBalance() + medicalExamPrice);
        this.doctorDao.update(d);
        this.customerDao.update(c);

        me.setState(new Available());
        this.medicalExamDao.deleteBookedMedicalExam(me);
        Notification nd = new Notification("Deleted exam booking " + me.getTitle() + " by:" + c.getName(), d.getId());
        notificationDao.insert(nd);
        return true;
    }

    /**
     * Complete a medical exam
     *
     * @param examId The id of the medical exam
     * @throws Exception If the medical exam is not found or if it is already completed
     */
    public void markMedicalExamAsComplete(int examId, String token) throws Exception {
        this.authz.requireAnyRole(token, "doctor", "admin");
        MedicalExam medicalExam = medicalExamDao.get(examId);
        if (!(medicalExam.getState() instanceof Booked)) {
            throw new RuntimeException("Can't mark an exam as complete if is not in booked state");
        }
        if (LocalDateTime.now().isBefore(medicalExam.getEndTime())) {
            throw new RuntimeException("Can't mark an exam as complete if is not finished");
        }
        this.medicalExamDao.changeState(examId, new Completed(LocalDateTime.now()));
    }

}


