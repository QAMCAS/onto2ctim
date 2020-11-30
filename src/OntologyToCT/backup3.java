/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyToCT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author liyih
 */
public class backup3 {

    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        Runtime r = Runtime.getRuntime();
        Process p = null;

        //String s = "cmd /k start C:\\Users\\liyih\\Desktop\\acts_3.1.jar java -Dalgo=ipog -Ddoi=1 -jar acts_3.1.jar s_1.xml s_1_output.xml";
        //String path="OntologyExtraction\\road_section_complex.graphml\\Pipe_2.txt";
        //String s="cmd /k start java -Dalgo=ipog -Ddoi=2 -jar acts_3.1.jar Line_2.txt Line_2_output.xml";
        //File f = new File("C:\\Users\\liyih\\Documents\\NetBeansProjects\\OntologyExtraction\\road_section_complex.graphml2\\RoadSection_2.txt");
        

        //======================================================================
        //mapping complex input model
//        File f = new File("C:\\Users\\liyih\\Desktop\\RoadSection_2.txt");
//        FileReader fr2 = new FileReader(f);
//        BufferedReader br2 = new BufferedReader(fr2);
//        ArrayList<String> alllines = new ArrayList<String>();
//        while (br2.ready()) {
//            alllines.add(br2.readLine());
//        }
//        //System.out.println(alllines.size());
//        ArrayList<String> parameters = new ArrayList<String>();
//        for (int i = 0; i < alllines.size(); i++) {
//            if (alllines.get(i).contains("[Parameter]")) {
//                while (!alllines.get(i + 1).contains("[Constraint]")) {
//                    parameters.add(alllines.get(i + 1));
//                    i++;
//                }
//                break;
//            }
//        }
//        System.out.println("parameter size: " + parameters.size());
//        ArrayList<String> pname = new ArrayList<String>();
//        ArrayList<String> pvalue = new ArrayList<String>();
//        for (int i = 0; i < parameters.size(); i++) {
//            pname.add(parameters.get(i).split(":")[0]);
//            //System.out.println("adding panme: "+parameters.get(i).split(":")[0]);
//            pvalue.add(parameters.get(i).split(":")[1].trim());
//            //System.out.println("adding pvalue: "+parameters.get(i).split(":")[1]);
//        }
//
//        ArrayList<ArrayList<Maps>> mapping = new ArrayList<ArrayList<Maps>>();
//        mapping.clear();
//        int key = 0;
//
//        for (int i = 0; i < pvalue.size(); i++) {
//            ArrayList<Maps> temparray = new ArrayList<Maps>();
//            temparray.clear();
//            key = 0;
//            for (int j = 0; j < pvalue.get(i).split(",").length; j++) {
//                Maps tempmap = new Maps();
//                tempmap.key = key;
//                tempmap.value = pvalue.get(i).split(",")[j];
//                temparray.add(tempmap);
//                key++;
//            }
//            System.out.println("temparray size: " + temparray.size());
//            mapping.add((ArrayList<Maps>) temparray.clone());
//        }
//        System.out.println("mapping size: " + mapping.size());
//
//        File mapfile = new File(f.getParent() + "/" + f.getName() + "_map.txt");
//        mapfile.createNewFile();
//        FileWriter fw2 = null;
//        BufferedWriter bw2 = null;
//        fw2 = new FileWriter(mapfile);
//        bw2 = new BufferedWriter(fw2);
//
//        bw2.write("[System]");
//        bw2.newLine();
//        bw2.write("Name: " + mapfile.getName());
//        bw2.newLine();
//        bw2.write("[Parameter]");
//        bw2.newLine();
//        for(int i=0;i<pname.size();i++){
//            String templine="";
//            templine=pname.get(i)+": ";
//            for(int j=0;j<mapping.get(i).size();j++){
//                templine=templine+mapping.get(i).get(j).key+", ";
//            }
//            bw2.write(templine.substring(0,templine.length()-2));
//            bw2.newLine();
//        }
//        bw2.write("[Constraint]");
//        bw2.newLine();
//        bw2.close();
//        fw2.close();

        //======================================================================
        //call ACTS
        File f=new File("C:\\Users\\liyih\\Desktop\\RoadSection_2.txt_map.txt");
        String s1 = "cmd /k start java -Dalgo=ipog -Ddoi=2 -jar acts_3.1.jar ";
        String ss = "\\" + f.getName() + "_map_output.xml";
        String s2 = f.getPath() + " " + f.getParent() + ss;
        String s = s1 + s2;
        //System.out.println(s);
        String s3 = f.getParent() + ss;
        p = r.exec(s);

        File f1 = new File(s3);
        System.out.println(f1.getName() + ": " + f1.getPath());
        TimeUnit.SECONDS.sleep(1);//秒
        System.out.println(f1.exists());

         //=====================================================================
//        
//        
//        FileReader fr = new FileReader(f1);
//        //FileReader fr=new FileReader("C:\\Users\\liyih\\Documents\\NetBeansProjects\\OntologyExtraction\\road_section_complex.graphml2\\Line_2.txt_output.xml");
//        BufferedReader br = new BufferedReader(fr);
//        int numoflines = 0;
//        ArrayList<String> outputxml = new ArrayList<String>();
//        while (br.ready()) {
//            outputxml.add(br.readLine());
//            numoflines++;
//        }
//
//        int parametersize = 0;
//        int testsize = 0;
//
//        Nodes nodetemp = new Nodes();
//        ArrayList<String> tempattributevalue = new ArrayList<String>();
//        tempattributevalue.clear();
//
//        for (int i = 0; i < outputxml.size(); i++) {
//
//            if (outputxml.get(i).contains("Number of parameters")) {
//                String t1 = outputxml.get(i).split(":")[1].trim();
//                parametersize = Integer.parseInt(t1);
//                System.out.println("the parameter size is: " + parametersize);
//            }
//
//            if (outputxml.get(i).contains("Number of configurations")) {
//                String t2 = outputxml.get(i).split(":")[1].trim();
//                testsize = Integer.parseInt(t2);
//                System.out.println("number of test case is: " + testsize);
//            }
//            //a concept now has only one attribute
//            //and this attribute has "number of test case" values, 
//            //and each value is presented by "parametersize" parameters
//
//            if (outputxml.get(i).contains("Configuration #")) {
//
//                //System.out.println(outputxml.get(i));
//                String t3 = "";
//                for (int j = 0; j < parametersize; j++) {
//                    //t3 = t3 + " " + outputxml.get(i + 2 + j).split(" ")[2];
//                    //System.out.println("length: "+outputxml.get(i+2+j).length());
//                    t3=t3+" "+outputxml.get(i+2+j).substring(3);
//                    //System.out.println(t3);
//                }
//                System.out.println(t3);
//                tempattributevalue.add(t3);
//            }
//        }
//        nodetemp.attributevalues=(ArrayList<String>)tempattributevalue.clone();
//        System.out.println(nodetemp.attributevalues.size());
//        String newattri="";
//        newattri=nodetemp.name+" (enum) : ";
//        String sss="";
//        for(int i=0;i<nodetemp.attributevalues.size();i++){
//            sss=sss+nodetemp.attributevalues.get(i)+", ";
//        }
//        System.out.println(newattri+sss.substring(0, sss.length()-2));
        //======================================================================
        //this nodetemp will be used as same node for higher level concept but with way much more values
    }
}
