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
import java.util.ArrayList;

/**
 *
 * @author liyih
 */
public class rule {

    public String rulename;
    public ArrayList<arg> arglist;
    public ArrayList<argrelation> argrelationlist;
    public String connection;
    public String constraint;

    public rule() {
        rulename = "";
        arglist = new ArrayList<arg>();
        argrelationlist = new ArrayList<argrelation>();
        connection = "";
        constraint = "";
    }

    public void printinfo() {
        System.out.println("*************************************************");
        System.out.println("Rule: " + rulename);
        for (int i = 0; i < arglist.size(); i++) {
            System.out.println(arglist.get(i).name + ": " + "\t" + "min: " + arglist.get(i).min + "\t" + "max: " + arglist.get(i).max);
        }
        for (int i = 0; i < argrelationlist.size(); i++) {
            System.out.println("argrelation " + (i + 1) + ": " + argrelationlist.get(i).leftarg + argrelationlist.get(i).operator + argrelationlist.get(i).rightarg);
        }
        System.out.println("connection: " + connection);
    }

//    @Override
//    public Object clone() {
//        rule ruleclone = null;
//        try {
//            ruleclone = (rule) super.clone();
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//        ruleclone.rulename = this.rulename;
//        ruleclone.arglist = (ArrayList<arg>) arglist.clone();
//        ruleclone.argrelationlist = (ArrayList<argrelation>) argrelationlist.clone();
//        ruleclone.connection = this.connection;
//        return ruleclone;
//    }
    public static void main(File f,File inputmodelfile) throws FileNotFoundException, IOException, CloneNotSupportedException {

        //File f = new File("C:\\Users\\liyih\\Google Drive\\junk-share\\junk.txt");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);

        ArrayList<rule> rulelist = new ArrayList<rule>();

        String temp = "";
        while (br.ready()) {
            temp = br.readLine();
            if (temp.contains("[rule]")) {
                rule temprule = new rule();
                //System.out.println("Reaching [rule] section");
                temprule.rulename = br.readLine();
                temp = br.readLine();
                if (temp.contains("[arg]")) {
                    //System.out.println("Reaching [arg] section");
                    temp = br.readLine();
                    while (temp.contains(":") && temp.contains("to")) {
                        //System.out.println("Find new arg!");
                        arg temparg = new arg();
                        temparg.name = temp.split(":")[0].trim();
                        //System.out.println(temparg.name);
                        temparg.min = Integer.parseInt(temp.split(":")[1].trim().split("to")[0].trim());
                        //System.out.println(temparg.min);
                        temparg.max = Integer.parseInt(temp.split(":")[1].trim().split("to")[1].trim());
                        //System.out.println(temparg.max);
                        //temprule.arglist.add((arg) temparg.clone());
                        temprule.arglist.add(temparg);
                        temp = br.readLine();
                    }
                    if (temp.contains("[argrelation]")) {
                        //System.out.println("Reaching [argrelation] section");
                        temp = br.readLine();
                        while (temp.contains("=")) {
                            //System.out.println("Find new argrelation!");
                            argrelation tempargrelation = new argrelation();
                            if (temp.contains("!=")) {
                                tempargrelation.leftarg = temp.split("!=")[0].trim();
                                tempargrelation.rightarg = temp.split("!=")[1].trim();
                                tempargrelation.operator = "!=";
                            } else {
                                tempargrelation.leftarg = temp.split("==")[0].trim();
                                tempargrelation.rightarg = temp.split("==")[1].trim();
                                tempargrelation.operator = "==";
                            }
                            //temprule.argrelationlist.add((argrelation)tempargrelation.clone());
                            temprule.argrelationlist.add(tempargrelation);
                            temp = br.readLine();
                        }
                        if (temp.contains("[connection]")) {
                            //System.out.println("Reaching [connection] section");
                            temp = br.readLine();
                            if (temp.contains("&&")) {
                                temprule.connection = "&&";
                            } else if (temp.contains("||")) {
                                temprule.connection = "||";
                            } else if (temp.contains("single")) {
                                temprule.connection = "single";
                            }
                        }//end of [connection]
                    }//end of [argrelation]
                }//end of [arg] 
                //rulelist.add((rule) temprule.clone());
                rulelist.add(temprule);

            }//end of [rule]    
        }//end of while(br.ready())
        //======================================================================
        for (int i = 0; i < rulelist.size(); i++) {
            rulelist.get(i).printinfo();
        }
        //======================================================================
        br.close();
        fr.close();
        //======================================================================
        //get all the combinations from the args
        for (int i = 0; i < rulelist.size(); i++) {
            decomposerule(rulelist.get(i));
        }
        //======================================================================
        //System.out.println("The constraints are:");
        ArrayList<String>allconstraintsready=new ArrayList<String>();
        for (int i = 0; i < rulelist.size(); i++) {
           //System.out.println(rulelist.get(i).constraint);
           allconstraintsready.add(rulelist.get(i).constraint);
           //System.out.println();
        }
        NewJFrame.passconstraints(allconstraintsready);
        
    }

