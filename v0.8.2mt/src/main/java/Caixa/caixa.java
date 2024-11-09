package Caixa;

import DB.Database;
import Funcionarios.Funcionarios;
import Funcionarios.addFuncionarios;
import Inicio.home;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.event.*;
import java.sql.*;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

public class Caixa extends javax.swing.JFrame {

    private DefaultTableModel tabelaCaixas = new DefaultTableModel(new Object[]{"Caixa", "Funcionário", "Aberto"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private static int id_caixa;
    public static int getIDCaixa() {
        return id_caixa;
    }

    public Caixa() {
        initComponents();
        setLocationRelativeTo(null);

        // Verifica o estado da conexão ao iniciar
        Connection conn = Database.getConnection();
        boolean isConnected = (conn != null);
        verificaConexao(isConnected);

        if (conn != null) {
            try {
                conn.close(); // Fecha a conexão após o teste
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        listaCaixas();
        configurarAtalho();
        addEventListeners();
    }

    public void verificaConexao(boolean isConnected) {
        Color white = new Color(255, 255, 255);
        Color lightBlue = new Color(0, 153, 255);
        Color red = new Color(255, 0, 0);

        if (isConnected) {
            connPanel.setBackground(lightBlue);
            msgPanel.setText("A conexão com o banco está funcionando corretamente! Ao trabalho.");
            msgPanel.setForeground(white);
        } else {
            connPanel.setBackground(red);
            msgPanel.setText("Você não está conectado ao servidor! Os recursos do sistema não funcionarão corretamente.");
            msgPanel.setForeground(white);
        }
    }

    public void listaCaixas() {
        Connection conn = Database.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id_caixa, id_funcionario, caixa, nome_funcionario, aberto FROM caixa";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            // Limpa a tabela antes de adicionar novos dados
            tabelaCaixas.setRowCount(0);

            while (rs.next()) {
                String aberto = rs.getBoolean("aberto") ? "Sim" : "Não";

                Object[] row = {
                    rs.getInt("caixa"),
                    rs.getString("nome_funcionario"),
                    aberto
                };
                tabelaCaixas.addRow(row);
            }

        } catch (SQLException e) {
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void addEventListeners() {
        // Ouvinte para clique duplo na tabela
        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirDetalhesCaixa();
                }
            }
        });

        // Ouvinte para tecla Enter
        jTable1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    abrirDetalhesCaixa();
                }
            }
        });
    }

    private void abrirDetalhesCaixa() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            id_caixa = (int) tabelaCaixas.getValueAt(selectedRow, 0);
            home h = new home();
            h.setVisible(true);
            dispose();
        }
    }

    private void configurarAtalho() {
        // Atalho Alt+A para adicionar caixa
        KeyStroke keyStroke = KeyStroke.getKeyStroke("alt A");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "abrirJanela");
        getRootPane().getActionMap().put("abrirJanela", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirJanCaixa();
            }
        });

        // Atalho Alt+E para fechar caixa
        KeyStroke keyStrokeClose = KeyStroke.getKeyStroke("alt E");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeClose, "fecharCaixa");
        getRootPane().getActionMap().put("fecharCaixa", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fecharCaixa();
            }
        });
    }

    private void abrirJanCaixa() {
        JFrame addCaixaFrame = new addCaixa();
        addCaixaFrame.setVisible(true);
        addCaixaFrame.setLocationRelativeTo(null);
        addCaixaFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void abrirJanFunc() {
        JFrame addCaixaFrame = new Funcionarios();
        addCaixaFrame.setVisible(true);
        addCaixaFrame.setLocationRelativeTo(null);
        addCaixaFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void abrirAddFunc() {
        JFrame addCaixaFrame = new addFuncionarios();
        addCaixaFrame.setVisible(true);
        addCaixaFrame.setLocationRelativeTo(null);
        addCaixaFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void fecharCaixa() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um caixa para fechar.");
            return;
        }

        int idCaixaSelecionado = (int) tabelaCaixas.getValueAt(selectedRow, 0);
        Connection conn = Database.getConnection();

        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados. Verifique a conexão.");
            return;
        }

        PreparedStatement stmt = null;

        try {
            String sqlSelect = "SELECT id_funcionario, nome_funcionario, abertura FROM caixa WHERE id_caixa = ?";
            stmt = conn.prepareStatement(sqlSelect);
            stmt.setInt(1, idCaixaSelecionado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int idFuncionario = rs.getInt("id_funcionario");
                String nomeFunc = rs.getString("nome_funcionario");
                Timestamp horaAbertura = rs.getTimestamp("abertura");
                Timestamp horaFechamento = new Timestamp(System.currentTimeMillis());

                // Atualizar o banco de dados para fechar o caixa
                String sqlUpdate = "UPDATE caixa SET aberto = ?, fechamento = ? WHERE id_caixa = ?";
                stmt = conn.prepareStatement(sqlUpdate);
                stmt.setBoolean(1, false);
                stmt.setTimestamp(2, horaFechamento);
                stmt.setInt(3, idCaixaSelecionado);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    tabelaCaixas.setValueAt("Não", selectedRow, tabelaCaixas.findColumn("aberto"));
                    JOptionPane.showMessageDialog(this, "Caixa fechado com sucesso.\nFuncionário: " + nomeFunc
                            + "\nHora de abertura: " + horaAbertura + "\nHora de fechamento: " + horaFechamento);
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao fechar o caixa.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Caixa não encontrado.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao executar a operação no banco de dados.");
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        connPanel = new javax.swing.JPanel();
        msgPanel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        acoesMenu = new javax.swing.JMenu();
        novoCaixaIt = new javax.swing.JMenuItem();
        fecharCaixaIt = new javax.swing.JMenuItem();
        funcionariosMenu = new javax.swing.JMenu();
        gerenciarFuncBtn = new javax.swing.JMenuItem();
        novoFuncBtn = new javax.swing.JMenuItem();

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/pizza.png"))).getImage());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Selecione um caixa");

        jTable1.setModel(tabelaCaixas);
        jScrollPane1.setViewportView(jTable1);

        connPanel.setBackground(new java.awt.Color(204, 204, 204));

        msgPanel.setText("Você não deveria estar aqui....");

        javax.swing.GroupLayout connPanelLayout = new javax.swing.GroupLayout(connPanel);
        connPanel.setLayout(connPanelLayout);
        connPanelLayout.setHorizontalGroup(
            connPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(msgPanel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        connPanelLayout.setVerticalGroup(
            connPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, connPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(msgPanel)
                .addContainerGap())
        );

        acoesMenu.setText("Ações");

        novoCaixaIt.setText("Novo Caixa");
        novoCaixaIt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                novoCaixaItActionPerformed(evt);
            }
        });
        acoesMenu.add(novoCaixaIt);

        fecharCaixaIt.setText("Fechar Caixa");
        fecharCaixaIt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fecharCaixaItActionPerformed(evt);
            }
        });
        acoesMenu.add(fecharCaixaIt);

        jMenuBar1.add(acoesMenu);

        funcionariosMenu.setText("Funcionários");

        gerenciarFuncBtn.setText("Gerenciar");
        gerenciarFuncBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gerenciarFuncBtnActionPerformed(evt);
            }
        });
        funcionariosMenu.add(gerenciarFuncBtn);

        novoFuncBtn.setText("Novo Funcionário");
        novoFuncBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                novoFuncBtnActionPerformed(evt);
            }
        });
        funcionariosMenu.add(novoFuncBtn);

        jMenuBar1.add(funcionariosMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(149, 149, 149)
                        .addComponent(jLabel1))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(connPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(60, 60, 60))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(connPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void novoCaixaItActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_novoCaixaItActionPerformed
        abrirJanCaixa();
    }//GEN-LAST:event_novoCaixaItActionPerformed

    private void fecharCaixaItActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fecharCaixaItActionPerformed
        fecharCaixa();
    }//GEN-LAST:event_fecharCaixaItActionPerformed

    private void gerenciarFuncBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gerenciarFuncBtnActionPerformed
        abrirJanFunc();
    }//GEN-LAST:event_gerenciarFuncBtnActionPerformed

    private void novoFuncBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_novoFuncBtnActionPerformed
        abrirAddFunc();
    }//GEN-LAST:event_novoFuncBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Caixa().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu acoesMenu;
    private javax.swing.JPanel connPanel;
    private javax.swing.JMenuItem fecharCaixaIt;
    private javax.swing.JMenu funcionariosMenu;
    private javax.swing.JMenuItem gerenciarFuncBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel msgPanel;
    private javax.swing.JMenuItem novoCaixaIt;
    private javax.swing.JMenuItem novoFuncBtn;
    // End of variables declaration//GEN-END:variables
}
