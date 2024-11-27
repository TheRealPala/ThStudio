package dao;

import domainModel.MedicalExam;

import java.sql.ResultSet;
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
    public List<MedicalExam> getDoctorExam(int doctorId, int examId) throws Exception;

}
