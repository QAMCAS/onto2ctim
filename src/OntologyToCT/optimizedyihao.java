/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyToCT;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author liyih
 */
public class optimizedyihao {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, CloneNotSupportedException, InterruptedException {
        // TODO code application logic here

        //File graphml = new File("C:\\Users\\yihaoli\\Google Drive\\Yihao-Li\\TU-Graz-AutoDrive\\models\\OntologyExtraction\\complex.graphml");
        //File graphml = new File("C:\\Users\\yihaoli\\Google Drive\\Yihao-Li\\TU-Graz-AutoDrive\\models\\OntologyExtraction\\jianbo-original.graphml");
        //File graphml = new File("C:\\Users\\yihaoli\\Documents\\NetBeansProjects\\OntologyExtraction\\complex3.graphml");
        //File graphml = new File("C:\\Users\\liyih\\Google Drive\\Yihao-Li\\TU-Graz-AutoDrive\\models\\OntologyExtraction\\complex.graphml");
        //File graphml = new File("C:\\Users\\liyih\\Google Drive\\Yihao-Li\\TU-Graz-AutoDrive\\models\\OntologyExtraction\\jianbo-original.graphml");
        //File graphml = new File("C:\\Users\\liyih\\Documents\\NetBeansProjects\\OntologyExtraction-used\\complex3.graphml");
        File graphml = new File("C:\\Users\\liyih\\Documents\\NetBeansProjects\\OntologyExtraction-used\\Ontology-v5-used.graphml");

        int strength = 2;
        String algorithm = "YL";

        int num_of_nodes = 0;
        int num_of_relations = 0;
        ArrayList<Nodes> allnodes = new ArrayList<>();
        ArrayList<Relations> allrelations = new ArrayList<>();
        ArrayList<Parameters> allparametersandvalues = new ArrayList<Parameters>();
        
        //read ".gramphl" file and get all the information I want
        extractinformation(allnodes, allrelations, num_of_nodes, num_of_relations, graphml, allparametersandvalues);

        System.gc();
        ArrayList<String> leafnodes = new ArrayList<String>();
        ArrayList<String> nodesalreadyconverted = new ArrayList<String>();
        ArrayList<String> nodesnotconverted = new ArrayList<String>();
        ArrayList<Relations> relationsalreadyconverted = new ArrayList<Relations>();
        ArrayList<Relations> relationsnotconverted = new ArrayList<Relations>();

        for (int i = 0; i < allrelations.size(); i++) {
            relationsnotconverted.add(allrelations.get(i));
        }
        for (int i = 0; i < allnodes.size(); i++) {
            nodesnotconverted.add(allnodes.get(i).name);
        }

        while (relationsnotconverted.size() != 0) {
            findcurrentleafnodes(leafnodes, nodesnotconverted, relationsnotconverted);
            convertcurrentleafnodes(leafnodes, nodesalreadyconverted, nodesnotconverted, relationsalreadyconverted, relationsnotconverted);
            System.out.println();
            //summary(allnodes,allrelations);
        }
        System.gc();
        
        summary(allnodes);
        
        //generate inputmodels for each concept
        ArrayList<File> inputmodels = new ArrayList<File>();
        generateinputmodels(inputmodels, allnodes, graphml, strength);
        //run ACTS
        runacts(inputmodels, strength);
        
    }//end of main()

    private static ArrayList<String> findleafnode(ArrayList<Nodes> allnodes, ArrayList<Relations> allrelations, ArrayList<String> leafnodes) {
        for (int i = 0; i < allnodes.size(); i++) {
            int count = 0;//for a leafnode, in case of composition relation, no count from the source side; in case of inheritance relation, no count from the target side
            //System.out.println("Checkig node: " + allnodes.get(i).name + " #####################################################");
            for (int j = 0; j < allrelations.size(); j++) {
                if (allnodes.get(i).name.matches(allrelations.get(j).sourcenode.name) && allrelations.get(j).type.matches("composition")) {//careful!!!!Using "matches()" is different from using ".contains(); e.g., concept "AH" contains concept "A"
                    count++;
                }
                if (allnodes.get(i).name.matches(allrelations.get(j).targetnode.name) && allrelations.get(j).type.matches("inheritance")) {//careful!!!!Using "matches()" is different from using ".contains()"
                    count++;
                }
            }
            if (count == 0) {
                //System.out.println("Find one leaf node: " + allnodes.get(i).name);
                leafnodes.add(allnodes.get(i).name);
            }
        }
        return leafnodes;
    }

