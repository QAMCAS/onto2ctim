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
public class OntologyExtraction {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, CloneNotSupportedException, InterruptedException {
        // TODO code application logic here
        //File graphml = new File("C:/Users/liyih/Desktop/road_section_simple.graphml");
        //File graphml = new File("C:/Users/liyih/Desktop/test.graphml");
        
        //File graphml = new File("C:\\Users\\liyih\\Documents\\NetBeansProjects\\OntologyExtraction\\case-study\\simple.graphml");
        //File graphml = new File("C:\\Users\\liyih\\Documents\\NetBeansProjects\\OntologyExtraction\\road_section.graphml");
        File graphml = new File("C:\\Users\\liyih\\Documents\\NetBeansProjects\\OntologyExtraction\\complex2.graphml");
        int strength=2;
        String algorithm="YL";

        String foldername = graphml.getName();
        FileReader fr1 = new FileReader(graphml);
        BufferedReader bf1 = new BufferedReader(fr1);
        //System.out.println(bf1.readLine());
        int num_of_lines = 0;
        ArrayList<String> lines = new ArrayList<String>();
        lines.clear();
        while (bf1.ready()) {
            lines.add(bf1.readLine());
            num_of_lines++;
        }
        //System.out.println(num_of_lines);
        //System.out.println(lines.size());
        int num_of_nodes = 0;
        int num_of_relations = 0;
        ArrayList<Nodes> allnodes = new ArrayList<>();
        ArrayList<Relations> allrelations = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("<y:UMLClassNode>")) {
                Nodes tempnode = new Nodes();
                num_of_nodes++;
                String temp = "";
                temp = lines.get(i + 4);
                //System.out.println(lines.get(i + 4));
                //System.out.println(temp.indexOf("y="));
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
                //System.out.println("now entering the attribute section......");
                temp = "";
                int count = 0;
                for (int j = 0; j < lines.size(); j++) {
                    //System.out.println(j);
                    if (lines.get(i + j).contains("<y:AttributeLabel")) {
                        count = i + j;
                        //System.out.println(count);
                        break;
                    }
                }
                temp = lines.get(count);
                //System.out.println("temp:========" + temp);
                int count1 = 0;
                for (int j = 0; j < lines.size(); j++) {
                    if (lines.get(i + j).contains("</y:AttributeLabel>")) {
                        count1 = i + j;
                        break;
                    }
                }
                String temp1 = "";
                temp1 = lines.get(count1);
                //System.out.println("temp1:=========" + temp1);
                String temp2 = "";
                for (int j = count; j < count1 + 1; j++) {
                    temp2 = temp2 + lines.get(j);
                }
                //System.out.println("temp2==========" + temp2);
                //System.out.println(temp2.substring(temp2.indexOf(">") + 1, temp2.indexOf("</")));
                String temp3 = "";
                temp3 = temp2.substring(temp2.indexOf(">") + 1, temp2.indexOf("</"));
                if (temp3.length() > 2) {
                    //System.out.println(temp3);
                    //System.out.println(temp3.split("\\+").length);
                }
                String[] attributes = new String[temp3.split("\\+").length];
                attributes = temp3.split("\\+");
                for (int j = 1; j < attributes.length; j++) {
                    tempnode.allattributes.add(attributes[j]);
                    //System.out.println(attributes[j]);
                }
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
                //System.out.println(lines.get(i) + "=========" + num_of_relations);
                //get relation type
                for (int j = 0; j < lines.size() - i; j++) {
                    if (lines.get(i + j).contains("<y:Arrows")) {
                        if (lines.get(i + j).contains("none") && lines.get(i + j).contains("white_delta")) {
                            temprelation.type = "inheritance";
                            temprelation.minarityfortargetnode = 1;
                            temprelation.maxarityfortargetnode = 1;
                            //System.out.println("The relation type is: " + temprelation.gettype());
                            break;
                        }
                        if (lines.get(i + j).contains("diamond") && lines.get(i + j).contains("plain")) {
                            temprelation.type = "composition";
                            //System.out.println("The relation type is: " + temprelation.gettype());
                            break;
                        }
                    }
                }//end of get relation type
                String temp4 = "";
                temp4 = lines.get(i);
                String[] temp5 = temp4.split("\"");
                //System.out.println(temp5.length);
                for (int j = 0; j < temp5.length; j++) {
                    //System.out.println(temp5[j]);
                }
                //get source node and target node
                for (int j = 0; j < allnodes.size(); j++) {
                    if (temp5[3].contains(String.valueOf(j))) {
                        temprelation.sourcenode = allnodes.get(j);//here is the connection between relations and nodes
                        //System.out.println("sourcecode: " + temprelation.getsourcenode());
                    }
                    if (temp5[5].contains(String.valueOf(j))) {
                        temprelation.targetnode = allnodes.get(j);//here is the connection between relations and nodes
                        //System.out.println("targetcode: " + temprelation.gettargetnode());
                    }
                }//end of get source node and target node
                //get maxarity and minarity for target node
                int tempcount = 0;
                for (int j = 0; j < lines.size() - i; j++) {
                    if (lines.get(i + j).contains("<y:EdgeLabel")) {
                        //System.out.println("Line: "+(i+j+1)+"####################################################");
                        tempcount++;//CAREFUL!!!!!!!!!!!!!!!!!this was delted from the original version
                        if (tempcount == 2) {
                            //System.out.println(lines.get(i+j)+"****************************************************");
                            String temp6 = "";
                            temp6 = lines.get(i + j).substring(lines.get(i + j).indexOf(">") + 1, lines.get(i + j).indexOf("<y:LabelModel>"));
                            //System.out.println(temp6);
                            if (temp6.contains("...")) {
                                //System.out.println(temp6+"=========");
                                String temp7[] = temp6.split("\\.\\.\\.");
                                //System.out.println(temp7.length+"~~~~");
                                temprelation.minarityfortargetnode = Integer.valueOf(temp7[0]);
                                temprelation.maxarityfortargetnode = Integer.valueOf(temp7[1]);
//                                for(int k=0;k<temp7.length;k++){
//                                    System.out.println(temp7[k]);
//                                }
                                break;
                            } else {
                                temprelation.minarityfortargetnode = Integer.valueOf(temp6);
                                temprelation.maxarityfortargetnode = Integer.valueOf(temp6);
                                if (temprelation.type.contains("composition")) {
                                    temprelation.minarityfortargetnode = 1;
                                }
                                if (temprelation.type.contains("inheritance")) {
                                    temprelation.minarityfortargetnode = 1;
                                    temprelation.maxarityfortargetnode = 1;
                                }
                                break;
                            }
                        }
                    }
            }//end of get maxarity and minarity for target node
                allrelations.add(temprelation);//here is the connection between relations and nodes
            }//end of "if" for finding relations and their types
            //==================================================================
        }//end of "for" loop for going through each line
        //======================================================================
        ArrayList<Parameters> allparametersandvalues = new ArrayList<Parameters>();
        for (int i = 0; i < lines.size(); i++) {
            //finding nodes that have parameters and values
            if (lines.get(i).contains("Node:")) {
                String[] temp8 = lines.get(i).split("Node:");
                //System.out.println(temp8.length+"============================================================================");
//                for(int j=0;j<temp8.length;j++){
//                    System.out.println(temp8[j]);
//                }
                //Parameters tempparameter = new Parameters();
                //get the parameter name
                String tempnodename = temp8[1].trim();
                System.out.println("Node: " + tempnodename);

                //for each node get parameters and their values
                for (int j = 1; j < lines.size(); j++) {
                    //int parameternumber=0;
                    if (lines.get(i + j).contains("Parameter:")) {
                        Parameters tempparameter = new Parameters();
                        tempparameter.nodename = tempnodename;

                        if (!lines.get(i + j).contains("range")) {
                            if (!lines.get(i + j).contains("<y:LabelModel>")) {
                                String temp9[] = lines.get(i + j).split(";");
                                tempparameter.parametername = temp9[0].replaceAll("Parameter:", "").trim();
                                tempparameter.values = temp9[1].trim();
                                System.out.println("Parameter " + j + ": " + tempparameter.parametername + "; type and values: " + tempparameter.values);

                            } else {
                                String temp9[] = lines.get(i + j).split(";");
                                tempparameter.parametername = temp9[0].replaceAll("Parameter:", "").trim();
                                tempparameter.values = temp9[1].replaceAll("<y:LabelModel>", "").trim();
                                System.out.println("Parameter " + j + ": " + tempparameter.parametername + "; type and values: " + tempparameter.values);

                            }
                        } else {
                            if (!lines.get(i + j).contains("<y:LabelModel>")) {
                                String temp9[] = lines.get(i + j).split(";");
                                tempparameter.parametername = temp9[0].replaceAll("Parameter:", "").trim();

                                String temp10[] = temp9[1].split(":");
                                int temp11[] = new int[temp10[1].split("~").length];
                                String temp12[] = temp10[1].split("~");
                                for (int k = 0; k < temp11.length; k++) {
                                    //System.out.println(temp12[k]);
                                    temp11[k] = Integer.parseInt(temp12[k].trim());
                                }
                                String temp13 = "";
                                for (int k = 0; k < temp11[1] - temp11[0] + 1; k++) {
                                    temp13 = temp13 + (temp11[0] + k) + ",";
                                }
                                tempparameter.values = "(range): " + temp13.substring(0, temp13.length() - 1);
                                System.out.println("Parameter " + j + ": " + tempparameter.parametername + "; type and values: " + tempparameter.values);

                            } else {
                                String temp9[] = lines.get(i + j).split(";");
                                tempparameter.parametername = temp9[0].replaceAll("Parameter:", "").trim();
                                String temp14 = temp9[1].replaceAll("<y:LabelModel>", "");
                                temp9[1] = temp14.trim();
                                String temp10[] = temp9[1].split(":");
                                int temp11[] = new int[temp10[1].split("~").length];
                                String temp12[] = temp10[1].split("~");
                                for (int k = 0; k < temp11.length; k++) {
                                    //System.out.println(temp12[k]);
                                    temp11[k] = Integer.parseInt(temp12[k].trim());
                                }
                                String temp13 = "";
                                for (int k = 0; k < temp11[1] - temp11[0] + 1; k++) {
                                    temp13 = temp13 + (temp11[0] + k) + ",";
                                }
                                tempparameter.values = "(range): " + temp13.substring(0, temp13.length() - 1);
                                System.out.println("Parameter " + j + ": " + tempparameter.parametername + "; type and values: " + tempparameter.values);
                            }
                        }

//                        for (int k = 0; k < allnodes.size(); k++) {
//                            if (allnodes.get(k).name.contains(tempparameter.nodename)) {
//                                allnodes.get(k).allparameters.add(tempparameter);
//                            }
//                        }
                        allparametersandvalues.add(tempparameter);
                    } else {
                        break;
                    }
                }

            }
            //end of finding values for each parameter of all available nodes
        }
        //======================================================================
        //now "allnodes" stores all Nodes classes with their attributes
        //now "allrelations" stores all Relations classes with name, source node, target node, and type
        //now "allparametersandvalues" store all node parameters and values

        for (int i = 0; i < allparametersandvalues.size(); i++) {
            System.out.println("Node: " + allparametersandvalues.get(i).nodename + "; Parameter: " + allparametersandvalues.get(i).parametername + "; Values: " + allparametersandvalues.get(i).values);
        }

        System.out.println("Total number of nodes: " + num_of_nodes);
        System.out.println("Total number of relations: " + num_of_relations);
        System.out.println();
