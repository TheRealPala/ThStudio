package BusinessLogic;

import dao.TrainerDao;
import domainModel.Trainer;

public class TrainerController extends PersonController<Trainer> {
    public TrainerController(TrainerDao trainerDAO) {
        super(trainerDAO);
    }

    /**
     * Add a new trainer
     *
     * @return The CF of the newly created trainer
     * @throws Exception bubbles up exceptions of PeopleController::addPerson()
     */
    public String addPerson(String name, String surname, String dateOfBirth, String iban, int id, String specialization) throws Exception {
        Trainer t = new Trainer(name, surname, dateOfBirth, iban, id, specialization);
        return super.addPerson(t);
    }
}
