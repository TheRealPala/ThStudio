package dao;

import domainModel.MedicalExam;
import domainModel.Search.Search;
import domainModel.State.*;

import java.util.List;

public interface MedicalExamDao extends DAO<MedicalExam, Integer>{

    /**
     * Get all doctors exams
     *
     * @param doctorId          The doctor's ID
     * @return                  The list of exams
     * @throws Exception        If something goes wrong
     */
    public List<MedicalExam> getDoctorExams(int doctorId) throws Exception;

    /**
     * Get all customer exams
     *
     * @param customerId        The customer's ID
     * @return                  The list of exams
     * @throws Exception        If something goes wrong
     */
    public List<MedicalExam> getCustomerExams(int customerId) throws Exception;

    /**
     * Get all exams in a specific state
     *
     * @param state             The state of the exam
     * @return                  The list of exams
     * @throws Exception        If something goes wrong
     */
    public List<MedicalExam> getExamsByState(String state) throws Exception;

    /**
     * Change the state of an exam
     *
     * @param idExam            The exam's ID
     * @param newState          The new state
     * @throws Exception        If something goes wrong
     */
    public void changeState(Integer idExam, State newState) throws Exception;

    /**
     *
     * @param search            Search Object with queryString
     * @return                  List of founded Exams
     * @throws Exception        If something goes wrong
     */
    public List<MedicalExam> search(Search search) throws Exception;
}
