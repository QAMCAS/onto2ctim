/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyToCT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.sql.DriverManager.println;
import java.util.ArrayList;

/**
 *
 * @author liyih
 */
public class consExtract {

    public static void main(String[] args, File f) throws FileNotFoundException, IOException {
        // TODO code application logic here
        File inputmodel = new File("C:\\Users\\liyih\\Desktop\\dynamicpart.graphml_results\\DynamicPart_2.txt");
        //File inputmodel = new File("C:\\Users\\liyih\\Desktop\\new.txt");
        //FileReader fr1 = new FileReader(parafile);
        //FileReader fr1 = new FileReader(inputmodel);
        FileReader fr1 = new FileReader(f);
        BufferedReader bf1 = new BufferedReader(fr1);
        //System.out.println(bf1.readLine());
        ArrayList<String> label = new ArrayList<String>();
        ArrayList<String> paralist = new ArrayList<String>();
        ArrayList<String> NumParaLabel = new ArrayList<String>();
        ArrayList<String> NumParaMin = new ArrayList<String>();
        String junk = "";

        while (bf1.ready()) {
            junk = bf1.readLine();
            if (junk.contains("(enum)") || junk.contains("(int)")) {
                //System.out.println(junk);

                if (junk.contains("(enum)")) {
                    label.add("enum");
                    paralist.add(junk.split(" ")[0].trim());
                } else {
                    label.add("int");
                    paralist.add(junk.split(" ")[0].trim());
                }

                if (junk.contains("NumberOf")) {
                    System.out.println(junk);
                    //int size = junk.split(":").length;
                    junk = junk.split(":")[1].toString();
                    //System.out.println(junk);
                    //String min = junk.split(",")[0].trim();
                    NumParaMin.add(junk.split(",")[0].trim());
                    //System.out.println(min);
                }

            }

        }
        System.out.println("Number of parameters: " + paralist.size());
        System.out.println("Number of labels: " + label.size());
        System.out.println("NumParaMin size: " + NumParaMin.size());

//        for(int i=0;i<paralist.size();i++){
//            System.out.println(paralist.get(i)+"\t"+label.get(i));
//        }
        bf1.close();
        fr1.close();
        //System.out.println();
        //=====================================================================
        //find parameters that look like NumOfXXX
        ArrayList<String> NumParaPrefix = new ArrayList<String>();
        ArrayList<String> NumParaName = new ArrayList<String>();
        ArrayList<Integer> MaxNumEach = new ArrayList<Integer>();
        for (int i = 0; i < paralist.size(); i++) {
            if (paralist.get(i).contains("NumberOf")) {
                //System.out.println("NumParaPrefix: "+paralist.get(i).split("NumberOf")[0]+paralist.get(i).split("NumberOf")[1]);
                NumParaPrefix.add((paralist.get(i).split("NumberOf")[0].trim() + paralist.get(i).split("NumberOf")[1]).trim());
                //System.out.println("NumParaName: "+paralist.get(i));
                NumParaName.add((paralist.get(i)).trim());
                NumParaLabel.add(label.get(i));
            }
        }
        System.out.println("NumParaPrefix size: " + NumParaPrefix.size());
        System.out.println("NumParaName size: " + NumParaName.size());
        System.out.println("NumParaLabel size: " + NumParaLabel.size());

        for (int i = 0; i < NumParaPrefix.size(); i++) {
            int t = 0;
            for (int j = 0; j < paralist.size(); j++) {
                if (paralist.get(j).contains(NumParaPrefix.get(i)) && (!paralist.get(j).contains("NumberOf"))) {
                    //System.out.println(paralist.get(i)+"88888888888");
                    String temp = paralist.get(j).split(NumParaPrefix.get(i))[1];
                    //System.out.println(temp);
                    String temp2 = paralist.get(j).split(NumParaPrefix.get(i))[1].split("_")[0];
                    //System.out.println(temp2);
                    t = Integer.parseInt(temp2);
                    //System.out.println(t);
                }
            }
            MaxNumEach.add(t);
        }
        System.out.println("MaxNumEach: " + MaxNumEach.size());
        System.out.println("**********************Find the following " + NumParaPrefix.size() + " parameters that can auto generate constraints:**********************");
        for (int i = 0; i < MaxNumEach.size(); i++) {
            System.out.println("NumParaPrefix " + (i + 1) + "==>" + NumParaPrefix.get(i) + " : " + MaxNumEach.get(i));
        }
        //start the shit!!!
        System.out.println();
        //01-auto constraints generation
        ArrayList<String>allConstraints=new ArrayList<String>();
        allConstraints=generateConstraints(paralist, NumParaPrefix, NumParaName, MaxNumEach, label, NumParaLabel, NumParaMin);

    }

