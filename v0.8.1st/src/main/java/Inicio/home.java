package Inicio;

import Cardapio.addTamanho;
import Cardapio.addSabor;
import Cardapio.addBebida;
import Cardapio.JCardapio;
import Pedido.IntPizza;
import java.awt.*;
import javax.swing.*;
import DB.Database;
import static DB.Database.getConnection;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;

public class home extends javax.swing.JFrame {

    private DefaultTableModel tabelaPedidos = new DefaultTableModel(new Object[]{"ID", "Sabor", "Tamanho", "Bebida", "Cliente", "Rua", "Bairro", "Nº", "Hora", "Preço"}, 0);
    private JPanel searchPanel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private Connection connection = null;

    public home() {
        super("Início");
        initComponents();
        setLocationRelativeTo(null);
        setStyles();
        connectionUpdate.start();
        listaPedidos();
        setKeyboardShortcuts();

        fazP.setText("Fazer Pedido");
        fazP.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK));

        verCardapio.setText("Ver Cardápio");
        verCardapio.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.ALT_DOWN_MASK));

        menuTamanho.setText("Novo Tamanho");
        menuTamanho.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK));

        menuSabor.setText("Novo Sabor");
        menuSabor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK));

        jMenuItem2.setText("Nova Bebida");
        jMenuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.ALT_DOWN_MASK));

        // Atualiza diretamente pela tabela
        tabelaPedidos.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int id = Integer.parseInt(tabelaPedidos.getValueAt(row, 0).toString());  // ID do pedido
                    String sabor = (String) tabelaPedidos.getValueAt(row, 1);
                    String tamanho = (String) tabelaPedidos.getValueAt(row, 2);
                    String bebida = (String) tabelaPedidos.getValueAt(row, 3);
                    String cliente = (String) tabelaPedidos.getValueAt(row, 4);
                    String rua = (String) tabelaPedidos.getValueAt(row, 5);
                    String bairro = (String) tabelaPedidos.getValueAt(row, 6);
                    int numero = Integer.parseInt(tabelaPedidos.getValueAt(row, 7).toString());
                    String hora = (String) tabelaPedidos.getValueAt(row, 8);
                    double preco = Double.parseDouble(tabelaPedidos.getValueAt(row, 9).toString());

                    atualizarPelaTabelaP(id, sabor, tamanho, bebida, cliente, rua, bairro, numero, hora, preco);  // Atualiza no banco
                }
            }
        });

        // Exclui linhas selecionadas
        JTpedidos.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    int selectedRow = JTpedidos.getSelectedRow(); // Obtém a linha selecionada
                    if (selectedRow != -1) {
                        int id = Integer.parseInt(JTpedidos.getValueAt(selectedRow, 0).toString());  // Obtém o ID do pedido
                        excluirPelaTabelaP(id);  // Exclui do banco de dados

                        DefaultTableModel model = (DefaultTableModel) JTpedidos.getModel();
                        model.removeRow(selectedRow);  // Remove a linha da tabela

                        JOptionPane.showMessageDialog(null, "Item excluído com sucesso.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Selecione uma linha para excluir.");
                    }
                }
            }
        });

        // Atalho para fechar
        JRootPane rootPane = this.getRootPane();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_DOWN_MASK), "fechar");
        rootPane.getActionMap().put("fechar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha o programa
            }
        });

        initSearchField();
    }
    
    private Timer connectionUpdate = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            checkDatabaseConnection();
        }
    });

    private void checkDatabaseConnection() {
        try {
            connection = getConnection();
            updateConnectionStatusPanel(connection != null && !connection.isClosed()); // Atualiza painel de status
            connection.close();
            connection = null;
        } catch (SQLException | NullPointerException  ex) {
            updateConnectionStatusPanel(false);
        }
    }

    private void updateConnectionStatusPanel(boolean isConnected) {
        Color white = new Color(255, 255, 255);
        Color lightBlue = new Color(0, 153, 255);
        Color red = new Color(255, 0, 0);

        //System.out.println(isConnected);
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
    
    private void setStyles() {
        Color backgroundColor = new Color(245, 245, 245);
        Color panelBackgroundColor = new Color(230, 230, 230);
        Color tableBackgroundColor = new Color(255, 255, 255);
        Color headerColor = new Color(200, 200, 200);

        Font titleFont = new Font("SansSerif", Font.BOLD, 14);
        Font tableFont = new Font("SansSerif", Font.PLAIN, 12);

        this.getContentPane().setBackground(backgroundColor);
        jPanel1.setBackground(panelBackgroundColor);
        jPanel1.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTpedidos.setFont(tableFont);
        JTpedidos.setBackground(tableBackgroundColor);
        JTpedidos.setGridColor(new Color(210, 210, 210));
        JTpedidos.getTableHeader().setFont(titleFont);
        JTpedidos.getTableHeader().setBackground(headerColor);
        JTpedidos.getTableHeader().setForeground(Color.DARK_GRAY);

        jMenuBar1.setBackground(headerColor);
        jMenuBar1.setBorderPainted(false);
        acoes.setFont(titleFont);
        cardapio.setFont(titleFont);

        jPanel1.add(connPanel);
    }
    
    private void setKeyboardShortcuts() {
        // Shortcut Alt+S para "Sabores"
        JRootPane rootPane = this.getRootPane();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.ALT_DOWN_MASK), "openSabor");
        rootPane.getActionMap().put("openSabor", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuSaborActionPerformed(e);
            }
        });

        // Shortcut Alt+T para "Tamanho"
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.ALT_DOWN_MASK), "openTamanho");
        rootPane.getActionMap().put("openTamanho", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuTamanhoActionPerformed(e);
            }
        });

        // Shortcut Alt+B para "Bebidas"
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.ALT_DOWN_MASK), "openBebida");
        rootPane.getActionMap().put("openBebida", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItem2ActionPerformed(e);
            }
        });
    }
    
    private static void atualizarPelaTabelaP(int id, String sabor, String tamanho, String bebida, String cliente, String rua, String bairro, int numero, String hora, double preco) {
        try (Connection conn = Database.getConnection()) {  // Obtém conexão com o banco
            String query = "UPDATE pedido SET sabor = ?, tamanho = ?, bebida = ?, nomeCliente = ?, rua = ?, bairro = ?, numero = ?, hora = ?, precoFinal = ? WHERE id_pedido = ?";  // SQL com placeholders
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, sabor);
            stmt.setString(2, tamanho);
            stmt.setString(3, bebida);
            stmt.setString(4, cliente);
            stmt.setString(5, rua);
            stmt.setString(6, bairro);
            stmt.setInt(7, numero);
            stmt.setString(8, hora);
            stmt.setDouble(9, preco);
            stmt.setInt(10, id);
            stmt.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao salvar no banco de dados: " + ex.getMessage());
        }
    }

    private static void excluirPelaTabelaP(int id) {
        try (Connection conn = Database.getConnection()) {  // Obtém conexão com o banco
            String query = "DELETE FROM pedido WHERE id_pedido = ?";  // SQL com placeholders
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);  // Apenas o id é necessário para a exclusão
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao excluir do banco de dados: " + ex.getMessage());
        }
    }

    public void listaPedidos() {
        Connection conn = Database.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id_pedido, sabor, tamanho, bebida, nomeCliente, rua, bairro, numero, hora, precoFinal FROM pedido";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            // Limpa a tabela antes de adicionar novos dados
            tabelaPedidos.setRowCount(0);

            while (rs.next()) {
                // Recupera os dados do pedido e adiciona à tabela
                Object[] row = {
                    rs.getInt("id_pedido"),
                    rs.getString("sabor"),
                    rs.getString("tamanho"),
                    rs.getString("bebida"),
                    rs.getString("nomeCliente"),
                    rs.getString("rua"),
                    rs.getString("bairro"),
                    rs.getInt("numero"),
                    rs.getString("hora"),
                    rs.getDouble("precoFinal")
                };
                tabelaPedidos.addRow(row);  // Adiciona cada linha à tabela
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
    
    private void initSearchField() {
        // Painel principal que vai conter a busca, tabela e o connPanel
        JPanel mainPanel = new JPanel(null);
        mainPanel.setLayout(null); // Para controle manual do layout

        // Configuração do painel de busca com posição e tamanho fixos
        searchPanel = new JPanel(null);
        searchPanel.setBounds(10, 10, 600, 30); // Ajuste do tamanho do painel de busca
        searchField = new JTextField();
        searchField.setBounds(70, 5, 400, 20);  // Campo de busca no painel

        // Adiciona os componentes ao painel de busca
        JLabel labelBuscar = new JLabel("Buscar:");
        labelBuscar.setBounds(10, 5, 50, 20); // Texto de busca
        searchPanel.add(labelBuscar);
        searchPanel.add(searchField);

        // Adiciona o painel de busca ao painel principal
        mainPanel.add(searchPanel);

        // Configuração da tabela com rolagem
        JTpedidos.setModel(tabelaPedidos);  // Garante que JTpedidos use o modelo correto
        sorter = new TableRowSorter<>(tabelaPedidos); // Inicializa o sorter
        JTpedidos.setRowSorter(sorter); // Associa o sorter à tabela

        JScrollPane scrollPane = new JScrollPane(JTpedidos); // Tabela dentro de JScrollPane
        scrollPane.setBounds(10, 50, 600, 400); // Ajuste da posição e tamanho da tabela
        mainPanel.add(scrollPane); // Adiciona ao painel principal

        // Configuração do painel de conexão (connPanel)
        Color whiteBackground = new Color(255, 255, 255);
        Color ligthBlue = new Color(0, 153, 255);
        Color red = new Color(255, 0, 0);// White background for connPanel
        connPanel.setBounds(10, 460, 600, 30);  // Define o tamanho e posição do connPanel
        Connection conn = Database.getConnection();
        if (conn == null) {
            connPanel.setBackground(red); // Red background
            msgPanel.setText("Você não está conectado ao servidor! Os recursos do sistema não funcionarão corretamente.");
            msgPanel.setForeground(Color.white);
        } else {
            connPanel.setBackground(ligthBlue);// Blue background
            msgPanel.setText("A conexão com o banco está funcionando corretamente! Ao trabalho.");
            msgPanel.setForeground(whiteBackground);
        }
        mainPanel.add(connPanel); // Adiciona connPanel ao painel principal

        // Configuração da janela principal
        setContentPane(mainPanel); // Define o mainPanel como o conteúdo principal
        setSize(640, 540); // Ajusta o tamanho da janela para acomodar todos os componentes

        // Listener para busca em tempo real
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });
    }

    private void filterTable() {
        String query = searchField.getText().toLowerCase().trim();

        if (query.isEmpty()) {
            sorter.setRowFilter(null); // Mostra tudo se o campo estiver vazio
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query)); // Filtro de busca
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

        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jPanel1 = new javax.swing.JPanel();
        connPanel = new javax.swing.JPanel();
        msgPanel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        JTpedidos = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        acoes = new javax.swing.JMenu();
        fazP = new javax.swing.JMenuItem();
        cardapio = new javax.swing.JMenu();
        verCardapio = new javax.swing.JMenuItem();
        menuTamanho = new javax.swing.JMenuItem();
        menuSabor = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        jMenu2.setText("jMenu2");

        jMenuItem1.setText("jMenuItem1");

        jMenuItem3.setText("jMenuItem3");

        jMenu4.setText("jMenu4");

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        connPanel.setBackground(new java.awt.Color(255, 255, 255));
        connPanel.setPreferredSize(new java.awt.Dimension(150, 300));

        msgPanel.setText("Bom dia (Como você chegou aqui?)");

        javax.swing.GroupLayout connPanelLayout = new javax.swing.GroupLayout(connPanel);
        connPanel.setLayout(connPanelLayout);
        connPanelLayout.setHorizontalGroup(
            connPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(msgPanel)
                .addContainerGap(570, Short.MAX_VALUE))
        );
        connPanelLayout.setVerticalGroup(
            connPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(msgPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(connPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 766, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(connPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        JTpedidos.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        JTpedidos.setModel(tabelaPedidos);
        jScrollPane1.setViewportView(JTpedidos);

        acoes.setText("Ações");

        fazP.setText("Fazer Pedido");
        fazP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fazPActionPerformed(evt);
            }
        });
        acoes.add(fazP);

        jMenuBar1.add(acoes);

        cardapio.setText("Cardápio");

        verCardapio.setText("Ver Cardápio");
        verCardapio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verCardapioActionPerformed(evt);
            }
        });
        cardapio.add(verCardapio);

        menuTamanho.setText("Novo Tamanho");
        menuTamanho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuTamanhoActionPerformed(evt);
            }
        });
        cardapio.add(menuTamanho);

        menuSabor.setText("Novo Sabor");
        menuSabor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaborActionPerformed(evt);
            }
        });
        cardapio.add(menuSabor);

        jMenuItem2.setText("Nova Bebida");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        cardapio.add(jMenuItem2);

        jMenuBar1.add(cardapio);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 7, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 7, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void fazPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fazPActionPerformed
        IntPizza j = new IntPizza(this);
        j.Show();
        listaPedidos();
    }//GEN-LAST:event_fazPActionPerformed

    private void menuTamanhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuTamanhoActionPerformed
        JFrame j = new addTamanho();
        j.setVisible(true);
        j.setLocationRelativeTo(null);
    }//GEN-LAST:event_menuTamanhoActionPerformed

    private void menuSaborActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaborActionPerformed
        JFrame j = new addSabor();
        j.setVisible(true);
        j.setLocationRelativeTo(null);
    }//GEN-LAST:event_menuSaborActionPerformed

    private void verCardapioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verCardapioActionPerformed
        JFrame j = new JCardapio();
        j.setVisible(true);
        j.setLocationRelativeTo(null);
    }//GEN-LAST:event_verCardapioActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        JFrame j = new addBebida();
        j.setVisible(true);
        j.setLocationRelativeTo(null);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//        //</editor-fold>

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new home().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable JTpedidos;
    private javax.swing.JMenu acoes;
    private javax.swing.JMenu cardapio;
    private javax.swing.JPanel connPanel;
    private javax.swing.JMenuItem fazP;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem menuSabor;
    private javax.swing.JMenuItem menuTamanho;
    private javax.swing.JLabel msgPanel;
    private javax.swing.JMenuItem verCardapio;
    // End of variables declaration//GEN-END:variables
}