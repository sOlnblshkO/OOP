/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kursach;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;

/**
 *
 * @author koyash
 */
public class NewJFrame extends javax.swing.JFrame {

    DefaultTableModel infoModeTable;
    DefaultTableModel dateOperInfoModeTable;
    Timer timerForOperLabel;
   
    public NewJFrame() {
        initComponents();
        operLabel.setText("");
        operPanel.setVisible(false);
        infoModeTable = (DefaultTableModel) infoTable.getModel();
        dateOperInfoModeTable = (DefaultTableModel) dateOperInfoTable.getModel();
        timerForOperLabel = new Timer(3000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                operLabel.setText("");
            }
        });
        
    }

     private void infoTableMouseClicked(java.awt.event.MouseEvent evt) {                                       
        // TODO add your handling code here:
        for (int i = 0; i < infoTable.getRowCount(); i++){
            if (infoTable.isRowSelected(i)){
                operPanel.setVisible(true);
                String infoSurname = String.valueOf(infoModeTable.getValueAt(infoTable.getSelectedRow(), 0));
                String[][] info = Kursach.koyash1.getInfo(Kursach.koyash1.getKlient(infoSurname));
                while (dateOperInfoModeTable.getRowCount() > 0){
                    dateOperInfoModeTable.removeRow(0);
                }
                for (int j = 0; j < info.length;j++){
                    dateOperInfoModeTable.addRow(new Object[]{info[j][0], info[j][1]});
                }
            }
        }
    }                                      

    private void deleteClientButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        if (Kursach.koyash1 != null){
            if (Kursach.koyash1.deleteFirstClient()){            
                infoModeTable.removeRow(0);
                operPanel.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(null, "В системе никого нет!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Система не создана!");
        }
    }                                                  

    private void changeSurnameButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String s = newClientTextField.getText();
        if (s.length() > 1){
                String newSurname = String.valueOf(s);
                String oldSurname = String.valueOf(infoModeTable.getValueAt(infoTable.getSelectedRow(), 0));
                if (Kursach.koyash1.checkForSur(newSurname)){
                    Kursach.koyash1.getKlient(oldSurname).setNewSurname(newSurname);
                    infoModeTable.setValueAt(newSurname, infoTable.getSelectedRow(), 0);
                    operLabel.setText("Изменено");
                    timerForOperLabel.stop();
                    timerForOperLabel.start();
                } else {
                    JOptionPane.showMessageDialog(null, "Такая фамилия уже существует, пожалуйста немного её измените!");
                }
        } else {
            JOptionPane.showMessageDialog(null, "Фамилия должна иметь больше букв");
        }
    }                                                   

    private void addOperButtonActionPerformed(java.awt.event.ActionEvent evt) {                                              
        String selectedClient;
        String newDate;
        int newOp;
        try {
            selectedClient = String.valueOf(infoModeTable.getValueAt(infoTable.getSelectedRow(), 0));
            newDate = new SimpleDateFormat("dd/MM/yyyy, HH:mm").format(dateOperSpinner.getValue());
            newOp = (int)(operSumSpinner.getValue());
            if (newOp != 0){
                if (Kursach.koyash1.getKlient(selectedClient).setHistory(newDate, newOp)) {
                    infoModeTable.setValueAt(Kursach.koyash1.getKlient(selectedClient).getBudget(), infoTable.getSelectedRow(), 1);
                    infoModeTable.setValueAt(Kursach.koyash1.getKlient(selectedClient).getCountOp(), infoTable.getSelectedRow(), 2);
                    operLabel.setText("Добавлено");
                    timerForOperLabel.stop();
                    timerForOperLabel.start();
                } else {
                    JOptionPane.showMessageDialog(null, "Недостаточно средств");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Сумма не должна быть равна 0");
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "Нужно ввести дату в поле даты и сумму в поле суммы");
        }
        String infoSurname = String.valueOf(infoModeTable.getValueAt(infoTable.getSelectedRow(), 0));
        String[][] info = Kursach.koyash1.getInfo(Kursach.koyash1.getKlient(infoSurname));
        while (dateOperInfoModeTable.getRowCount() > 0){
            dateOperInfoModeTable.removeRow(0);
        }
        for (int j = 0; j < info.length;j++){
            dateOperInfoModeTable.addRow(new Object[]{info[j][0], info[j][1]});
        }
    }                                             

    private void deleteOperButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        String delStr = String.valueOf(infoModeTable.getValueAt(infoTable.getSelectedRow(), 0));
        String selectedClient = String.valueOf(infoModeTable.getValueAt(infoTable.getSelectedRow(), 0));
        Kursach.klient tempDel = Kursach.koyash1.getKlient(delStr);
        if (tempDel != null){
            try {
                if (tempDel.deleteHistory(new SimpleDateFormat("dd/MM/yyyy, HH:mm").format(dateOperSpinner.getValue()), (int)operSumSpinner.getValue())){
                    infoModeTable.setValueAt(Kursach.koyash1.getKlient(selectedClient).getBudget(), infoTable.getSelectedRow(), 1);
                    infoModeTable.setValueAt(Kursach.koyash1.getKlient(selectedClient).getCountOp(), infoTable.getSelectedRow(), 2);
                    operLabel.setText("Удалено");
                    timerForOperLabel.stop();
                    timerForOperLabel.start();
                } else {
                    JOptionPane.showMessageDialog(null, "Такой операции не существует!");
                }
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "Нужно ввести дату в поле даты и сумму в поле суммы");
            }
        }
        String infoSurname = String.valueOf(infoModeTable.getValueAt(infoTable.getSelectedRow(), 0));
        String[][] info = Kursach.koyash1.getInfo(Kursach.koyash1.getKlient(infoSurname));
        while (dateOperInfoModeTable.getRowCount() > 0){
            dateOperInfoModeTable.removeRow(0);
        }
        for (int j = 0; j < info.length;j++){
            dateOperInfoModeTable.addRow(new Object[]{info[j][0], info[j][1]});
        }
    }                                                

    private void loadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {                                             
        FileFilter filter = new FileNameExtensionFilter("txt", "txt");
        jFileChooser1.setFileFilter(filter);
        int replyOpen = jFileChooser1.showOpenDialog(null);
        if (replyOpen == jFileChooser1.APPROVE_OPTION){
            try {
                String pathLoad = jFileChooser1.getSelectedFile().getAbsolutePath();
                int reply = JOptionPane.showConfirmDialog(null, "Вы уверены? Все не сохраненные данные будут утеряны.", null, JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION){
                    operPanel.setVisible(false);
                    Kursach.koyash1 = new Kursach.mainBank();
                    Kursach.koyash1.loadFromFile(pathLoad);
                    Kursach.klient[] newBank = Kursach.koyash1.getBank();
                    while (infoTable.getRowCount() > 0){
                        infoModeTable.removeRow(0);
                    }
                    int i = Kursach.koyash1.getFirst();
                    while (i != Kursach.koyash1.getLast()){
                        infoModeTable.addRow(new Object[]{newBank[i].getSurname(), newBank[i].getBudget(), newBank[i].getCountOp()});
                        i++;
                        if (i > Kursach.koyash1.getMaxClients())
                            i = 0;
                    }
                }
            } catch(Exception e){

            }
        }
    }                                            

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {                                             
        if (Kursach.koyash1 != null) {
            FileFilter filter = new FileNameExtensionFilter("txt", "txt");
            jFileChooser1.setFileFilter(filter);
            jFileChooser1.showSaveDialog(null);
            try {
                String pathSave = jFileChooser1.getSelectedFile().getAbsolutePath();
                JOptionPane.showMessageDialog(null, pathSave);
                FileWriter fout = new FileWriter(pathSave);
                Kursach.koyash1.saveToFile(fout);
                fout.close();
            } catch(Exception e){
            
            }
        } else {
            JOptionPane.showMessageDialog(null, "Система не создана");
        }
    }     
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        infoTable = new javax.swing.JTable();
        addNewClientButton = new javax.swing.JButton();
        newClientTextField = new javax.swing.JTextField();
        operPanel = new javax.swing.JPanel();
        addOperButton = new javax.swing.JButton();
        label1 = new java.awt.Label();
        label2 = new java.awt.Label();
        jSeparator1 = new javax.swing.JSeparator();
        deleteOperButton = new javax.swing.JButton();
        operLabel = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        changeSurnameButton = new javax.swing.JButton();
        dateOperSpinner = new javax.swing.JSpinner();
        operSumSpinner = new javax.swing.JSpinner();
        jScrollPane3 = new javax.swing.JScrollPane();
        dateOperInfoTable = new javax.swing.JTable();
        deleteClientButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        loadMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Bank system");
        setAutoRequestFocus(false);
        setPreferredSize(new java.awt.Dimension(850, 480));
        setResizable(false);

        infoTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Фамилия", "Бюджет", "Количество операций"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        infoTable.setPreferredSize(getPreferredSize());
        infoTable.getTableHeader().setReorderingAllowed(false);
        infoTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                infoTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(infoTable);
        if (infoTable.getColumnModel().getColumnCount() > 0) {
            infoTable.getColumnModel().getColumn(1).setResizable(false);
            infoTable.getColumnModel().getColumn(1).setPreferredWidth(30);
            infoTable.getColumnModel().getColumn(2).setResizable(false);
            infoTable.getColumnModel().getColumn(2).setPreferredWidth(30);
        }

        addNewClientButton.setText("Добавить клиента");
        addNewClientButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewClientButtonActionPerformed(evt);
            }
        });

        newClientTextField.setToolTipText("Фамилия");
        newClientTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                newClientTextFieldKeyTyped(evt);
            }
        });

        addOperButton.setText("Добавить операцию");
        addOperButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOperButtonActionPerformed(evt);
            }
        });

        label1.setText("Дата");

        label2.setText("Сумма");

        deleteOperButton.setText("Удалить операцию");
        deleteOperButton.setMaximumSize(new java.awt.Dimension(135, 23));
        deleteOperButton.setMinimumSize(new java.awt.Dimension(135, 23));
        deleteOperButton.setPreferredSize(new java.awt.Dimension(135, 23));
        deleteOperButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteOperButtonActionPerformed(evt);
            }
        });

        operLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        operLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        operLabel.setText("Добавлено");

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        changeSurnameButton.setText("Изменить фамилию");
        changeSurnameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeSurnameButtonActionPerformed(evt);
            }
        });

        dateOperSpinner.setModel(new javax.swing.SpinnerDateModel());

        operSumSpinner.setModel(new javax.swing.SpinnerNumberModel());

        dateOperInfoTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Дата", "Сумма"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(dateOperInfoTable);

        javax.swing.GroupLayout operPanelLayout = new javax.swing.GroupLayout(operPanel);
        operPanel.setLayout(operPanelLayout);
        operPanelLayout.setHorizontalGroup(
            operPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(operPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(operPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, operPanelLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jSeparator2))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(operPanelLayout.createSequentialGroup()
                        .addGroup(operPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(operPanelLayout.createSequentialGroup()
                                .addGroup(operPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(operPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(dateOperSpinner)
                                    .addComponent(operSumSpinner))
                                .addGap(18, 18, 18)
                                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(operLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(operPanelLayout.createSequentialGroup()
                                .addComponent(addOperButton, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteOperButton, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(changeSurnameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 4, Short.MAX_VALUE)))
                .addContainerGap())
        );
        operPanelLayout.setVerticalGroup(
            operPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(operPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(operPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(operLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                    .addComponent(jSeparator3)
                    .addGroup(operPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(operPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dateOperSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(operPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(operSumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(operPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(operPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(addOperButton)
                        .addComponent(deleteOperButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(changeSurnameButton)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        deleteClientButton.setText("Удалить клиента");
        deleteClientButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteClientButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Фамилия");

        jMenu1.setText("Файл");

        loadMenuItem.setText("Загрузить");
        loadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(loadMenuItem);

        saveMenuItem.setText("Сохранить");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(saveMenuItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(3, 3, 3)
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(newClientTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addNewClientButton)
                        .addGap(124, 124, 124)
                        .addComponent(deleteClientButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(operPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(newClientTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addNewClientButton)
                            .addComponent(deleteClientButton))
                        .addGap(0, 13, Short.MAX_VALUE))
                    .addComponent(operPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newClientTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newClientTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (!(Character.isDigit(c) || Character.isAlphabetic(c) 
                || (c == KeyEvent.VK_DELETE)
                || (c == KeyEvent.VK_BACK_SPACE)
                || Character.isWhitespace(c))){
            evt.consume();
            JOptionPane.showMessageDialog(null, "Можно вводить только латинские буквы, кириллицу и цифры");
        } 
    }//GEN-LAST:event_newClientTextFieldKeyTyped
    
    private void addNewClientButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        // TODO add your handling code here:
        String s = newClientTextField.getText();
        if (s.length() > 1){
                if (Kursach.koyash1 == null){
                    Kursach.koyash1 = new Kursach.mainBank();
                    Kursach.koyash1.mainBank(5);
                }
                if (Kursach.koyash1.addClient(s))
                    infoModeTable.addRow(new Object[]{s, 0, 0});
                else 
                    JOptionPane.showMessageDialog(null, "Нет места или клиент с такой фамилей уже существует!");
            } 
        else {
            JOptionPane.showMessageDialog(null, "Фамилия должна иметь больше букв");
        }
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
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addNewClientButton;
    private javax.swing.JButton addOperButton;
    private javax.swing.JButton changeSurnameButton;
    private javax.swing.JTable dateOperInfoTable;
    private javax.swing.JSpinner dateOperSpinner;
    private javax.swing.JButton deleteClientButton;
    private javax.swing.JButton deleteOperButton;
    private javax.swing.JTable infoTable;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private javax.swing.JMenuItem loadMenuItem;
    private javax.swing.JTextField newClientTextField;
    private javax.swing.JLabel operLabel;
    private javax.swing.JPanel operPanel;
    private javax.swing.JSpinner operSumSpinner;
    private javax.swing.JMenuItem saveMenuItem;
    // End of variables declaration//GEN-END:variables
}
