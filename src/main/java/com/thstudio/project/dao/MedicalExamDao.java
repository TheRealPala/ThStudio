package com.thstudio.project.dao;

import com.thstudio.project.domainModel.MedicalExam;
import com.thstudio.project.domainModel.Search.Search;
import com.thstudio.project.domainModel.State.State;

import java.util.List;

public interface MedicalExamDao extends DAO<MedicalExam, Integer> {

    /**
     * Get all doctors exams
     *
     * @param doctorId The doctor's ID
     * @return The list of exams
     * @throws Exception If something goes wrong
     */
    public List<MedicalExam> getDoctorExams(int doctorId) throws Exception;

    /**
     * Get all customer exams
     *
     * @param customerId The customer's ID
     * @return The list of exams
     * @throws Exception If something goes wrong
     */
    public List<MedicalExam> getCustomerExams(int customerId) throws Exception;

    /**
     * Get all exams in a specific state
     *
     * @param state The state of the exam
     * @return The list of exams
     * @throws Exception If something goes wrong
     */
    public List<MedicalExam> getExamsByState(String state) throws Exception;

    /**
     * Get all exams booked by a customer
     *
     * @param customerId The customer's ID
     * @return The list of exams
     * @throws Exception If something goes wrong
     */
    public List<MedicalExam> getCustomerBookedExams(int customerId) throws Exception;

    /**
     * Change the state of an exam
     *
     * @param idExam   The exam's ID
     * @param newState The new state
     * @throws Exception If something goes wrong
     */
    public void changeState(Integer idExam, State newState) throws Exception;

    /**
     * @param search Search Object with queryString
     * @return List of founded Exams
     * @throws Exception If something goes wrong
     */
    public List<MedicalExam> search(Search search) throws Exception;

    /**
     * Change the state of an exam
     *
     * @param me         The exam's ID
     * @param idCustomer The customer's ID
     * @throws Exception If something goes wrong
     */
    public void bookMedicalExam(MedicalExam me, int idCustomer) throws Exception;

    /**
     * Change the state of an exam
     *
     * @param me The exam's ID
     * @throws Exception If something goes wrong
     */
    public void deleteBookedMedicalExam(MedicalExam me) throws Exception;
}
