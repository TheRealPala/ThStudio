package dao;

import domainModel.MedicalExam;
import domainModel.Search.Search;

import java.util.List;

public interface MedicalExamDao extends DAO<MedicalExam, Integer>{

    /**
     * Get all doctors exams
     *
     * @param doctorId          The doctor's ID
     * @param examId            The exam's ID
     * @return                  The list of exams
     * @throws Exception        If something goes wrong
     */
    public List<MedicalExam> getDoctorExams(int doctorId, int examId) throws Exception;

    /**
     * Get all customer exams
     *
     * @param customerId        The customer's ID
     * @param examId            The exam's ID
     * @return                  The list of exams
     * @throws Exception        If something goes wrong
     */
    public List<MedicalExam> getCustomerExams(int customerId, int examId) throws Exception;

    /**
     *
     * @param search            Search Object with queryString
     * @return                  List of founded Exams
     * @throws Exception        If something goes wrong
     */
    public List<MedicalExam> search(Search search) throws Exception;
}
