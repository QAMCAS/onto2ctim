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
public class Parameters {

    public String nodename;
    public String parametername;
    public String parametertype;
    public ArrayList<String> parametervalues;
    public String values;

    public Parameters() {
        nodename="none";
        parametername = "none";
        parametertype = "none";
        parametervalues = new ArrayList<String>();
        values = "none";
    }

    public String getnodename(){
        return this.nodename;
    }
    
    public String getparametername() {
        return this.parametername;
    }

    public String getparametertype() {
        return this.parametertype;
    }

    public ArrayList<String> getparametervalues() {
        return this.parametervalues;
    }

    public String getvalues() {
        return this.values;
    }

    @Override
    public Object clone() {
        Parameters parametersclone = null;
        try {
            parametersclone = (Parameters) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        parametersclone.parametervalues = (ArrayList<String>) parametervalues.clone();
        return parametersclone;
    }

}
