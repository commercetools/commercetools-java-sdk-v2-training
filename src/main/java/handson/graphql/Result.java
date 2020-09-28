package handson.graphql;

public class Result {

    private String id;

    private MasterData masterData;

    public MasterData getMasterData() {
        return masterData;
    }

    public void setMasterData(MasterData masterData) {
        this.masterData = masterData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
