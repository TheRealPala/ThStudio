package domainModel;

public class Person {
    protected String name;
    protected String surname;
    protected String dateOfBirth;
    protected String iban;
    protected int id;
    public Person(){}

    public Person(String name, String surname, String dateOfBirth, String iban, int id){
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.iban = iban;
        this.id = id;
    }
    public Person(String name, String surname, String dateOfBirth, String iban){
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.iban = iban;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class Tag {
        protected String tag;
        protected String tagType;

        public Tag(String tag, String tagType ){
            this.tag = tag;
            this.tagType = tagType;
        };

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTagType() {
            return tagType;
        }

        public void setTagType(String tagType) {
            this.tagType = tagType;
        }
    }
}