    private static void convertcurrentleafnodes(ArrayList<String> leafnodes, ArrayList<String> nodesalreadyconverted, ArrayList<String> nodesnotconverted, ArrayList<Relations> relationsalreadyconverted, ArrayList<Relations> relationsnotconverted) {
        System.out.println("before converting leafnodes the size of relationsnotconverted is: " + relationsnotconverted.size());
        System.out.println("before converting leafnodes the size of relationsalreadyconverted is: " + relationsalreadyconverted.size());
        System.out.println("before converting leafnodes the size of nodesnotconverted is: " + nodesnotconverted.size());
        System.out.println("before converting leafnodes the size of nodesalreadyconverted is: " + nodesalreadyconverted.size());
        for (int i = 0; i < leafnodes.size(); i++) {
            gohere:
            for (int j = 0; j < relationsnotconverted.size(); j++) {
                //for leafnodes with composition relations, find their source nodes and do conversion
                if ((leafnodes.get(i).matches(relationsnotconverted.get(j).targetnode.name) && (relationsnotconverted.get(j).type.matches("composition")))) {
                    //for composition relations, need to consider maxarity!!!
                    for (int m = 0; m < relationsnotconverted.get(j).maxarityfortargetnode; m++) {
                        for (int k = 0; k < relationsnotconverted.get(j).targetnode.parameternames.size(); k++) {
                            //add each targetnode parameter to sourcenode parameternames
                            relationsnotconverted.get(j).sourcenode.parameternames.add(relationsnotconverted.get(j).targetnode.name + (m + 1) + "_" + relationsnotconverted.get(j).targetnode.parameternames.get(k));

                            //add each values of a targetnode parameter to sourcenode parametervalues arraylist
                            ArrayList<String> tempparametervalues = new ArrayList<String>();
                            tempparametervalues.clear();
                            for (int l = 0; l < relationsnotconverted.get(j).targetnode.parametervalues.get(k).size(); l++) {
                                tempparametervalues.add(relationsnotconverted.get(j).targetnode.parametervalues.get(k).get(l));
                            }
                            relationsnotconverted.get(j).sourcenode.parametervalues.add(tempparametervalues);
                        }
                    }
                    //remove the used relations and add it to relationsalreadyconverted
                    relationsalreadyconverted.add((Relations) relationsnotconverted.get(j).clone());
                    relationsnotconverted.remove(j);
                    j = -1;//reset to -1 so that the loop can start over for updated relationsnotconverted
                    continue gohere;
                }
                //for leafnodes with inheritance relations, find their target nodes and do conversion
                //for inheritance relations, unlike composition relations, it is always 1:1
                if ((leafnodes.get(i).matches(relationsnotconverted.get(j).sourcenode.name) && (relationsnotconverted.get(j).type.matches("inheritance")))) {
                    for (int k = 0; k < relationsnotconverted.get(j).sourcenode.parameternames.size(); k++) {
                        //add each sourcenode parameter to targetnode parameternames
                        relationsnotconverted.get(j).targetnode.parameternames.add(relationsnotconverted.get(j).sourcenode.name + "_" + relationsnotconverted.get(j).sourcenode.parameternames.get(k));

                        //add each values of a sourcenode parameter to targetnode parametervalues arraylist
                        ArrayList<String> tempparametervalues = new ArrayList<String>();
                        tempparametervalues.clear();
                        for (int l = 0; l < relationsnotconverted.get(j).sourcenode.parametervalues.get(k).size(); l++) {
                            tempparametervalues.add(relationsnotconverted.get(j).sourcenode.parametervalues.get(k).get(l));
                        }
                        relationsnotconverted.get(j).targetnode.parametervalues.add(tempparametervalues);
                    }
                    //remove the used relations and add it to relationsalreadyconverted
                    relationsalreadyconverted.add((Relations) relationsnotconverted.get(j).clone());
                    relationsnotconverted.remove(j);
                    j = -1;//reset to -1 so that the loop can start over for updated relationsnotconverted
                    continue gohere;
                }
            }
            //update nodesnotconverted
            for (int n = 0; n < nodesnotconverted.size(); n++) {
                if (nodesnotconverted.get(n).matches(leafnodes.get(i))) {
                    nodesnotconverted.remove(n);
                }
            }
            //update nodesalreadyconverted
            nodesalreadyconverted.add(leafnodes.get(i));
        }
        System.out.println("after converting leafnodes the size of relationsnotconverted is: " + relationsnotconverted.size());
        System.out.println("after converting leafnodes the size of relationsalreadyconverted is: " + relationsalreadyconverted.size());
        System.out.println("after converting leafnodes the size of nodesnotconverted is: " + nodesnotconverted.size());
        System.out.println("after converting leafnodes the size of nodesalreadyconverted is: " + nodesalreadyconverted.size());
    }

