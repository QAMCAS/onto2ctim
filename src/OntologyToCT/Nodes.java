/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyToCT;

import java.util.ArrayList;

/**
 *
 * @author liyih
 */
public class Nodes implements Cloneable {

    public String name;
    public ArrayList<String> allattributes;
    //public ArrayList<Parameters> allparameters;//not used
    //public ArrayList<String>attributevalues;//not used
    public ArrayList<String>parameternames;
    public ArrayList<ArrayList<String>>parametervalues;

    public Nodes() {
        this.allattributes = new ArrayList<String>();
        this.allattributes.clear();
        this.name = "none";
        //this.allparameters = new ArrayList<Parameters>();
        //this.attributevalues=new ArrayList<String>();
        this.parameternames=new ArrayList<String>();
        this.parametervalues=new ArrayList<ArrayList<String>>();
    }

    public String getname() {
        return this.name;
    }

    public ArrayList<String> getattribute() {
        return this.allattributes;
    }

    public ArrayList<String> getparameternames() {
        return this.parameternames;
    }
    
    public ArrayList<ArrayList<String>>getparametervalues(){
        return this.parametervalues;
    }

    @Override
    public Object clone() {
        Nodes nodeclone = null;
        try {
            nodeclone = (Nodes) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        nodeclone.allattributes = (ArrayList<String>) allattributes.clone();
        nodeclone.parameternames=(ArrayList<String>)parameternames.clone();
        nodeclone.parametervalues=(ArrayList<ArrayList<String>>)parametervalues.clone();
        return nodeclone;
    }
}
