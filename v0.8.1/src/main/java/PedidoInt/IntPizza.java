package PedidoInt;

import InicioInt.home;
import DB.Database;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class IntPizza extends javax.swing.JDialog {

    public double precoSabor = 0;
    public double precoTamanho = 0;
    public double precoBebida = 0;
    public double precoFinal;

    public IntPizza(java.awt.Frame parent) {
        super(parent, true);
        JFrame j = new JFrame();
        j.setLocationRelativeTo(null);
        j.setUndecorated(true);
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        listaSabores();
        listaTamanhos();
        listaBebidas();

        // Atalho para fechar
        JRootPane rootPane = this.getRootPane();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_DOWN_MASK), "closeProgram");
        rootPane.getActionMap().put("closeProgram", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha o programa
            }
        });
    }
    
    public void Show(){
        this.setVisible(true);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        comboTamanho = new javax.swing.JComboBox<>();
        comboSabor = new javax.swing.JComboBox<>();
        comboBebida = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        enviar = new javax.swing.JButton();
        clienteNome = new javax.swing.JLabel();
        rua = new javax.swing.JLabel();
        bairro = new javax.swing.JLabel();
        nCasa = new javax.swing.JLabel();
        inputNumeroCasa = new javax.swing.JTextField();
        inputBairro = new javax.swing.JTextField();
        inputRua = new javax.swing.JTextField();
        inputNome = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        comboTamanho.setSelectedItem(null);

        comboSabor.setSelectedItem(null);
        comboSabor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboSaborActionPerformed(evt);
            }
        });

        comboBebida.setSelectedItem(null);

        jLabel1.setText("Tamanho");

        jLabel2.setText("Bebidas");

        jLabel3.setText("Sabor");

        enviar.setBackground(new java.awt.Color(255, 51, 51));
        enviar.setForeground(java.awt.Color.white);
        enviar.setText("Enviar");
        enviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enviarActionPerformed(evt);
            }
        });

        clienteNome.setText("Cliente");

        rua.setText("Rua");

        bairro.setText("Bairro");

        nCasa.setText("Nº");

        inputNumeroCasa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputNumeroCasaActionPerformed(evt);
            }
        });

        inputBairro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputBairroActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel4.setText("Fazer Pedido");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(115, 115, 115)
                                        .addComponent(jLabel3)
                                        .addGap(137, 137, 137)
                                        .addComponent(jLabel2))
                                    .addComponent(clienteNome))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(comboTamanho, 0, 167, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboSabor, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboBebida, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(enviar, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(inputNome)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(inputRua, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(rua)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(inputBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(bairro))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nCasa, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(inputNumeroCasa, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboTamanho, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBebida, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboSabor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addComponent(clienteNome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rua)
                    .addComponent(bairro)
                    .addComponent(nCasa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputNumeroCasa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputRua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 121, Short.MAX_VALUE)
                .addComponent(enviar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void enviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enviarActionPerformed
        print();
    }//GEN-LAST:event_enviarActionPerformed

    private void comboSaborActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboSaborActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboSaborActionPerformed

    private void inputBairroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputBairroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputBairroActionPerformed

    private void inputNumeroCasaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputNumeroCasaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputNumeroCasaActionPerformed

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
            java.util.logging.Logger.getLogger(IntPizza.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IntPizza.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IntPizza.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IntPizza.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IntPizza(new javax.swing.JFrame()).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bairro;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel clienteNome;
    private javax.swing.JComboBox<String> comboBebida;
    private javax.swing.JComboBox<String> comboSabor;
    private javax.swing.JComboBox<String> comboTamanho;
    private javax.swing.JButton enviar;
    private javax.swing.JTextField inputBairro;
    private javax.swing.JTextField inputNome;
    private javax.swing.JTextField inputNumeroCasa;
    private javax.swing.JTextField inputRua;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel nCasa;
    private javax.swing.JLabel rua;
    // End of variables declaration//GEN-END:variables

    public void print() {
        String tamanho = comboTamanho.getSelectedItem().toString();
        String sabor = comboSabor.getSelectedItem().toString();
        String bebida = comboBebida.getSelectedItem().toString();

        //    NOMEC PARA NOME_CLIENTE
        String nomeC = inputNome.getText();
        String nome_Cliente = nomeC;

        //    RUA PARA ENDERÇO
        String rua = inputRua.getText();
        String endereco = rua;

        String bairro = inputBairro.getText();
        int nCasa = Integer.parseInt(inputNumeroCasa.getText());
        String hora = pegaHora();
        precoFinal = precoTamanho + precoSabor + precoBebida;

        Integer print = JOptionPane.showConfirmDialog(rootPane,
                "-------------------------------\n"
                + "Cliente: " + nomeC + "\n"
                + "Rua: " + rua + "\n"
                + "Bairro: " + bairro + "\n"
                + "Número Casa: " + nCasa + "\n"
                + "Hora: " + hora+ "\n"
                + "-------------------------------\n"
                + "PEDIDOS\n"
                + "Sabor: " + sabor + "\n"
                + "Tamanho: " + tamanho + "\n"
                + "Bebida: " + bebida + "\n"
                + "-------------------------------\n"
                + "PREÇO\n"
                + "Total: " + precoFinal + "\n"
                + "-------------------------------\n"
        );

        if (print == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(rootPane, "Fasido");
            enviarPedido();
            dispose();
        } else if (print == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(rootPane, "Beta");
        } else {
            JOptionPane.showMessageDialog(rootPane, "Beta");
        }
    }

    public void listaSabores() {
        Connection conn = Database.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id_sabor, sabor, precoSabor FROM sabor";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboSabor.getModel();

            model.removeAllElements();

            while (rs.next()) {
                String sabor = rs.getString("sabor");
                precoSabor = rs.getDouble("precoSabor");
                model.addElement(sabor);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void listaTamanhos() {
        Connection conn = Database.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT id_tamanho, tamanho, precoTamanho FROM tamanho";  // Agora, vamos pegar apenas o tamanho
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            // Obtém o modelo do combo box
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboTamanho.getModel();

            // Remove todos os itens atuais do combo box
            model.removeAllElements();

            // Adiciona os itens ao combo box
            while (rs.next()) {
                String tamanho = rs.getString("tamanho");
                precoTamanho = rs.getDouble("precoTamanho");
                model.addElement(tamanho);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void listaBebidas() {
        Connection conn = Database.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT id_bebida, bebida, precoBebida FROM bebida";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            // Obtém o modelo do combo box
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBebida.getModel();
            // Remove todos os itens atuais do combo box
            model.removeAllElements();

            // Adiciona os itens ao combo box
            while (rs.next()) {
                String bebida = rs.getString("bebida");
                precoBebida = rs.getDouble("precoBebida");
                model.addElement(bebida);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void enviarPedido() {
        Connection conn = Database.getConnection();

        String tamanho = comboTamanho.getSelectedItem().toString();
        String sabor = comboSabor.getSelectedItem().toString();
        String bebida = comboBebida.getSelectedItem().toString();

        String nome_Cliente = inputNome.getText();
        String endereco = inputRua.getText();

        String bairro = inputBairro.getText();
        int nCasa = Integer.parseInt(inputNumeroCasa.getText());
        String horaP = pegaHora();
        double precoFinal = precoTamanho + precoSabor + precoBebida;

        try {
            // Query de inserção sem o `id_pedido` (auto-incremento)
            String sql = "INSERT INTO `pedido`(`sabor`, `tamanho`, `bebida`, `nomeCliente`, `rua`, `bairro`, `numero`, `hora`, `precoFinal`) VALUES (?,?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, sabor);
            stmt.setString(2, tamanho);
            stmt.setString(3, bebida);
            stmt.setString(4, nome_Cliente);
            stmt.setString(5, endereco);
            stmt.setString(6, bairro);
            stmt.setInt(7, nCasa);
            stmt.setString(8, horaP);
            stmt.setDouble(9, precoFinal);

            stmt.executeUpdate();

            // Recupera o id gerado
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                long idPedido = generatedKeys.getLong(1);  // Aqui você tem o id_pedido gerado
                System.out.println("Pedido enviado com sucesso. ID do Pedido: " + idPedido);
            }

            // Atualiza a tabela com os pedidos
            home h = new home();
            h.listaPedidos();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao enviar o pedido: " + ex.getMessage());
        }
    }

    private String pegaHora() {
        LocalTime horaAtual = LocalTime.now();  // Obtém a hora atual
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");  // Define o formato da hora
        return horaAtual.format(formatter);  // Retorna a hora formatada
    }
}