//        for (int j = 0; j < allnodes.size(); j++) {
//            System.out.println("node " + (j + 1) + ": " + allnodes.get(j).getname());
//            for (int l = 0; l < allnodes.get(j).allparameters.size(); l++) {
//                System.out.println("attribute " + (l + 1) + ": " + allnodes.get(j).allparameters.get(l).parametername + ": " + allnodes.get(j).allparameters.get(l).values);
//            }
//            System.out.println();
//        }

        System.out.println("************************************************************");
        for (int j = 0; j < allrelations.size(); j++) {
            allrelations.get(j).name = allrelations.get(j).getsourcenode().getname() + "_" + allrelations.get(j).gettargetnode().getname() + "_" + allrelations.get(j).gettype();
//            System.out.println("relation " + (j + 1) + ": " + allrelations.get(j).getname());
//            System.out.println("source node: " + allrelations.get(j).getsourcenode().getname() + "; " + "target node: " + allrelations.get(j).gettargetnode().getname() + "; " + "relation type: " + allrelations.get(j).gettype());
//            System.out.println("minarity for targetnode: " + allrelations.get(j).getminarity() + "; " + "maxarity for targetnode: " + allrelations.get(j).getmaxarity());
//            System.out.println();
        }
        //======================================================================
        //get the hierarchicallevel for each node
        //ArrayList<ArrayList<String>> nodeontology = new ArrayList<ArrayList<String>>();

        bf1.close();
        fr1.close();
        System.gc();
        checknodelevel(allnodes, allrelations, allparametersandvalues, foldername, graphml, strength, algorithm);  
    }//end of main()

    private static void checknodelevel(ArrayList<Nodes> allnodes, ArrayList<Relations> allrelations, ArrayList<Parameters> allparametersandvalues, String foldername, File graphml, int strength, String algorithm) throws IOException, InterruptedException {
        ArrayList<ArrayList<Relations>> nodeontology = new ArrayList<ArrayList<Relations>>();

        for (int i = 0; i < allnodes.size(); i++) {
            ArrayList<Relations> onenodeontology = new ArrayList<Relations>();
            onenodeontology.clear();

            for (int j = 0; j < allrelations.size(); j++) {
                System.gc();
                if ((allrelations.get(j).sourcenode.name.contains(allnodes.get(i).name) && allrelations.get(j).type.contains("composition")) || (allrelations.get(j).targetnode.name.contains(allnodes.get(i).name) && allrelations.get(j).type.contains("inheritance"))) {
                    if (allrelations.get(j).type.contains("composition")) {
                        Relations noderelation = new Relations();
                        noderelation = allrelations.get(j);
                        onenodeontology.add(noderelation);
                        //System.out.println(allrelations.get(j).sourcenode.name + " --> " + allrelations.get(j).targetnode.name);
                        checkfurthernode(onenodeontology, allrelations.get(j).targetnode.name, allrelations);
                    }
                    if (allrelations.get(j).type.contains("inheritance")) {
                        Relations noderelation = new Relations();
                        noderelation = allrelations.get(j);
                        onenodeontology.add(noderelation);
                        //System.out.println(allrelations.get(j).targetnode.name + " --> " + allrelations.get(j).sourcenode.name);
                        checkfurthernode(onenodeontology, allrelations.get(j).sourcenode.name, allrelations);
                    }
                }
            }
            //System.out.println();
            nodeontology.add(onenodeontology);
        }
        for (int i = 0; i < nodeontology.size(); i++) {
            //System.out.println("Node ontology " + (i + 1) + ": " + nodeontology.get(i).size());
        }
        List<Integer> size = new ArrayList<Integer>();
        for (int i = 0; i < nodeontology.size(); i++) {
            size.add(nodeontology.get(i).size());
        }
        Collections.sort(size);
        for (int i = 0; i < nodeontology.size(); i++) {
            if (nodeontology.get(i).size() == Collections.max(size)) {
                //System.out.println(nodeontology.get(i).size()+"@@@@@@@@@@@@");
                //nodeontology.get(i) is what I need
                for (int j = nodeontology.get(i).size() - 1; j >= 0; j--) {
                    //System.out.println("Relation " + (j + 1) + " : " + nodeontology.get(i).get(j).name);

                    if (nodeontology.get(i).get(j).type.contains("composition")) {
                        for (int k = 0; k < nodeontology.get(i).get(j).maxarityfortargetnode; k++) {
                            for (int l = 0; l < nodeontology.get(i).get(j).targetnode.allattributes.size(); l++) {
                                nodeontology.get(i).get(j).sourcenode.allattributes.add(nodeontology.get(i).get(j).targetnode.name + (k + 1) + "_" + nodeontology.get(i).get(j).targetnode.allattributes.get(l));
                            }
                        }
                    }

                    if (nodeontology.get(i).get(j).type.contains("inheritance")) {
                        for (int k = 0; k < nodeontology.get(i).get(j).maxarityfortargetnode; k++) {
                            for (int l = 0; l < nodeontology.get(i).get(j).sourcenode.allattributes.size(); l++) {
                                nodeontology.get(i).get(j).targetnode.allattributes.add(nodeontology.get(i).get(j).sourcenode.name + (k + 1) + "_" + nodeontology.get(i).get(j).sourcenode.allattributes.get(l));
                            }
                        }
                    }

                }
            }
        }

        //======================================================================
        System.gc();
        ArrayList<Nodes> nodesused = new ArrayList<Nodes>();
        nodesused.clear();

        for (int i = 0; i < allnodes.size(); i++) {
            Nodes nodetemp = new Nodes();
            nodetemp.name = allnodes.get(i).name;
            //System.out.println("Node " + allnodes.get(i).name + ": " + allnodes.get(i).allattributes.size());
            for (int j = 0; j < allnodes.get(i).allattributes.size(); j++) {
                String temp = "";
                temp = allnodes.get(i).allattributes.get(j).split(":")[0];
                //System.out.println(temp + "$$$$$$$$$$$$$$$$$$$$");
                for (int k = 0; k < allparametersandvalues.size(); k++) {
                    if (!temp.contains("_")) {
                        if (allnodes.get(i).name.contains(allparametersandvalues.get(k).nodename) && temp.matches(allparametersandvalues.get(k).parametername)) {
                            //System.out.println(temp+"000000000000000");
                            //System.out.println("Attribute " + (j + 1) + ": " + temp + allparametersandvalues.get(k).values);
                            nodetemp.allattributes.add(temp + " " + allparametersandvalues.get(k).values);
                            //nodesused.add(nodetemp);
                            //System.out.println();
                        }
                    } else {
                        String temp1[] = temp.split("_");
                        if (temp1[temp1.length - 2].contains(allparametersandvalues.get(k).nodename) && temp1[temp1.length - 1].matches(allparametersandvalues.get(k).parametername)) {
                            //System.out.println("Attribute " + (j + 1) + ": " + temp + allparametersandvalues.get(k).values);
                            nodetemp.allattributes.add(temp + " " + allparametersandvalues.get(k).values);
                            //nodesused.add(nodetemp);
                            //System.out.println();
                        }
                    }

                }
            }
            nodesused.add(nodetemp);
        }
        System.gc();
        for (int i = 0; i < nodesused.size(); i++) {
            System.out.println("Node: " + nodesused.get(i).name);
            for (int j = 0; j < nodesused.get(i).allattributes.size(); j++) {
                System.out.println("Attribute " + (j + 1) + "==>" + nodesused.get(i).allattributes.get(j));
            }
            System.out.println();
        }

        //=======================================================================
        //create files
        for (int i = 0; i < nodesused.size(); i++) {
            
            File newfolder = new File(graphml.getParentFile().getPath()+"/"+foldername+"_results" + "/");
            newfolder.mkdir();
            String filename = newfolder.getPath() + "/" + nodesused.get(i).name+"_" +algorithm+"_"+strength+ ".txt";
            File file = new File(filename);
            file.createNewFile();
            FileWriter fw = null;
            BufferedWriter bw = null;
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);

            bw.write("[System]");
            bw.newLine();
            bw.write("Name: " + nodesused.get(i).name);
            bw.newLine();
            bw.write("[Parameter]");
            bw.newLine();
            for (int j = 0; j < nodesused.get(i).allattributes.size(); j++) {
                bw.write(nodesused.get(i).allattributes.get(j));
                bw.newLine();
            }
            bw.write("[Constraint]");
            bw.newLine();
            bw.close();
            fw.close();
            TimeUnit.SECONDS.sleep(3);//秒
            //==================================================================
            //call ACTS
            Runtime rr = Runtime.getRuntime();
            Process pp = null;
            String s1 = "cmd /k start java -Dalgo=ipog -Ddoi="+strength+" -jar acts_3.1.jar ";
            String s2 = file.getPath() + " " + file.getParent() + "\\" + file.getName() + "_output_"+strength+".xml";
            //s3 = file1.getParent() + "\\" + file1.getName() + "_output.xml";
            String s = s1 + s2;
            System.out.println(s);
            pp = rr.exec(s);
            System.out.println("hahaha CT-based test suite for source node: " + nodesused.get(i).getname() + " has been generated!");
            System.out.println();
            TimeUnit.SECONDS.sleep(1);//秒
            //==================================================================
        }

    }

    private static void checkfurthernode(ArrayList<Relations> onenodeontology, String previousnode, ArrayList<Relations> allrelations) {
        System.gc();
        for (int i = 0; i < allrelations.size(); i++) {
            if ((allrelations.get(i).sourcenode.name.contains(previousnode) && allrelations.get(i).type.contains("composition")) || (allrelations.get(i).targetnode.name.contains(previousnode) && allrelations.get(i).type.contains("inheritance"))) {
                if (allrelations.get(i).type.contains("composition")) {

                    Relations noderelation = new Relations();
                    noderelation = allrelations.get(i);
                    onenodeontology.add(noderelation);
                    System.out.println(allrelations.get(i).sourcenode.name + " --> " + allrelations.get(i).targetnode.name);
                    checkfurthernode(onenodeontology, allrelations.get(i).targetnode.name, allrelations);
                }
                if (allrelations.get(i).type.contains("inheritance")) {
                    
                    Relations noderelation = new Relations();
                    noderelation = allrelations.get(i);
                    onenodeontology.add(noderelation);
                    //System.out.println(allrelations.get(i).targetnode.name + " --> " + allrelations.get(i).sourcenode.name);
                    checkfurthernode(onenodeontology, allrelations.get(i).sourcenode.name, allrelations);
                }
            }
        }
    }

}//end of Class OntologyExtraction
