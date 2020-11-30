/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyToCT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import OntologyToCT.NewJFrame;
import java.awt.GridLayout;

/**
 *
 * @author liyih
 */
public class NewJFrame1 extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame1
     */
    ListSelectionModel lsm;
    ListSelectionModel lsm2;
    ListSelectionModel lsm3;
    ListSelectionModel lsm4;
    ListSelectionModel lsm5;
    File file;//refer to input model file
    ArrayList<DefaultListModel> dml = new ArrayList<DefaultListModel>();
    Nodes theroot;

    public NewJFrame1(ArrayList<String> parameters, File inputmodelfile, ArrayList<String> parametertype, Nodes root) {
        theroot = root;
        initComponents();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        //======================================================================
        file = inputmodelfile;

        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < parameters.size(); i++) {
            model.addElement(parameters.get(i));
        }
        this.jList1.setModel(model);
        this.jList1.setVisible(true);
        lsm = this.jList1.getSelectionModel();
        lsm.setSelectionMode(0);
        //======================================================================
        DefaultListModel model3 = new DefaultListModel();
        for (int i = 0; i < parameters.size(); i++) {
            model3.addElement("(" + parametertype.get(i) + ") " + parameters.get(i));
        }
        this.jList5.setModel(model3);
        this.jList5.setVisible(true);
        //======================================================================
        //create a model to store the values of each parameter
        for (int i = 0; i < theroot.parametervalues.size(); i++) {
            DefaultListModel tempmodel = new DefaultListModel();
            ArrayList<String> shit = new ArrayList<String>();
            if (parametertype.get(i).contains("int")) {
                for (int j = 0; j < theroot.parametervalues.get(i).size(); j++) {
                    tempmodel.addElement(theroot.parametervalues.get(i).get(j));
                    shit.add(theroot.parametervalues.get(i).get(j));
                }
            } else if (parametertype.get(i).contains("enum")) {
                for (int j = 0; j < theroot.parametervalues.get(i).size(); j++) {
                    tempmodel.addElement("\"" + theroot.parametervalues.get(i).get(j) + "\"");
                    shit.add("\"" + theroot.parametervalues.get(i).get(j) + "\"");
                }
            }
            //dml.add((DefaultListModel)shit.clone());
            dml.add(tempmodel);
        }
        //======================================================================
        ListSelectionListener listSelectionListener5 = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
//                System.out.println("First index: " + listSelectionEvent.getFirstIndex());
//                System.out.println(", Last index: " + listSelectionEvent.getLastIndex());
                boolean adjust = listSelectionEvent.getValueIsAdjusting();
                //System.out.println(", Adjusting? " + adjust);
                if (!adjust) {
                    jList5 = (JList<String>) listSelectionEvent.getSource();
                    int selections[] = jList5.getSelectedIndices();
                    Object selectionValues[] = jList5.getSelectedValues();
                    for (int i = 0, n = selections.length; i < n; i++) {
                        if (i == 0) {
                            //System.out.print(" Selections: ");
                        }
                        //System.out.println(selections[i] + "/" + selectionValues[i] + " ");
                        //System.out.print(selectionValues[i] + " ");
                        //jTextArea1.append(selectionValues[i].toString()+" ");
                        //jTextArea1.insert((selectionValues[i] + " ").toString(), jTextArea1.getCaretPosition());
                        for (int j = 0; j < parameters.size(); j++) {
                            if (selectionValues[i].toString().contains(parameters.get(j))) {
                                jList6.setModel(dml.get(j));
                            }
                        }
                    }
                }
            }
        };
        jList5.addListSelectionListener(listSelectionListener5);
        //======================================================================
        ListSelectionListener listSelectionListener6 = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
