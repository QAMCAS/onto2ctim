/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyToCT;

/**
 *
 * @author liyih
 */
public class arg {

    public String name;
    public int min;
    public int max;
    
    public arg(){
        name="";
        min=1;
        max=1;
    }

//    @Override
//    public Object clone() throws CloneNotSupportedException {
//        arg argclone = null;
//        try {
//            argclone = (arg) super.clone();
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//        argclone.name = name;
//        argclone.min = min;
//        argclone.max = max;
//        return argclone;
//    }

}

