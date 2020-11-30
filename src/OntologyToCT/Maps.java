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
public class Maps {

    public String value;
    public int key;
    public String paraname;

    public Maps() {
        this.value = "none";
        this.key = -1;
        this.paraname = "none";
    }

    public String getvalue() {
        return this.value;
    }

    public int getkey() {
        return this.key;
    }

    public String getparaname() {
        return this.paraname;
    }

    @Override
    public Object clone() {
        Maps mapclone = null;
        try {
            mapclone = (Maps) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return mapclone;
    }

}