    private static void findcurrentleafnodes(ArrayList<String> leafnodes, ArrayList<String> nodesnotconverted, ArrayList<Relations> relationsnotconverted) {
        System.out.println("previous size of leafnodes is: " + leafnodes.size());
        leafnodes.clear();
        for (int i = 0; i < nodesnotconverted.size(); i++) {
            int count = 0;//for a leafnode, in case of composition relation, no count from the source side; in case of inheritance relation, no count from the target side
            //System.out.println("Checkig node: " + nodesnotconverted.get(i) + " #####################################################");
            for (int j = 0; j < relationsnotconverted.size(); j++) {
                if (nodesnotconverted.get(i).matches(relationsnotconverted.get(j).sourcenode.name) && relationsnotconverted.get(j).type.matches("composition")) {//careful!!!!Using "matches()" is different from using ".contains(); e.g., concept "AH" contains concept "A"
                    count++;
                }
                if (nodesnotconverted.get(i).matches(relationsnotconverted.get(j).targetnode.name) && relationsnotconverted.get(j).type.matches("inheritance")) {//careful!!!!Using "matches()" is different from using ".contains()"
                    count++;
                }
            }
            if (count == 0) {
                //System.out.println("Find one leaf node: " + nodesnotconverted.get(i));
                leafnodes.add(nodesnotconverted.get(i));
            }
        }
        System.out.println("current size of leafnodes is: " + leafnodes.size());
        for (int i = 0; i < leafnodes.size(); i++) {
            System.out.print(leafnodes.get(i) + "===");
        }
        System.out.println();
    }

    private static void summary(ArrayList<Nodes> nodesalreadyconverted) {
        for (int i = 0; i < nodesalreadyconverted.size(); i++) {
            System.out.println(nodesalreadyconverted.get(i).name);
            for (int j = 0; j < nodesalreadyconverted.get(i).parameternames.size(); j++) {
                String hhh = "";
                hhh = nodesalreadyconverted.get(i).parameternames.get(j) + ": ";
                for (int k = 0; k < nodesalreadyconverted.get(i).parametervalues.get(j).size(); k++) {
                    hhh = hhh + nodesalreadyconverted.get(i).parametervalues.get(j).get(k) + " ";
                }
                System.out.println(hhh);
            }
            System.out.println();
        }

        System.out.println("************************************************************");

    }

