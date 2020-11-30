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
public class argrelation {
    public String leftarg;
    public String rightarg;
    public String operator;
    
    public argrelation(){
        leftarg="";
        rightarg="";
        operator="";
    }
    
//     @Override
//    public Object clone() throws CloneNotSupportedException {
//        argrelation argrelationclone = null;
//        try {
//            argrelationclone = (argrelation) super.clone();
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//        argrelationclone.leftarg = this.leftarg;
//        argrelationclone.rightarg = this.rightarg;
//        argrelationclone.operator = this.operator;
//        return argrelationclone;
//    }
}
