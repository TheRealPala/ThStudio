import domainModel.Doctor;
import domainModel.Document;
import io.github.cdimascio.dotenv.Dotenv;
import dao.*;
public class Main {
    public static void main(String[] args) throws Exception {
        Dotenv dotenv = Dotenv.configure().directory("config").load();
        Database.setDbHost(dotenv.get("DB_HOST"));
        Database.setDbName(dotenv.get("DB_NAME_DEFAULT"));
        Database.setDbTestName(dotenv.get("DB_NAME_TEST"));
        Database.setDbUser(dotenv.get("DB_USER"));
        Database.setDbPassword(dotenv.get("DB_PASSWORD"));
        Database.setDbPort(dotenv.get("DB_PORT"));
        System.out.println(Database.testConnection(false, false));
        PersonDao pDao = new MariaDbPersonDao();
        DoctorDao dDao = new MariaDbDoctorDao(pDao);
        Doctor d = new Doctor("nomeD", "cognomeD", "2000-10-02","123456", 3500.50);
        dDao.insert(d);
    }
}
