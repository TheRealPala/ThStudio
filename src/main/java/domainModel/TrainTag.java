package domainModel;

public class TrainTag extends Tag {
    int idTrain;

    public TrainTag() {
    }

    public TrainTag(String tags, String tags_type, int idTrain) {
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
