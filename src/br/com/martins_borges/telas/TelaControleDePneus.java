package br.com.martins_borges.telas;

import br.com.martins_borges.dal.PneuDAO;
import br.com.martins_borges.dal.VeiculoDAO;
import br.com.martins_borges.model.Pneu;
import br.com.martins_borges.model.Veiculo;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class TelaControleDePneus extends javax.swing.JDialog {

    private final VeiculoDAO veiculoDAO;
    private final PneuDAO pneuDAO;
    private List<Veiculo> listaDeVeiculos; // Para ter acesso ao objeto completo ao clicar

    public TelaControleDePneus(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.veiculoDAO = new VeiculoDAO();
        this.pneuDAO = new PneuDAO();
        initComponents();

        // Define um tamanho preferencial e centraliza a janela.
        definirTamanhoEPosicao();
        
        // Carrega os dados na tabela de veículos. A tabela de pneus começa vazia.
        atualizarTabelaVeiculos();
    }

    // Este método agora apenas POPULA a tabela com uma lista de pneus fornecida
    private void popularTabelaPneusEstoque(List<Pneu> pneus) {
        String[] colunas = {"EMPRESA", "N° FOGO", "TIPO DE PNEU", "MODELO", "MEDIDA"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (pneus != null) {
            for (Pneu pneu : pneus) {
                model.addRow(new Object[]{
                    pneu.getIdEmpresaProprietaria(),
                    pneu.getFogo(),
                    pneu.getTipoPneu(),
                    pneu.getModelo(),
                    pneu.getMedida()
                });
            }
        }
        
        Tabela_Exibicao_pneus_em_estoque.setModel(model);
    }

    private void atualizarTabelaVeiculos() {
        // Armazena a lista de veículos para uso posterior no clique da tabela
        this.listaDeVeiculos = veiculoDAO.listarTodos();
        
        DefaultTableModel model = new DefaultTableModel(new Object[]{"FROTA", "TIPO DE VEICULO"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Veiculo veiculo : this.listaDeVeiculos) {
            model.addRow(new Object[]{
                veiculo.getFROTA(),
                veiculo.getID_CONFIG_FK()
            });
        }
        
        Tabela_Exibicao_veiculos2.setModel(model);
    }

    private void definirTamanhoEPosicao() {
        // O código é executado no Event Dispatch Thread para garantir que o Frame pai já tenha um tamanho.
        javax.swing.SwingUtilities.invokeLater(() -> {
            java.awt.Window parent = javax.swing.SwingUtilities.getWindowAncestor(this);
            if (parent != null) {
                // Define um tamanho fixo, porém razoável, para a janela.
                int frameWidth = 1110;
                int frameHeight = 710;
                setSize(frameWidth, frameHeight);

                // Calcula a posição para centralizar em relação ao pai.
                int x = parent.getLocation().x + (parent.getWidth() - frameWidth) / 2;
                int y = parent.getLocation().y + (parent.getHeight() - frameHeight) / 2;

                // Adiciona um deslocamento vertical de 30 pixels para não cobrir o menu.
                y += 30;

                // Garante que a janela não fique com coordenadas negativas.
                x = Math.max(0, x);
                y = Math.max(0, y);

                setLocation(x, y);
            }
        });
    }

  TelaControleDePneus() {
        throw new UnsupportedOperationException("Not supported yet."); 
  }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Tipo_pneu = new javax.swing.JLabel();
        PNEUS_ESTOQUE = new javax.swing.JScrollPane();
        Tabela_Exibicao_pneus_em_estoque = new javax.swing.JTable();
        Painel = new javax.swing.JPanel();
        lbtitulo = new javax.swing.JLabel();
        lbconserto = new javax.swing.JLabel();
        conserto = new javax.swing.JLabel();
        lbestoque = new javax.swing.JLabel();
        estoque = new javax.swing.JLabel();
        lbsucata = new javax.swing.JLabel();
        sucata = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabela_Exibicao_veiculos1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tabela_Exibicao_veiculos2 = new javax.swing.JTable();
        PaneldoControle = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("TelaControleDePneus"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1000, 690));
        getContentPane().setLayout(null);
        getContentPane().add(Tipo_pneu);
        Tipo_pneu.setBounds(593, 61, 202, 22);

        PNEUS_ESTOQUE.setPreferredSize(new java.awt.Dimension(900, 402));

        Tabela_Exibicao_pneus_em_estoque.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Tabela_Exibicao_pneus_em_estoque.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PNEUS EM ESTOQUE"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabela_Exibicao_pneus_em_estoque.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        Tabela_Exibicao_pneus_em_estoque.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Tabela_Exibicao_pneus_em_estoque.setMaximumSize(null);
        Tabela_Exibicao_pneus_em_estoque.setName("Tabela_Exibicao_veiculos"); // NOI18N
        Tabela_Exibicao_pneus_em_estoque.getTableHeader().setReorderingAllowed(false);
        Tabela_Exibicao_pneus_em_estoque.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tabela_Exibicao_pneus_em_estoqueMouseClicked(evt);
            }
        });
        PNEUS_ESTOQUE.setViewportView(Tabela_Exibicao_pneus_em_estoque);

        getContentPane().add(PNEUS_ESTOQUE);
        PNEUS_ESTOQUE.setBounds(643, 18, 330, 175);

        Painel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Painel.setLayout(null);

        lbtitulo.setBackground(new java.awt.Color(102, 102, 102));
        lbtitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbtitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbtitulo.setText("AÇOES DO PNEU");
        lbtitulo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbtitulo.setOpaque(true);
        Painel.add(lbtitulo);
        lbtitulo.setBounds(0, 0, 130, 20);

        lbconserto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbconserto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/support.png"))); // NOI18N
        lbconserto.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lbconserto.setName("AG. CONSERTO"); // NOI18N
        Painel.add(lbconserto);
        lbconserto.setBounds(9, 30, 100, 104);

        conserto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        conserto.setText("AG. CONSERTO");
        conserto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Painel.add(conserto);
        conserto.setBounds(20, 110, 85, 16);

        lbestoque.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbestoque.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/ESTOQUE.png"))); // NOI18N
        lbestoque.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbestoque.setMaximumSize(new java.awt.Dimension(64, 64));
        lbestoque.setMinimumSize(new java.awt.Dimension(64, 64));
        lbestoque.setName("ESTOQUE"); // NOI18N
        lbestoque.setPreferredSize(new java.awt.Dimension(64, 64));
        Painel.add(lbestoque);
        lbestoque.setBounds(10, 190, 100, 120);

        estoque.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        estoque.setText("ESTOQUE");
        estoque.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Painel.add(estoque);
        estoque.setBounds(30, 290, 53, 16);

        lbsucata.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbsucata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/LIXO.png"))); // NOI18N
        lbsucata.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Painel.add(lbsucata);
        lbsucata.setBounds(20, 370, 77, 110);

        sucata.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        sucata.setText("SUCATA");
        sucata.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Painel.add(sucata);
        sucata.setBounds(35, 460, 50, 16);

        getContentPane().add(Painel);
        Painel.setBounds(433, 18, 128, 505);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(900, 402));

        Tabela_Exibicao_veiculos1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Tabela_Exibicao_veiculos1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "N° FOGO", "FABRICANTE", "PROFUNDIDADE"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabela_Exibicao_veiculos1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        Tabela_Exibicao_veiculos1.setMaximumSize(null);
        Tabela_Exibicao_veiculos1.setName("Tabela_Exibicao_veiculos"); // NOI18N
        Tabela_Exibicao_veiculos1.getTableHeader().setReorderingAllowed(false);
        Tabela_Exibicao_veiculos1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tabela_Exibicao_veiculos1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Tabela_Exibicao_veiculos1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(640, 560, 340, 134);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(900, 402));

        Tabela_Exibicao_veiculos2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Tabela_Exibicao_veiculos2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "FROTA", "TIPO DE VEICULO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabela_Exibicao_veiculos2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        Tabela_Exibicao_veiculos2.setMaximumSize(null);
        Tabela_Exibicao_veiculos2.setName("Tabela_Exibicao_veiculos"); // NOI18N
        Tabela_Exibicao_veiculos2.getTableHeader().setReorderingAllowed(false);
        Tabela_Exibicao_veiculos2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tabela_Exibicao_veiculos2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(Tabela_Exibicao_veiculos2);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(643, 205, 330, 348);

        javax.swing.GroupLayout PaneldoControleLayout = new javax.swing.GroupLayout(PaneldoControle);
        PaneldoControle.setLayout(PaneldoControleLayout);
        PaneldoControleLayout.setHorizontalGroup(
            PaneldoControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 310, Short.MAX_VALUE)
        );
        PaneldoControleLayout.setVerticalGroup(
            PaneldoControleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 670, Short.MAX_VALUE)
        );

        getContentPane().add(PaneldoControle);
        PaneldoControle.setBounds(30, 20, 310, 670);

        getAccessibleContext().setAccessibleName("TelaControleDePneus");

        setSize(new java.awt.Dimension(1016, 711));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void Tabela_Exibicao_pneus_em_estoqueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Tabela_Exibicao_pneus_em_estoqueMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_Tabela_Exibicao_pneus_em_estoqueMouseClicked

    private void Tabela_Exibicao_veiculos1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Tabela_Exibicao_veiculos1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_Tabela_Exibicao_veiculos1MouseClicked

    private void Tabela_Exibicao_veiculos2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Tabela_Exibicao_veiculos2MouseClicked
        int selectedRow = Tabela_Exibicao_veiculos2.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // Pega o veículo selecionado da lista que já carregamos
        Veiculo veiculoSelecionado = this.listaDeVeiculos.get(selectedRow);
        String medidaNecessaria = veiculoSelecionado.getMEDIDA_PNEU();

        // Se o veículo não tiver uma medida de pneu definida, não faz nada
        if (medidaNecessaria == null || medidaNecessaria.trim().isEmpty()) {
            // Limpa a tabela de pneus se o veículo não tiver medida
            popularTabelaPneusEstoque(new java.util.ArrayList<>());
            return;
        }

        // Busca no banco os pneus em estoque que tenham a medida necessária
        List<Pneu> pneusCompativeis = pneuDAO.listarPneusPorStatusEMedida("ESTOQUE", medidaNecessaria);

        // Popula a tabela de pneus com os resultados
        popularTabelaPneusEstoque(pneusCompativeis);

        // Limpa a outra tabela de pneus (detalhes) pois um novo veículo foi selecionado
        Tabela_Exibicao_veiculos1.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {"N° FOGO", "FABRICANTE", "PROFUNDIDADE"}
        ));
    }//GEN-LAST:event_Tabela_Exibicao_veiculos2MouseClicked

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
            java.util.logging.Logger.getLogger(TelaControleDePneus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaControleDePneus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaControleDePneus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaControleDePneus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TelaControleDePneus dialog = new TelaControleDePneus(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Este método garante que o pneu não fique distorcido e o posiciona.
private void configuraPneu(javax.swing.JLabel labelDoPneu, javax.swing.ImageIcon icone, int x, int y) {
    if (labelDoPneu != null) {
        int larguraPneu = icone.getIconWidth();
        int alturaPneu = icone.getIconHeight();

        labelDoPneu.setIcon(icone);
        // Define a posição E O TAMANHO do label para ser exato ao da imagem
        labelDoPneu.setBounds(x, y, larguraPneu, alturaPneu); 
        labelDoPneu.setVisible(true);
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane PNEUS_ESTOQUE;
    private javax.swing.JPanel Painel;
    private javax.swing.JPanel PaneldoControle;
    private javax.swing.JTable Tabela_Exibicao_pneus_em_estoque;
    private javax.swing.JTable Tabela_Exibicao_veiculos1;
    private javax.swing.JTable Tabela_Exibicao_veiculos2;
    private javax.swing.JLabel Tipo_pneu;
    private javax.swing.JLabel conserto;
    private javax.swing.JLabel estoque;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbconserto;
    private javax.swing.JLabel lbestoque;
    private javax.swing.JLabel lbsucata;
    private javax.swing.JLabel lbtitulo;
    private javax.swing.JLabel sucata;
    // End of variables declaration//GEN-END:variables

    void setState(int NORMAL) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
