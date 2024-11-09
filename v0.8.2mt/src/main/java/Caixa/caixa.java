package Caixa;

import DB.Database;
import Inicio.home;
import com.formdev.flatlaf.FlatLightLaf;
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

public final class Caixa extends javax.swing.JFrame {

    private DefaultTableModel tabelaCaixas = new DefaultTableModel(new Object[]{"Caixa", "Funcionário", "Aberto"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;  // Torna todas as células não editáveis
        }
    };

    private static int id_caixa;

    public static int getIDCaixa() {
        return id_caixa;
    }

    /**
     * Creates new form caixa
     */
    public Caixa() {
        initComponents();
        setLocationRelativeTo(null);
        listaCaixas();

        configurarAtalho();

        // Adiciona ouvintes para clique duplo e tecla Enter
        addEventListeners();
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

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        novoCaixaIt = new javax.swing.JMenuItem();
        fecharCaixaIt = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/pizza.png"))).getImage());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Selecione um caixa");

        jTable1.setModel(tabelaCaixas);
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jLabel2.setText("Você não deveria estar aqui....");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        jMenu1.setText("Ações");

        novoCaixaIt.setText("Novo Caixa");
        novoCaixaIt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                novoCaixaItActionPerformed(evt);
            }
        });
        jMenu1.add(novoCaixaIt);

        fecharCaixaIt.setText("Fechar Caixa");
        fecharCaixaIt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fecharCaixaItActionPerformed(evt);
            }
        });
        jMenu1.add(fecharCaixaIt);

        jMenuBar1.add(jMenu1);

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
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            //new home().setVisible(true); 
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem fecharCaixaIt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JMenuItem novoCaixaIt;
    // End of variables declaration//GEN-END:variables
}