    private static void generateinputmodels(ArrayList<File> inputmodels, ArrayList<Nodes> allnodes, File graphml, int strength) throws IOException, InterruptedException {
        String foldername = graphml.getName();
        //String path = "C:\\Users\\yihaoli\\Documents\\NetBeansProjects";
        Nodes root = allnodes.get(0);
        for (int i = 0; i < allnodes.size(); i++) {
            if (root.parameternames.size() < allnodes.get(i).parameternames.size()) {
                root = allnodes.get(i);
            }
        }
        File newfolder = new File(graphml.getParentFile().getPath() + "/" + foldername + "_results" + "/");
        //File newfolder = new File(path + "/" + foldername + "_results" + "/");
        newfolder.mkdir();
        String filename = newfolder.getPath() + "/" + root.name + "_" + strength + ".txt";
        File file = new File(filename);
        file.createNewFile();
        FileWriter fw = null;
        BufferedWriter bw = null;
        fw = new FileWriter(file);
        bw = new BufferedWriter(fw);

        bw.write("[System]");
        bw.newLine();
        bw.write("Name: " + root.name);
        bw.newLine();
        bw.write("[Parameter]");
        bw.newLine();
        for (int j = 0; j < root.parameternames.size(); j++) {
            bw.write(root.parameternames.get(j) + " (enum) : ");
            String temp = "";
            for (int k = 0; k < root.parametervalues.get(j).size(); k++) {
                temp = temp + root.parametervalues.get(j).get(k) + ", ";
            }
            bw.write(temp.substring(0, temp.length() - 2));
            bw.newLine();
        }
        bw.write("[Constraint]");
        bw.newLine();
        bw.close();
        fw.close();
        TimeUnit.SECONDS.sleep(3);//
        inputmodels.add(file);
//        for (int i = 0; i < allnodes.size(); i++) {
//            File newfolder = new File(graphml.getParentFile().getPath() + "/" + foldername + "_results" + "/");
//            //File newfolder = new File(path + "/" + foldername + "_results" + "/");
//            newfolder.mkdir();
//            String filename = newfolder.getPath() + "/" + allnodes.get(i).name + "_" + strength + ".txt";
//            File file = new File(filename);
//            file.createNewFile();
//            FileWriter fw = null;
//            BufferedWriter bw = null;
//            fw = new FileWriter(file);
//            bw = new BufferedWriter(fw);
//
//            bw.write("[System]");
//            bw.newLine();
//            bw.write("Name: " + allnodes.get(i).name);
//            bw.newLine();
//            bw.write("[Parameter]");
//            bw.newLine();
//            for (int j = 0; j < allnodes.get(i).parameternames.size(); j++) {
//                bw.write(allnodes.get(i).parameternames.get(j) + " (enum) : ");
//                String temp = "";
//                for (int k = 0; k < allnodes.get(i).parametervalues.get(j).size(); k++) {
//                    temp = temp + allnodes.get(i).parametervalues.get(j).get(k) + ", ";
//                }
//                bw.write(temp.substring(0, temp.length() - 2));
//                bw.newLine();
//            }
//            bw.write("[Constraint]");
//            bw.newLine();
//            bw.close();
//            fw.close();
//            TimeUnit.SECONDS.sleep(3);//
//            inputmodels.add(file);
//        }
    }

