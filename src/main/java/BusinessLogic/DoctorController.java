package BusinessLogic;

import dao.DoctorDao;
import domainModel.Doctor;

public class DoctorController extends PersonController<Doctor>{
    public DoctorController(DoctorDao doctorDAO){
        super(doctorDAO);
    }

    /**
     * Add a new doctor
     *
     * @return The CF of the newly created doctor
     * @throws Exception bubbles up exceptions of PeopleController::addPerson()
     */
    public String addPerson(String name, String surname, String dateOfBirth, String iban, int id, String medicalLicenseNumber) throws Exception {
        Doctor d = new Doctor(name, surname, dateOfBirth, iban, id, medicalLicenseNumber);
        return super.addPerson(d);
    }
}
