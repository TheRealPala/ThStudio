package domainModel;

public class TrainTags extends Tags {
    int idTrain;

    public TrainTags() {
    }

    public TrainTags(String tags, String tags_type, int idTrain) {
        super(tags, tags_type);
        this.idTrain = idTrain;
    }

    public int getIdTrain() {
        return idTrain;
    }

    public void setIdTrain(int idTrain) {
        this.idTrain = idTrain;
    }
}
