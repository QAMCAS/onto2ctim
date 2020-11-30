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
public class Relations implements Cloneable {

    public String name;
    public Nodes sourcenode;
    public Nodes targetnode;
    public String type;
    public int maxarityfortargetnode;
    public int minarityfortargetnode;

    public Relations() {
        this.name = "none";
//        this.sourcenode = "none";
//        this.targetnode = "none";
        this.sourcenode = new Nodes();
        this.targetnode = new Nodes();
        this.type = "none";
        this.maxarityfortargetnode = -1;
        this.minarityfortargetnode = -1;
    }

    public String getname() {
        return this.name;
    }

    public Nodes getsourcenode() {
        return this.sourcenode;
    }

    public Nodes gettargetnode() {
        return this.targetnode;
    }

    public String gettype() {
        return this.type;
    }

    public int getmaxarity() {
        return this.maxarityfortargetnode;
    }

    public int getminarity() {
        return this.minarityfortargetnode;
    }

    @Override
    public Object clone() {
        Relations relationsclone = null;
        try {
            relationsclone = (Relations) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        relationsclone.sourcenode = (Nodes) sourcenode.clone();
        relationsclone.targetnode = (Nodes) targetnode.clone();
        return relationsclone;
    }

}
