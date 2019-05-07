package doe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Paper {
    private String papername;
    private String papertype;
    private String publish;
    private String specific_publish;
    private String id;

    private List<String> types;

    public Paper(String papername, String publish,String id) {
        this.papername = papername;
        initTypes();
        setRandomType();
        this.publish = publish;
        this.id = id;
    }

    private void initTypes(){
        types = new ArrayList<>();
        types.add("ShortPaper");
        types.add("DemoPaper");
        types.add("SurveyPaper");
        types.add("FullPaper");
    }

    private void setRandomType(){
        this.papertype = types.get(getRandom(types.size()-1,0));
    }

    private int getRandom(int max,int min){
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getPapername() {
        return papername;
    }

    public void setPapername(String papername) {
        this.papername = papername;
    }

    public String getPapertype() {
        return papertype;
    }

    public void setPapertype(String papertype) {
        this.papertype = papertype;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

    public String getSpecific_publish() {
        return specific_publish;
    }

    public void setSpecific_publish(String specific_publish) {
        this.specific_publish = specific_publish;
    }




}