//                System.out.println("First index: " + listSelectionEvent.getFirstIndex());
//                System.out.println(", Last index: " + listSelectionEvent.getLastIndex());
                boolean adjust = listSelectionEvent.getValueIsAdjusting();
                //System.out.println(", Adjusting? " + adjust);
                if (!adjust) {
                    jList6 = (JList<String>) listSelectionEvent.getSource();
                    int selections[] = jList6.getSelectedIndices();
                    Object selectionValues[] = jList6.getSelectedValues();
                    for (int i = 0, n = selections.length; i < n; i++) {
                        if (i == 0) {
                            //System.out.print(" Selections: ");
                        }
                        //System.out.println(selections[i] + "/" + selectionValues[i] + " ");
                        //System.out.print(selectionValues[i] + " ");
                        //jTextArea1.append(selectionValues[i].toString()+" ");
                        jTextArea1.insert((selectionValues[i] + " ").toString(), jTextArea1.getCaretPosition());
                    }
                }
            }
        };
        jList6.addListSelectionListener(listSelectionListener6);

        //======================================================================
        DefaultListModel model2 = new DefaultListModel();
        model2.addElement("(");
        model2.addElement(")");
        model2.addElement("==");
        model2.addElement("!=");
        model2.addElement(">");
        model2.addElement("<");
        model2.addElement(">=");
        model2.addElement("<=");
        model2.addElement("&&");
        model2.addElement("||");
        model2.addElement("=>");
        model2.addElement("!");
        model2.addElement("*");
        model2.addElement("/");
        model2.addElement("-");
        model2.addElement("%");
        model2.addElement("+");
        this.jList2.setModel(model2);
        this.jList2.setVisible(true);
        lsm2 = this.jList2.getSelectionModel();
        lsm2.setSelectionMode(0);
        //======================================================================
        ListSelectionListener listSelectionListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
//                System.out.println("First index: " + listSelectionEvent.getFirstIndex());
//                System.out.println(", Last index: " + listSelectionEvent.getLastIndex());
                boolean adjust = listSelectionEvent.getValueIsAdjusting();
                //System.out.println(", Adjusting? " + adjust);
                if (!adjust) {
                    jList1 = (JList<String>) listSelectionEvent.getSource();
                    int selections[] = jList1.getSelectedIndices();
                    Object selectionValues[] = jList1.getSelectedValues();
                    for (int i = 0, n = selections.length; i < n; i++) {
                        if (i == 0) {
                            //System.out.print(" Selections: ");
                        }
                        //System.out.println(selections[i] + "/" + selectionValues[i] + " ");
                        System.out.print(selectionValues[i] + " ");
                        //jTextArea1.append(selectionValues[i].toString()+" ");
                        jTextArea1.insert((selectionValues[i] + " ").toString(), jTextArea1.getCaretPosition());
                    }
                }
            }
        };
        jList1.addListSelectionListener(listSelectionListener);

        ListSelectionListener listSelectionListener2 = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
//                System.out.println("First index: " + listSelectionEvent.getFirstIndex());
//                System.out.println(", Last index: " + listSelectionEvent.getLastIndex());
                boolean adjust = listSelectionEvent.getValueIsAdjusting();
                //System.out.println(", Adjusting? " + adjust);
                if (!adjust) {
                    jList2 = (JList<String>) listSelectionEvent.getSource();
                    int selections[] = jList2.getSelectedIndices();
                    Object selectionValues[] = jList2.getSelectedValues();
                    for (int i = 0, n = selections.length; i < n; i++) {
                        if (i == 0) {
                            //System.out.print(" Selections: ");
                        }
                        //System.out.println(selections[i] + "/" + selectionValues[i] + " ");
                        System.out.print(selectionValues[i] + " ");
                        //jTextArea1.append(selectionValues[i].toString()+" ");
                        jTextArea1.insert((selectionValues[i] + " ").toString(), jTextArea1.getCaretPosition());

                    }
                }
            }
        };

        jList2.addListSelectionListener(listSelectionListener2);
        //======================================================================
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList5 = new javax.swing.JList<>();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList6 = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setViewportView(jList1);

        jScrollPane2.setViewportView(jList2);

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        jButton1.setText("Add Constraints");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Clear Text");
        jButton2.setToolTipText("");
        jButton2.setActionCommand("");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jScrollPane6.setViewportView(jList5);

        jScrollPane7.setViewportView(jList6);

        jLabel1.setText("Parameter");

        jLabel2.setText("Operator");

        jLabel3.setText("Get Parameter Value");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(113, 113, 113)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel2)
                        .addComponent(jLabel1)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        jTextArea1.setText("");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            // TODO add your handling code here:
            //System.out.println(jTextArea1.getText()+"***********************");
            FileWriter fw = null;
            BufferedWriter bw = null;
            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.append(jTextArea1.getText());
            bw.newLine();
            bw.newLine();
            bw.close();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(NewJFrame1.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog(this, "New constraints have been added to the input model!");
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public NewJFrame1() {
        initComponents();
    }

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
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewJFrame1 njm = new NewJFrame1();
                njm.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JList<String> jList5;
    private javax.swing.JList<String> jList6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
