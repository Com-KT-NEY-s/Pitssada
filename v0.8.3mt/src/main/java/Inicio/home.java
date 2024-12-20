package Inicio;

import Caixa.Caixa;
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
import java.io.File;
import java.sql.*;
import java.util.concurrent.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;

public class home extends javax.swing.JFrame {

    private DefaultTableModel tabelaPedidos = new DefaultTableModel(new Object[]{"ID", "Sabor", "Tamanho", "Bebida", "Cliente", "Rua", "Bairro", "Nº", "Hora", "Preço"}, 0);
    private JPanel searchPanel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private Connection connection = null;
    private ScheduledExecutorService connectionChecker;  // ExecutorService para a tarefa de verificação
    private static File loadedFile = null; // verifies if a file was loaded || verifica se um arquivo foi carregado
    private static DefaultTableModel tableModel; // tabel model || model da tabela
    public int numeroCaixa;
    public static String ficaAberto = "";

    public home() {
        super("Início");
        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Evita que o programa finalize ao fechar 'home'

        // Aplique o tema selecionado no início
        String theme = Configuracoes.isDarkThemeEnabled() ? "dark" : "light";
        Configuracoes.setLookAndFeel(theme);
        setStyles();
        listaPedidos();
        setKeyboardShortcuts();

        // Configura o comportamento de confirmação ao fechar a janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarFechamento();
            }
        });

        // Resto da configuração inicial
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

        startDatabaseConnectionChecker();
        initSearchField();
    }

    // Inicia o ScheduledExecutorService para checar a conexão do banco em background
    public void startDatabaseConnectionChecker() {
        connectionChecker = Executors.newScheduledThreadPool(1);
        connectionChecker.scheduleAtFixedRate(() -> {
            try {
                checkDatabaseConnection();
            } catch (Exception ex) {
                System.err.println("Erro na verificação de conexão: " + ex.getMessage());
                ex.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void checkDatabaseConnection() {
        try {
            connection = getConnection();
            updateConnectionStatusPanel(connection != null && !connection.isClosed());
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException | NullPointerException ex) {
            System.err.println("Erro ao conectar ao banco de dados: " + ex.getMessage());
            updateConnectionStatusPanel(false);
        }
    }

    public void updateConnectionStatusPanel(boolean isConnected) {
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

    // Fecha o ExecutorService quando a aplicação for fechada
    @Override
    public void dispose() {
        if (connectionChecker != null && !connectionChecker.isShutdown()) {
            connectionChecker.shutdown();
            try {
                if (!connectionChecker.awaitTermination(1, TimeUnit.SECONDS)) {
                    connectionChecker.shutdownNow();
                }
            } catch (InterruptedException ex) {
                connectionChecker.shutdownNow();
                System.err.println("Erro ao encerrar ExecutorService: " + ex.getMessage());
            }
        }
        super.dispose();
    }

    private void confirmarFechamento() {
        int resposta = JOptionPane.showOptionDialog(
                this,
                "Sair?\n Sim - Fechar o Caixa\n Não - Sair dos Pedidos, Caixa continua Aberto\n Cancelar - Nada",
                "Confirmação de Saída",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Sim", "Não", "Cancelar"},
                "Sim"
        );

        Caixa c = new Caixa();
        Caixa caixa = Caixa.getInstance(); // Garantir uma única instância do Caixa
        int idCaixaAtual = caixa.getIdCaixaAtual(); // Obtém o ID do caixa em uso (assumindo que há um método para isso)

        switch (resposta) {
            case JOptionPane.YES_OPTION:
                caixa.atualizarEstadoCaixaTabela(false, idCaixaAtual); // Marca "Aberto" como "Não"
                c.setVisible(true);
                
                dispose(); // Fecha a janela `home`
                break;
            case JOptionPane.NO_OPTION:
                caixa.atualizarEstadoCaixaTabela(true, idCaixaAtual); // Marca "Aberto" como "Sim"
                c.setVisible(true);
                
                dispose(); // Fecha a janela `home`
                break;
            case JOptionPane.CANCEL_OPTION:
                // Não faz nada
                break;
        }
    }

    public void fecharCaixa() {
        Connection conn = Database.getConnection();
        PreparedStatement updateStmt = null;

        try {
            String sqlUpdate = "UPDATE caixa SET aberto = ?, fechamento = ? WHERE id_caixa = ?";
            updateStmt = conn.prepareStatement(sqlUpdate);
            updateStmt.setString(1, ficaAberto);
            updateStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            updateStmt.setInt(3, Caixa.getIDCaixa());

            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Caixa fechado com sucesso.");

            } else {
                JOptionPane.showMessageDialog(this, "Erro ao fechar o caixa.");
            }
        } catch (SQLException e) {
        }

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

        // Shortcut Alt+B para "Configurações"
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_DOWN_MASK), "openConfigs");
        rootPane.getActionMap().put("openConfigs", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configsActionPerformed(e);
            }
        });
    }

    private void setStyles() {
        Color backgroundColor;
        Color panelBackgroundColor;
        Color tableBackgroundColor;
        Color headerColor;
        Color textColor;

        if (Configuracoes.isDarkThemeEnabled()) {
            // Cores para o tema escuro
            backgroundColor = new Color(60, 63, 65);
            panelBackgroundColor = new Color(43, 43, 43);
            tableBackgroundColor = new Color(69, 73, 74);
            headerColor = new Color(75, 110, 175);
            textColor = Color.WHITE;
        } else {
            // Cores para o tema claro
            backgroundColor = new Color(245, 245, 245);
            panelBackgroundColor = new Color(230, 230, 230);
            tableBackgroundColor = new Color(255, 255, 255);
            headerColor = new Color(200, 200, 200);
            textColor = Color.DARK_GRAY;
        }

        // Configura o estilo da janela e dos componentes
        this.getContentPane().setBackground(backgroundColor);
        jPanel1.setBackground(panelBackgroundColor);
        jPanel1.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Configuração da tabela
        JTpedidos.setFont(new Font("SansSerif", Font.PLAIN, 12));
//        JTpedidos.setBackground(tableBackgroundColor);
        JTpedidos.setForeground(textColor);
//        JTpedidos.setGridColor(new Color(210, 210, 210));
        JTpedidos.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        JTpedidos.getTableHeader().setBackground(headerColor);
        JTpedidos.getTableHeader().setForeground(textColor);

        // Configuração da barra de menus
        jMenuBar1.setBackground(headerColor);
        jMenuBar1.setBorderPainted(false);
        acoes.setFont(new Font("SansSerif", Font.BOLD, 14));
        cardapio.setFont(new Font("SansSerif", Font.BOLD, 14));

        jPanel1.add(connPanel);
    }

    private void initSearchField() {
        JPanel mainPanel = new JPanel(null);
        mainPanel.setLayout(null);

        searchPanel = new JPanel(null);
        searchPanel.setBounds(10, 10, 600, 30);
        searchField = new JTextField();
        searchField.setBounds(70, 5, 400, 20);

        JLabel labelBuscar = new JLabel("Buscar:");
        labelBuscar.setBounds(10, 5, 50, 20);
        searchPanel.add(labelBuscar);
        searchPanel.add(searchField);
        mainPanel.add(searchPanel);

        JTpedidos.setModel(tabelaPedidos);
        sorter = new TableRowSorter<>(tabelaPedidos);
        JTpedidos.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(JTpedidos);
        scrollPane.setBounds(10, 50, 600, 400);
        mainPanel.add(scrollPane);

        Color whiteBackground = new Color(255, 255, 255);
        Color ligthBlue = new Color(0, 153, 255);
        Color red = new Color(255, 0, 0);
        connPanel.setBounds(10, 460, 600, 30);
        Connection conn = Database.getConnection();
        if (conn == null) {
            connPanel.setBackground(red);
            msgPanel.setText("Você não está conectado ao servidor! Os recursos do sistema não funcionarão corretamente.");
            msgPanel.setForeground(Color.white);
        } else {
            connPanel.setBackground(ligthBlue);
            msgPanel.setText("A conexão com o banco está funcionando corretamente! Ao trabalho.");
            msgPanel.setForeground(whiteBackground);
        }
        mainPanel.add(connPanel);

        setContentPane(mainPanel);
        setSize(640, 540);

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
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    public void listaPedidos() {
        Connection conn = Database.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id_pedido, sabor, tamanho, bebida, nomeCliente, rua, bairro, numero, hora, precoFinal FROM pedido WHERE id_caixa = ?";

            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, Caixa.getIDCaixa());
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
        jScrollPane1 = new javax.swing.JScrollPane();
        JTpedidos = new javax.swing.JTable();
        connPanel = new javax.swing.JPanel();
        msgPanel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        acoes = new javax.swing.JMenu();
        fazP = new javax.swing.JMenuItem();
        configs = new javax.swing.JMenuItem();
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 766, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        JTpedidos.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        JTpedidos.setModel(tabelaPedidos);
        jScrollPane1.setViewportView(JTpedidos);

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

        acoes.setText("Ações");

        fazP.setText("Fazer Pedido");
        fazP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fazPActionPerformed(evt);
            }
        });
        acoes.add(fazP);

        configs.setText("Configurações");
        configs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configsActionPerformed(evt);
            }
        });
        acoes.add(configs);

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(connPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 766, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(connPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
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

    private void configsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configsActionPerformed
        JFrame j = new Configuracoes();
        j.setVisible(true);
        j.setLocationRelativeTo(null);
    }//GEN-LAST:event_configsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
    private javax.swing.JMenuItem configs;
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