    private static void runacts(ArrayList<File> inputmodels, int strength) throws IOException, InterruptedException {
//        for (int i = 0; i < inputmodels.size(); i++) {
//            Runtime rr = Runtime.getRuntime();
//            Process pp = null;
//            String s1 = "cmd /k start java -Dalgo=ipog -Ddoi=" + strength + " -jar acts_3.1.jar ";
//            String s2 = inputmodels.get(i).getPath() + " " + inputmodels.get(i).getParent() + "\\" + inputmodels.get(i).getName() + "_output_" + strength + ".xml";
//            //s3 = file1.getParent() + "\\" + file1.getName() + "_output.xml";
//            String s = s1 + s2;
//            //System.out.println(s);
//            pp = rr.exec(s);
//            //System.out.println("hahaha CT-based test suite for source node: " + inputmodels.get(i).getName() + " has been generated!");
//            //System.out.println();
//            TimeUnit.SECONDS.sleep(1);
//        }
        File root;
        root = inputmodels.get(0);
        for (int i = 0; i < inputmodels.size(); i++) {
            if (root.length() < inputmodels.get(i).length()) {
                root = inputmodels.get(i);
            }
            System.out.println(root.getName());

            Runtime rr = Runtime.getRuntime();
            Process pp = null;
            String s1 = "cmd /k start java -Dalgo=ipog -Ddoi=" + strength + " -jar acts_3.1.jar ";
            String s2 = root.getPath() + " " + root.getParent() + "\\" + root.getName() + "_output_" + strength + ".xml";
            String s = s1 + s2;
            pp = rr.exec(s);
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private static void extractinformation(ArrayList<Nodes> allnodes, ArrayList<Relations> allrelations, int num_of_nodes, int num_of_relations, File graphml, ArrayList<Parameters> allparametersandvalues) throws FileNotFoundException, IOException {
        String foldername = graphml.getName();
        FileReader fr1 = new FileReader(graphml);
        BufferedReader bf1 = new BufferedReader(fr1);

        int num_of_lines = 0;
        ArrayList<String> lines = new ArrayList<String>();
        lines.clear();
        while (bf1.ready()) {
            lines.add(bf1.readLine());
            num_of_lines++;
        }
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("<y:UMLClassNode>")) {
                Nodes tempnode = new Nodes();
                num_of_nodes++;
                String temp = "";
                temp = lines.get(i + 4);
                //==============================================================
                //get the node name
                int left = 0;
                int right = 0;
                for (int j = 0; j < temp.length() - temp.indexOf("y="); j++) {
                    if (temp.charAt(temp.indexOf("y=") + j) == '>') {
                        //System.out.println(temp.charAt(temp.indexOf("y=") + j));
                        left = temp.indexOf("y=") + j + 1;
                        break;
                    }
                }
                for (int j = 0; j < temp.length() - temp.indexOf("y="); j++) {
                    if (temp.charAt(temp.indexOf("y=") + j) == '<') {
                        //System.out.println(temp.charAt(temp.indexOf("y=") + j));
                        right = temp.indexOf("y=") + j;
                        break;
                    }
                }
                //System.out.println(temp.substring(left, right));
                tempnode.name = temp.substring(left, right);
                //end of get the node name
                //==============================================================
                //get the attribute             
                temp = "";
                int count = 0;
                for (int j = 0; j < lines.size(); j++) {

                    if (lines.get(i + j).contains("<y:AttributeLabel")) {
                        count = i + j;
                        break;
                    }
                }
                temp = lines.get(count);
                int count1 = 0;
                for (int j = 0; j < lines.size(); j++) {
                    if (lines.get(i + j).contains("</y:AttributeLabel>")) {
                        count1 = i + j;
                        break;
                    }
                }
                String temp1 = "";
                temp1 = lines.get(count1);
                String temp2 = "";
                for (int j = count; j < count1 + 1; j++) {
                    temp2 = temp2 + lines.get(j);
                }
                String temp3 = "";
                temp3 = temp2.substring(temp2.indexOf(">") + 1, temp2.indexOf("</"));
                String[] attributes = new String[temp3.split("\\+").length];
                attributes = temp3.split("\\+");
//                for (int j = 1; j < attributes.length; j++) {
//                    tempnode.allattributes.add(attributes[j]);
//                }
                allnodes.add((Nodes) tempnode.clone());
                //end of get the attribute
                //==============================================================
            }//end of "if" for finding nodes and attributes
            //==================================================================
            //get the relations and their types
            //there are two relations: inheritance and composition
            //"allrelations" stores all Relations classes
            if (lines.get(i).contains("<edge id=")) {
                Relations temprelation = new Relations();
                num_of_relations = num_of_relations + 1;
                //System.out.println(lines.get(i));

                //get relation type
                for (int j = 0; j < lines.size() - i; j++) {
                    if (lines.get(i + j).contains("<y:Arrows")) {
                        if (lines.get(i + j).contains("none") && lines.get(i + j).contains("white_delta")) {
                            temprelation.type = "inheritance";
                            temprelation.minarityfortargetnode = 1;
                            temprelation.maxarityfortargetnode = 1;
                            break;
                        }
                        if (lines.get(i + j).contains("diamond") && lines.get(i + j).contains("plain")) {
                            temprelation.type = "composition";
                            break;
                        }
                    }
                }//end of get relation type
                String temp4 = "";
                temp4 = lines.get(i);
                String[] temp5 = temp4.split("\"");

                //get source node and target node
                for (int j = 0; j < allnodes.size(); j++) {
                    if (temp5[3].trim().contains(String.valueOf(j).trim())) {
                        temprelation.sourcenode = allnodes.get(j);//here is the connection between relations and nodes
                    }
                    if (temp5[5].trim().contains(String.valueOf(j).trim())) {
                        temprelation.targetnode = allnodes.get(j);//here is the connection between relations and nodes
                    }
                }//end of get source node and target node
                //get maxarity and minarity for target node
                if (temprelation.type.contains("inheritance")) {
                    temprelation.minarityfortargetnode = 1;
                    temprelation.maxarityfortargetnode = 1;
                }
                if (temprelation.type.contains("composition")) {
                    int tempcount = 0;
                    for (int j = 0; j < lines.size() - i; j++) {
                        if (lines.get(i + j).contains("<y:EdgeLabel")) {
                            tempcount++;
                            if (tempcount == 2) {
                                String temp6 = "";
                                temp6 = lines.get(i + j).substring(lines.get(i + j).indexOf(">") + 1, lines.get(i + j).indexOf("<y:LabelModel>"));
                                if (temp6.contains("...")) {
                                    String temp7[] = temp6.split("\\.\\.\\.");
                                    temprelation.minarityfortargetnode = Integer.valueOf(temp7[0]);
                                    temprelation.maxarityfortargetnode = Integer.valueOf(temp7[1]);
                                    break;
                                } else {
                                    temprelation.minarityfortargetnode = Integer.valueOf(temp6);
                                    temprelation.maxarityfortargetnode = Integer.valueOf(temp6);
//                                if (temprelation.type.contains("composition")) {
//                                    temprelation.minarityfortargetnode = 1;
//                                }
//                                if (temprelation.type.contains("inheritance")) {
//                                    temprelation.minarityfortargetnode = 1;
//                                    temprelation.maxarityfortargetnode = 1;
//                                }
                                    break;
                                }
                            }
                        }
                    }
                }
//end of get maxarity and minarity for target node
                allrelations.add(temprelation);//here is the connection between relations and nodes
            }//end of "if" for finding relations and their types
            //==================================================================
        }//end of "for" loop for going through each line
        for (int i = 0; i < lines.size(); i++) {
            //finding nodes that have parameters and values
            if (lines.get(i).contains("Node:")) {
                String[] temp8 = lines.get(i).split("Node:");

                //get the parameter name
                String tempnodename = temp8[1].trim();
                //System.out.println("Node: " + tempnodename);

                //for each node, get parameters and their values
                for (int j = 1; j < lines.size(); j++) {
                    //int parameternumber=0;
                    if (lines.get(i + j).contains("Parameter:")) {
                        Parameters tempparameter = new Parameters();
                        tempparameter.nodename = tempnodename;

                        if (!lines.get(i + j).matches("range")) {
                            if (!lines.get(i + j).contains("<y:LabelModel>")) {
                                String temp9[] = lines.get(i + j).split(";");
                                tempparameter.parametername = temp9[0].replaceAll("Parameter:", "").trim();
                                tempparameter.values = temp9[1].trim();
                                //System.out.println("Parameter " + j + ": " + tempparameter.parametername + "; type and values: " + tempparameter.values);

                            } else {
                                String temp9[] = lines.get(i + j).split(";");
                                tempparameter.parametername = temp9[0].replaceAll("Parameter:", "").trim();
                                tempparameter.values = temp9[1].replaceAll("<y:LabelModel>", "").trim();
                                //System.out.println("Parameter " + j + ": " + tempparameter.parametername + "; type and values: " + tempparameter.values);

                            }
                        } else {
                            if (!lines.get(i + j).contains("<y:LabelModel>")) {
                                String temp9[] = lines.get(i + j).split(";");
                                tempparameter.parametername = temp9[0].replaceAll("Parameter:", "").trim();
                                String temp10[] = temp9[1].split(":");
                                int temp11[] = new int[temp10[1].split("~").length];
                                String temp12[] = temp10[1].split("~");
                                for (int k = 0; k < temp11.length; k++) {
                                    temp11[k] = Integer.parseInt(temp12[k].trim());
                                }
                                String temp13 = "";
                                for (int k = 0; k < temp11[1] - temp11[0] + 1; k++) {
                                    temp13 = temp13 + (temp11[0] + k) + ",";
                                }
                                tempparameter.values = "(range): " + temp13.substring(0, temp13.length() - 1);
                                //System.out.println("Parameter " + j + ": " + tempparameter.parametername + "; type and values: " + tempparameter.values);
                            } else {
                                String temp9[] = lines.get(i + j).split(";");
                                tempparameter.parametername = temp9[0].replaceAll("Parameter:", "").trim();
                                String temp14 = temp9[1].replaceAll("<y:LabelModel>", "");
                                temp9[1] = temp14.trim();
                                String temp10[] = temp9[1].split(":");
                                int temp11[] = new int[temp10[1].split("~").length];
                                String temp12[] = temp10[1].split("~");
                                for (int k = 0; k < temp11.length; k++) {
                                    temp11[k] = Integer.parseInt(temp12[k].trim());
                                }
                                String temp13 = "";
                                for (int k = 0; k < temp11[1] - temp11[0] + 1; k++) {
                                    temp13 = temp13 + (temp11[0] + k) + ",";
                                }
                                tempparameter.values = "(range): " + temp13.substring(0, temp13.length() - 1);
                                //System.out.println("Parameter " + j + ": " + tempparameter.parametername + "; type and values: " + tempparameter.values);
                            }
                        }
                        allparametersandvalues.add(tempparameter);
                    } else {
                        break;
                    }
                }

            }
            //end of finding values for each parameter of all available nodes
        }
        bf1.close();
        fr1.close();

        for (int i = 0; i < allnodes.size(); i++) {
            for (int j = 0; j < allparametersandvalues.size(); j++) {
                if (allnodes.get(i).name.matches(allparametersandvalues.get(j).nodename)) {//CAREFULL!!!!!!!!! using matches() or contains() will cause a difference!!!!!
                    allnodes.get(i).parameternames.add(allparametersandvalues.get(j).parametername);
                    String tempa = allparametersandvalues.get(j).values.split(":")[1].trim();
                    String[] tempb = tempa.split(",");
                    ArrayList<String> templist = new ArrayList<String>();
                    templist.clear();
                    for (int k = 0; k < tempb.length; k++) {
                        templist.add(tempb[k].trim());
                    }
                    allnodes.get(i).parametervalues.add((ArrayList<String>) templist.clone());
                }
            }
        }
        //now "allnodes" stores all Nodes classes with their attributes
        //now "allrelations" stores all Relations classes with name, source node, target node, and type
        //now "allparametersandvalues" stores all node parameters and values
        //for each node, "parameternames" stores all the parnameter names of this node
        //for each parameter of a node in "allnodes", "parametervalues" stores the values of each parameter of the node in "allnodes"
        //the size of "parameternames"= the size of "parametervalues" and the parameter values of parameternames.get(i) are stored in parametervalues.get(i)
        System.out.println("Total number of nodes: " + num_of_nodes + "@@@" + allnodes.size());
        System.out.println("Total number of relations: " + num_of_relations + "@@@" + allrelations.size());
        System.out.println("************************************************************");

        for (int i = 0; i < allnodes.size(); i++) {
            System.out.println(allnodes.get(i).name);
            for (int j = 0; j < allnodes.get(i).parameternames.size(); j++) {
                String hhh = "";
                hhh = allnodes.get(i).parameternames.get(j) + ": ";
                for (int k = 0; k < allnodes.get(i).parametervalues.get(j).size(); k++) {
                    hhh = hhh + allnodes.get(i).parametervalues.get(j).get(k) + " ";
                }
                System.out.println(hhh);
            }
            System.out.println();
        }

        System.out.println("************************************************************");

        for (int j = 0; j < allrelations.size(); j++) {
            allrelations.get(j).name = allrelations.get(j).getsourcenode().getname() + "_" + allrelations.get(j).gettargetnode().getname() + "_" + allrelations.get(j).gettype();
            System.out.println("relation " + (j + 1) + ": " + allrelations.get(j).getname());
            System.out.println("source node: " + allrelations.get(j).getsourcenode().getname() + "; " + "target node: " + allrelations.get(j).gettargetnode().getname() + "; " + "relation type: " + allrelations.get(j).gettype());
            System.out.println("minarity for targetnode: " + allrelations.get(j).getminarity() + "; " + "maxarity for targetnode: " + allrelations.get(j).getmaxarity());
            System.out.println();
        }
        System.out.println("************************************************************");
    }
}//end of Class OntologyExtraction