    public static void decomposerule(rule r) {
        ArrayList<ArrayList<Integer>> allcombinations = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> tempsequence = new ArrayList<Integer>();

        int count = 0;
        ArrayList<Integer> keep = new ArrayList<Integer>();
        keep = (ArrayList<Integer>) tempsequence.clone();
        for (int i = r.arglist.get(0).min; i < r.arglist.get(0).max + 1; i++) {
            count = 0;
            tempsequence.add(i);
            if (count + 1 < r.arglist.size()) {

                godeep(r.arglist.get(count + 1), r, (ArrayList<Integer>) tempsequence.clone(), allcombinations, count + 1);
            } else {

                allcombinations.add((ArrayList<Integer>) tempsequence.clone());
                tempsequence.clear();
            }

            tempsequence = (ArrayList<Integer>) keep.clone();
        }
        //======================================================================
//        System.out.println(allcombinations.size());
//        for (int i = 0; i < allcombinations.size(); i++) {
//            System.out.println("Combo " + (i + 1) + ":");
//            for (int j = 0; j < allcombinations.get(i).size(); j++) {
//                System.out.print(allcombinations.get(i).get(j) + "\t");
//            }
//            System.out.println();
//        }
        //======================================================================
        getvalidcombinations(allcombinations, r);
    }

    public static void getvalidcombinations(ArrayList<ArrayList<Integer>> allcombinations, rule r) {
        ArrayList<ArrayList<Integer>> validcombinations = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < allcombinations.size(); i++) {
            int count = 0;
            for (int j = 0; j < r.argrelationlist.size(); j++) {
                int left = Integer.parseInt(r.argrelationlist.get(j).leftarg.replace("arg", "").trim());
                int right = Integer.parseInt(r.argrelationlist.get(j).rightarg.replace("arg", "").trim());
                if (r.argrelationlist.get(j).operator.contains("==")) {
                    if (allcombinations.get(i).get(left - 1) == allcombinations.get(i).get(right - 1)) {
                        count++;
                    }
                } else {
                    if (allcombinations.get(i).get(left - 1) != allcombinations.get(i).get(right - 1)) {
                        count++;
                    }
                }
            }
            if (count == r.argrelationlist.size()) {
                validcombinations.add((ArrayList<Integer>) allcombinations.get(i).clone());
            }
        }
//        for (int i = 0; i < validcombinations.size(); i++) {
//            System.out.println("Valid combiation " + (i + 1) + ": ");
//            for (int j = 0; j < validcombinations.get(i).size(); j++) {
//                System.out.print(validcombinations.get(i).get(j) + "\t");
//            }
//            System.out.println();
//        }
        //======================================================================
        //generate constrints
        ArrayList<String> args = new ArrayList<String>();
        for (int i = 0; i < r.arglist.size(); i++) {
            args.add("(arg" + (i + 1) + ")");
        }
        ArrayList<String> constraintlist = new ArrayList<String>();
        for (int i = 0; i < validcombinations.size(); i++) {
            String temp = r.rulename;
            for (int j = 0; j < args.size(); j++) {
                temp = temp.replace(args.get(j), validcombinations.get(i).get(j).toString());
            }
            constraintlist.add(temp);
            //System.out.println(temp);
        }
        //======================================================================
        //the final format which will be written into the input model
        //System.out.println("");
        //System.out.println("The following constraints have been generated:");
        String finalconstraint = "";
        for (int i = 0; i < constraintlist.size(); i++) {
            if (r.connection.contains("&&")) {
                finalconstraint = finalconstraint + "(" + constraintlist.get(i) + ")" + " && ";
            } else if (r.connection.contains("||")) {
                finalconstraint = finalconstraint + "(" + constraintlist.get(i) + ")" + " || ";
            } else if (r.connection.contains("single")) {
                finalconstraint = finalconstraint + constraintlist.get(i) + "\n";
            }
        }
        if (!r.connection.contains("single")) {
            //System.out.println(finalconstraint.substring(0,finalconstraint.length()-4));
            r.constraint = finalconstraint.substring(0, finalconstraint.length() - 4);
        } else {
            //System.out.println(finalconstraint);
            r.constraint = finalconstraint;
        }
    }

    public static void godeep(arg nextarg, rule r, ArrayList<Integer> tempsequence, ArrayList<ArrayList<Integer>> allcombinations, int count) {
        ArrayList<Integer> keep = new ArrayList<Integer>();
        keep = (ArrayList<Integer>) tempsequence.clone();
        for (int i = nextarg.min; i < nextarg.max + 1; i++) {
            tempsequence.add(i);
            if (count + 1 < r.arglist.size()) {
                godeep(r.arglist.get(count + 1), r, (ArrayList<Integer>) tempsequence.clone(), allcombinations, count + 1);
            } else {
                allcombinations.add((ArrayList<Integer>) tempsequence.clone());
                tempsequence.clear();
            }
            tempsequence = (ArrayList<Integer>) keep.clone();
        }
    }
}
