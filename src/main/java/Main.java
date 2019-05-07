
import org.apache.commons.io.FileUtils;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.propertytable.graph.GraphCSV;
import org.apache.jena.propertytable.lang.CSV2RDF;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.shared.Lock;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
//    Property predicate = model.createProperty("p");
//            Resource object = model.createResource();
//            model.add(subject, predicate, object);

public class Main {

    public static void main(String[] args) throws IOException {


        java.lang.String prefix = "test";
        java.lang.String baseuri = "http://localhost:8890/research";
        java.lang.String ty = "ttl"; //outputtype
        java.lang.String pt = "src/main/resources/post"; // resrouces path

        convertCSVToRDF("author","pre/lab_author.csv",pt+"lab_author.ttl",ty,prefix,baseuri);
        convertCSVToRDF("paper","pre/lab_paper.csv",pt+"lab_paper.ttl",ty,prefix,baseuri);
        convertCSVToRDF("reviewer","pre/lab_reviewers.csv",pt+"lab_reviewers.ttl",ty,prefix,baseuri);
        convertCSVToRDF("","pre/lab_journalConference.csv",pt+"lab_journalConference.ttl",ty,prefix,baseuri);


        createUniqueFile("src/main/resources");
    }

    public static void createUniqueFile(String output){
        File folder = new File(output+"/post");
        File[] fileNames = folder.listFiles();

        try {
            File fi = new File(output+"/post/abox.ttl");
            System.out.println(fi.delete());
            PrintWriter pw = new PrintWriter(output+"/post/abox.ttl");
            int cont = 0;
            for(File file : fileNames){
                // if directory call the same method again
                if(!file.isDirectory() && !file.getName().equals("abox.ttl")){
                    BufferedReader br1 = new BufferedReader(new FileReader(file));
                    String line1 = br1.readLine();
                    while (line1 != null) {
                        if(line1 != null) {

                            if(cont>0 && line1.contains("@prefix")){
                                //do nothing
                            }else
                                pw.println(line1);
                            line1 = br1.readLine();
                            cont++;
                        }

                    }
                    pw.flush();
                    br1.close();
                }
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static InputStream getFileFromResources(String fileName) {

        ClassLoader classLoader = Main.class.getClassLoader();

        InputStream resource = classLoader.getResourceAsStream(fileName);
        return resource;

    }


    public static void convertCSVToRDF (String subject, String inputFilename, String outputFilename,
                                        String outputType, String prefix, String baseuri) {
        CSV2RDF.init();
        String uri = baseuri+"#";

        Model model = ModelFactory.createDefaultModel();
        model.read( getFileFromResources(inputFilename), baseuri, "csv");


        //Manually insert class triples for each instance in the CSV file
        String sparqlQueryString = "select distinct ?s where  {?s ?p ?o}";
        Query query = QueryFactory.create(sparqlQueryString);
        QueryExecution qexec = QueryExecutionFactory.create(sparqlQueryString, model);
        ResultSet s = qexec.execSelect();
        Model m2 = ModelFactory.createDefaultModel();
        int cont = 0;
        if(subject != ""){
            while(s.hasNext()) {
                QuerySolution so = s.nextSolution();
                Statement stmt = ResourceFactory.createStatement(so.getResource("s"), RDF.type,
                        ResourceFactory.createResource(uri+subject));
                m2.add(stmt);
            }
        }

        Model m3 = describeModel(ModelFactory.createUnion(model, m2),uri);
        m3.setNsPrefix(prefix, uri);

        try {
            FileWriter out = new FileWriter(outputFilename);
            m3.write(out,outputType);
        } catch (Exception e) {
            System.out.println("Error in the file output process!");
            e.printStackTrace();
        }

        //Delete specific triples of a specific predicate called ¨row¨
//
    }


    public static void cleanModel(){

    }

    @SuppressWarnings("unused")
    public static Model describeModel(Model model,String uri) {

        Model containerModel = ModelFactory.createDefaultModel();

        Map<String, String> map = new HashMap<>();

        StmtIterator stmtIterator = model.listStatements(null, model.getProperty(uri+"id"), (RDFNode) null);
        while (stmtIterator.hasNext()){
            Statement s = stmtIterator.next();
            map.put(s.getSubject().toString(), s.getObject().toString());

        }

        int index =1;
        model.listSubjects().forEachRemaining(
                r -> {
                    String sub = map.get(r.toString());
                    Resource subject;
                    if (sub != null)
                        subject = containerModel.createResource(uri+sub);
                    else
                        subject = r;

                    StmtIterator props = r.listProperties();
                    props.forEachRemaining(p -> {
                        if(!p.getPredicate().toString().replace(uri,"").equals("id")
                            && ! p.getPredicate().toString().equals("http://w3c/future-csv-vocab/row")){

                            if(p.getPredicate().toString().replace(uri,"").equals("label")){
                                containerModel.add(subject, RDFS.label, p.getObject());
                            }else{
                                if(p.getPredicate().toString().replace(uri,"").equals("subClassOf")){
                                    Resource temp = containerModel.createResource(uri+p.getObject());
                                    containerModel.add(subject, RDFS.subClassOf, temp);
                                    //Case for type
//
                                }else{
                                    if(p.getPredicate().toString().replace(uri,"").equals("type")){
                                        Resource temp = containerModel.createResource(uri+p.getObject());
                                        containerModel.add(subject, RDF.type, temp);
                                    }else{
                                        containerModel.add(subject, p.getPredicate(), p.getObject());
                                    }

                                }
                            }
                        }

                    });
                }
        );
        return containerModel;
    }

}
