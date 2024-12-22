package businessLogic;

import dao.DoctorDao;
import domainModel.Doctor;

public class DoctorController extends PersonController<Doctor>{
    public DoctorController(DoctorDao doctorDAO){
        super(doctorDAO);
    }

    /**
     * Add a new doctor
     * @param name                          The name of the doctor
     * @param surname                       The surname of the doctor
     * @param dateOfBirth                   The date of birth of the doctor
     * @param iban                          The IBAN of the doctor
     * @param medicalLicenseNumber          The medical license number of the doctor
     *
     * @return The CF of the newly created doctor
     * @throws Exception bubbles up exceptions of PeopleController::addPerson()
     */
    public String addPerson(String name, String surname, String dateOfBirth, String iban, String medicalLicenseNumber) throws Exception {
        Doctor d = new Doctor(name, surname, dateOfBirth, iban, medicalLicenseNumber);
        return super.addPerson(d);
    }
}
