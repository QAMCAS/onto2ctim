/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyToCT;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;
import static OntologyToCT.consExtract.generateConstraints;

/**
 *
 * @author liyih
 */
//==============================================================================
//Attention!!!!
//remember to set the main class to "OntologyExtraction"!!!!!
//==============================================================================
public class NewJFrame extends javax.swing.JFrame {

    ArrayList<String> FinalParameterName = new ArrayList<String>();
    File inputmodelfile;
    ArrayList<String> ParameterType = new ArrayList<String>();
    Nodes theroot;

    public static ArrayList<String> allruleconstraints = new ArrayList<String>();

    public static void passconstraints(ArrayList<String> allconstraints) {
        allruleconstraints = (ArrayList<String>) allconstraints.clone();
        System.out.println("===============The constraints are:=====================");
        for (int i = 0; i < allruleconstraints.size(); i++) {
            System.out.println(allruleconstraints.get(i));
            System.out.println();
        }
    }

    private void autogenerateCons(File file) throws FileNotFoundException, IOException {
        FileReader fr1 = new FileReader(file);
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

        ParameterType = label;

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

        ArrayList<String> allConstraints = new ArrayList<String>();
        allConstraints = generateConstraints(paralist, NumParaPrefix, NumParaName, MaxNumEach, label, NumParaLabel, NumParaMin);
        //======================================================================
        FileWriter fw = null;
        BufferedWriter bw = null;
        fw = new FileWriter(file, true);
        bw = new BufferedWriter(fw);
        for (int pp = 0; pp < allConstraints.size(); pp++) {
            bw.append(allConstraints.get(pp));
            bw.newLine();
            bw.newLine();
        }

        bw.close();
        fw.close();
        //======================================================================
        JOptionPane.showMessageDialog(this, "The CT input model and initial constraints have been generated!");
        jButton3.setEnabled(true);
        jButton5.setEnabled(true);
        inputmodelfile = file;
    }

    /**
     * Creates new form NewJFrame
     */
    public NewJFrame() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        //this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        jButton5.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel1.setText("Ontology-to-CT");

