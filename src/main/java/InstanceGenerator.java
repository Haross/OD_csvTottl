import doe.Author;
import doe.JournalConference;
import doe.Paper;

import java.io.*;
import java.util.*;

public class InstanceGenerator {

    List<Author> authors;
    List<Paper> papers;
    List<JournalConference> joun;
    int numAuthors = 150, numPapers = 80, numJournalConference = 6;

    public static void main(String[] args) {
        InstanceGenerator inst = new InstanceGenerator();
        inst.init();
        inst.initJournalConference();
        inst.initPapers();
        inst.generateAuthors();
        inst.generatePapers();
        inst.generateJournalsConference();
    }

    /**
     * Generate doe.Author wrote paper
     */
    public void generateAuthors(){
        File file = new File("src/main/resources/pre/lab_author.csv");
        File fileR = new File("src/main/resources/pre/lab_reviewers.csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter fw = new FileWriter(file);
            FileWriter fwR = new FileWriter(fileR);

            fw.write("id,wrote,label\n");
            fwR.write("id,review,label\n");

            for (int i = 0; i < numPapers; i++) {
                int numA = getRandom(3,1);
                String paper = papers.get(i).getId();

                List<Integer> numbers = new ArrayList<Integer>();
                for (int j = 0; j < numA; j++) {
                    numbers.add(getRandom(numAuthors-1,0));
                    Author a = authors.get(numbers.get(j));

                    fw.write(a.getId()+","+paper+","+a.getLabel()+"\n");
                }
                //for reviewers
                numA = getRandom(3,1);
                for (int j = 0; j <numA ; j++) {
                    int next = -1;
                    while (next == -1){
                        int next2 = getRandom(numAuthors-1,0);
                        if (!numbers.contains(next2))
                            next = next2;

                    };
                    Author a = authors.get(next);
                    fwR.write(a.getId()+","+paper+","+a.getLabel()+"\n");

                }


            }

            fw.close();
            fwR.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void generatePapers(){
        File file = new File("src/main/resources/pre/lab_paper.csv");

        try {
            // create FileWriter object with file as parameter
            FileWriter fw = new FileWriter(file);

            fw.write("id,label,subClassOf,publish_in\n");

            for (int i = 0; i < numPapers; i++) {
                Paper p = papers.get(i);
                fw.write(p.getId()+","+p.getPapername()+","+p.getPapertype()+","+p.getPublish()+"\n");
            }


            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void generateJournalsConference(){
        File file = new File("src/main/resources/pre/lab_journalConference.csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter fw = new FileWriter(file);

            fw.write("id,label,type,subClassOf\n");

            for (int i = 0; i < numJournalConference; i++) {
                JournalConference p = joun.get(i);
                fw.write(p.getId()+","+p.getName()+","+p.getType()+","+p.getSpecificname()+"\n");
            }


            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getRandom(int max,int min){
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public void init() {
        authors = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("raw/authors.csv")));

            String line;
            int cont = -1;
            while ((line = br.readLine()) != null && cont < numAuthors) {
                String[] values = line.split(",");
                if(cont != -1)
                    authors.add(new Author(values[0],values[0].replace(" ","_")));
                cont++;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void initJournalConference(){
        joun = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("raw/journals.csv")));

            String line;
            int cont = -1;
            int conJ = 1;
            int conC = 1;
            while ((line = br.readLine()) != null && cont <= numJournalConference) {
                String[] values = line.split(",");
                cont++;
                if(cont != 0){
                    int opt = getRandom(2,1);

                    if(opt == 1){
                        joun.add(new JournalConference(values[0],"Conference"+conC,"Conference"));
                        conC++;
                    }else{
                        joun.add(new JournalConference(values[0],"Journal"+conJ,"Journal"));
                        conJ++;
                    }

                }

            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initPapers(){
        papers = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("raw/papers.csv")));

            String line;
            int cont = -1;
            while ((line = br.readLine()) != null && cont <= numPapers) {
                String[] values = line.split(",");
                cont++;
                if(cont != 0){

                    String id = joun.get(getRandom(numJournalConference-1,0)).getId();
                    papers.add(new Paper(values[0],id,"paper"+cont));
                }

            }
        }catch (IOException e) {
            e.printStackTrace();
        }

//        int cont = 0;
//        while (cont < numPapers){
//            papers.add(new doe.Paper("paper"+cont,"","",""));
//            cont++;
//        }


    }
}