    public static ArrayList<String> generateConstraints(ArrayList<String> paralist, ArrayList<String> NumParaPrefix, ArrayList<String> NumParaName, ArrayList<Integer> MaxNumEach, ArrayList<String> label, ArrayList<String> NumParaLabel, ArrayList<String> NumParaMin) {
        //
        ArrayList<String> allConstraints = new ArrayList<String>();
        for (int i = 0; i < NumParaPrefix.size(); i++) {//start from each NumOfPara k1,k2,k3

            //==================================================================
            int row = 1;
//            for (int p = 0; p < MaxNumEach.get(i); p++) {
//                row = 2 * row;
//            }
            row = (int) Math.pow(2, MaxNumEach.get(i));
            int col = MaxNumEach.get(i);
            int[][] matrix = new int[row][col];
            //System.out.println("row: " + row + " ; " + "col: " + col);
            //==================================================================
            //build three arraylists based on col or n
            ArrayList<ArrayList<Integer>> comb = new ArrayList<ArrayList<Integer>>();
            for (int j = 0; j < col; j++) {
                ArrayList<Integer> temparray = new ArrayList<Integer>();
                for (int k = 1; k < Math.pow(2, j + 1) + 1; k++) {
                    if (k % 2 == 0) {
                        temparray.add(1);
                    } else {
                        temparray.add(0);
                    }
                }
                comb.add((ArrayList<Integer>) temparray.clone());
                //System.out.println("temp size: " + temparray.size());
            }
            //System.out.println("comb size: " + comb.size());
            //==================================================================
            //for int[][] matrix = new int[row][col];
            ArrayList<String> matrixstring = new ArrayList<String>();
            int count = 0;
            for (int l = 0; l < comb.get(count).size(); l++) {
                //System.out.print(comb.get(count).get(l)+"\t");
                String line = "";
                line = comb.get(count).get(l).toString() + "\t";
                if (count + 1 < comb.size()) {
                    //count=count+1;
                    printnext(comb, count + 1, line, matrixstring);
                } else {
                    //System.out.println(line);
                    matrixstring.add(line);
                    //System.out.println("length: "+line.split("\t").length);
                }
            }
            //==================================================================
            //System.out.println("matrixstring size: " + matrixstring.size());
            for (int m = 0; m < matrixstring.size(); m++) {
                for (int n = 0; n < matrixstring.get(m).split("\t").length; n++) {
                    matrix[m][n] = Integer.parseInt(matrixstring.get(m).split("\t")[n].trim());
                    //System.out.print(matrix[m][n] + "\t");
                }
                //System.out.println();
            }

            //==================================================================
            //constraints matching
            //NumParaPrefix,NumParaName,paralist
            //System.out.println("**********************" + (i + 1) + " Start generating constraints for: " + NumParaName.get(i) + "**********************");
            ArrayList<String> RightSingleCompleteCons = new ArrayList<String>();
            for (int m = 0; m < matrix.length; m++) {
                //System.out.println("Case " + (m + 1) + ":");
                String tempstring = "";
                ArrayList<String> cons = new ArrayList<String>();
                for (int n = 0; n < matrix[m].length; n++) {
                    if (matrix[m][n] == 0) {
                        tempstring = tempstring + NumParaPrefix.get(i) + (n + 1) + "==" + "\"" + "null" + "\"" + "\t";
                        cons.add(NumParaPrefix.get(i) + (n + 1) + "==" + "\"" + "null" + "\"");
                    } else {
                        cons.add(NumParaPrefix.get(i) + (n + 1) + "!=" + "\"" + "null" + "\"");
                        tempstring = tempstring + NumParaPrefix.get(i) + (n + 1) + "!=" + "\"" + "null" + "\"" + "\t";
                    }
                }

                String right = "";

                for (int r = 0; r < cons.size(); r++) {
                    //System.out.println("Part "+(r+1)+": "+cons.get(r) + "\t");
                    //left=left+cons.get(r)+" && ";

                    if (cons.get(r).contains("==")) {
                        for (int s = 0; s < paralist.size(); s++) {
                            String tmp = cons.get(r).split("==")[0].trim();
                            if (paralist.get(s).contains(tmp)) {
                                //System.out.println(paralist.get(s) + "==" + "\"" + "null" + "\"");
                                if (label.get(s).contains("enum")) {
                                    right = right + paralist.get(s) + "==" + "\"" + "null" + "\"" + " && ";
                                } else {
                                    right = right + paralist.get(s) + "==" + 0 + " && ";
                                }
                            }
                        }
                    } else {//contains "!="
                        for (int s = 0; s < paralist.size(); s++) {
                            String tmp = cons.get(r).split("!=")[0].trim();
                            if (paralist.get(s).contains(tmp)) {
                                //System.out.println(paralist.get(s) + "!=" + "\"" + "null" + "\"");
                                if (label.get(s).contains("enum")) {
                                    right = right + paralist.get(s) + "!=" + "\"" + "null" + "\"" + " && ";
                                } else {
                                    right = right + paralist.get(s) + "!=" + 0 + " && ";
                                }
                            }
                        }
                    }
                }
                right = "(" + right.substring(0, right.length() - 4) + ")";
                RightSingleCompleteCons.add(right);
                //System.out.println(right);

                //System.out.print(tempstring);
                //System.out.println();
            }
            //==================================================================
            //output constriants, use MaxNumEach now
            //System.out.println("total right size: "+RightSingleCompleteCons.size());
            String left = "";
            int[] summary = new int[matrix.length];
            for (int w = 0; w < matrix.length; w++) {
                int tempsum = 0;
                for (int x = 0; x < matrix[w].length; x++) {
                    //System.out.print(matrix[w][x]+"\t");
                    tempsum = tempsum + matrix[w][x];
                }
                summary[w] = tempsum;
                //System.out.println("tempsum: "+tempsum);
            }
            //ArrayList<String>Constraints=new ArrayList<String>();
            for (int u = Integer.parseInt(NumParaMin.get(i)); u <= MaxNumEach.get(i); u++) {
                if (NumParaLabel.get(i).contains("enum")) {
                    left = "(" + NumParaName.get(i).toString() + "==" + "\"" + u + "\"" + ")";
                } else {
                    left = "(" + NumParaName.get(i).toString() + "==" + u + ")";
                }
                //left="("+NumParaName.get(i).toString()+"=="+u+")";
                String completeright = "";
                //System.out.println(left);
                for (int v = 0; v < RightSingleCompleteCons.size(); v++) {
                    if (u == summary[v]) {
                        //System.out.println("find equivalent condition where the number is: "+u);
                        completeright = completeright + RightSingleCompleteCons.get(v) + " || ";
                    }
                }
                completeright = "(" + completeright.substring(0, completeright.length() - 4) + ")";
                //System.out.println("complete right is: @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                //System.out.println(completeright);
                //System.out.println("complete constraint is: &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                String completeconstraint = "";
                completeconstraint = left + "=>" + completeright;
                allConstraints.add(completeconstraint);
                System.out.println(completeconstraint);
                System.out.println();
                //return allConstraints;
            }

            //System.out.println("==============================================");
        }
        return allConstraints;
    }

    private static void printnext(ArrayList<ArrayList<Integer>> comb, int count, String line, ArrayList<String> matrixstring) {
        for (int i = 0; i < comb.get(count).size() / Math.pow(2, count); i++) {//THIS IS THE KEY!!!!!!!!!!!!!!!!!!!!!!!!
            String newline = "";
            newline = comb.get(count).get(i).toString() + "\t";
            if (count + 1 < comb.size()) {
                printnext(comb, count + 1, line + newline, matrixstring);
            } else {
                //System.out.println(line+newline);
                matrixstring.add(line + newline);
                //System.out.println("length: "+(line+newline).split("\t").length);
            }
        }
    }
}