        jButton1.setText("Load Ontology");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Convert Ontology");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("More Constraints");
        jButton3.setActionCommand("");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton5.setText("Load Rule");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(192, 192, 192))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(233, 233, 233)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 2, Short.MAX_VALUE)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 535, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextField2))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    File file;

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        JFileChooser jfc = new JFileChooser();
        //设置当前路径为桌面路径,否则将我的文档作为默认路径
        FileSystemView fsv = FileSystemView.getFileSystemView();
        jfc.setCurrentDirectory(fsv.getHomeDirectory());
        //JFileChooser.FILES_AND_DIRECTORIES 选择路径和文件
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //用户选择的路径或文件
        if (jfc.showOpenDialog(NewJFrame.this) == JFileChooser.APPROVE_OPTION) {
            file = jfc.getSelectedFile();
            jTextField1.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        File graphml = file;
        int strength = 2;
        String algorithm = "YL";

        int num_of_nodes = 0;
        int num_of_relations = 0;
        ArrayList<Nodes> allnodes = new ArrayList<>();
        ArrayList<Relations> allrelations = new ArrayList<>();
        ArrayList<Parameters> allparametersandvalues = new ArrayList<Parameters>();

        try {
            //read ".gramphl" file and get all the information I want
            extractinformation(allnodes, allrelations, num_of_nodes, num_of_relations, graphml, allparametersandvalues);
        } catch (IOException ex) {
            Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

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
        try {
            generateinputmodels(inputmodels, allnodes, graphml, strength, allparametersandvalues);
            //run ACTS
            //runacts(inputmodels, strength);
        } catch (IOException ex) {
            Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        new NewJFrame1(FinalParameterName, inputmodelfile, ParameterType, theroot);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        JFileChooser jfc = new JFileChooser();
        //设置当前路径为桌面路径,否则将我的文档作为默认路径
        FileSystemView fsv = FileSystemView.getFileSystemView();
        jfc.setCurrentDirectory(fsv.getHomeDirectory());
        //JFileChooser.FILES_AND_DIRECTORIES 选择路径和文件
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //用户选择的路径或文件
        if (jfc.showOpenDialog(NewJFrame.this) == JFileChooser.APPROVE_OPTION) {
            file = jfc.getSelectedFile();
            jTextField2.setText(file.getAbsolutePath());
        }
        try {
            //======================================================================
            if (file.exists()) {
                rule.main(file, inputmodelfile);
                FileWriter fw=new FileWriter(inputmodelfile,true);
                BufferedWriter bw=new BufferedWriter(fw);
                if (NewJFrame.allruleconstraints.size() > 0) {
                    for (int pp = 0; pp < NewJFrame.allruleconstraints.size(); pp++) {
                        bw.append(NewJFrame.allruleconstraints.get(pp));
                        bw.newLine();
                        bw.newLine();
                    }
                    System.out.println("All rule-based constraints have been generated and added into the CT inputmodel!");
                    JOptionPane.showMessageDialog(this, "All rule-based constraints have been generated and added into the CT inputmodel!");
                    bw.close();
                    fw.close();
                }
            }
            //======================================================================
        } catch (IOException ex) {
            Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame().setVisible(true);
//                try {
//                    Runtime.getRuntime().exec("java -jar acts_3.1.jar");
//                } catch (IOException ex) {
//                    Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
//                }

            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables

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

    private void generateinputmodels(ArrayList<File> inputmodels, ArrayList<Nodes> allnodes, File graphml, int strength, ArrayList<Parameters> allparametersandvalues) throws IOException, InterruptedException {
        String foldername = graphml.getName();
        //String path = "C:\\Users\\yihaoli\\Documents\\NetBeansProjects";
        Nodes root = allnodes.get(0);
        theroot = allnodes.get(0);
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

            for (int m = 0; m < allparametersandvalues.size(); m++) {
                if (root.parameternames.get(j).contains(allparametersandvalues.get(m).getparametername().trim())) {
                    System.out.println(root.parameternames.get(j) + "%%%%%%%%%%%");
                    FinalParameterName.add(root.parameternames.get(j));
                    bw.write(root.parameternames.get(j) + " " + allparametersandvalues.get(m).parametertype + ": ");
                    break;
                }
            }

            //bw.write(root.parameternames.get(j) + " (enum) : ");
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
        inputmodelfile = file;
        //inputmodels.add(file);//this is for running acts
        autogenerateCons(file);//this is for auto generate constraints
    }

    private static void runacts(ArrayList<File> inputmodels, int strength) throws IOException, InterruptedException {
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
                                if (tempparameter.values.contains("(enum)")) {
                                    tempparameter.parametertype = "(enum)";
                                }
                                if (tempparameter.values.contains("(int)")) {
                                    tempparameter.parametertype = "(int)";
                                }
                                //System.out.println("Parameter " + j + ": " + tempparameter.parametername + "; type and values: " + tempparameter.values+"**************************");

                            } else {
                                String temp9[] = lines.get(i + j).split(";");
                                tempparameter.parametername = temp9[0].replaceAll("Parameter:", "").trim();
                                tempparameter.values = temp9[1].replaceAll("<y:LabelModel>", "").trim();
                                if (tempparameter.values.contains("(enum)")) {
                                    tempparameter.parametertype = "(enum)";
                                }
                                if (tempparameter.values.contains("(int)")) {
                                    tempparameter.parametertype = "(int)";
                                }
                                //System.out.println("Parameter " + j + ": " + tempparameter.parametername + "; type and values: " + tempparameter.values+"*********************************");

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
                                if (tempparameter.values.contains("(enum)")) {
                                    tempparameter.parametertype = "(enum)";
                                }
                                if (tempparameter.values.contains("(int)")) {
                                    tempparameter.parametertype = "(int)";
                                }
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
                                if (tempparameter.values.contains("(enum)")) {
                                    tempparameter.parametertype = "(enum)";
                                }
                                if (tempparameter.values.contains("(int)")) {
                                    tempparameter.parametertype = "(int)";
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

    //==========================================================================
    //for auto generation
}
