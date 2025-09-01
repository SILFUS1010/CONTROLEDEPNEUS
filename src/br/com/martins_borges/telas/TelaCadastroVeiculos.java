package br.com.martins_borges.telas;

import java.awt.Point; // Adicionar esta linha
import br.com.martins_borges.dal.TipoConfiguracaoDAO;
import br.com.martins_borges.dal.VeiculoDAO;
import br.com.martins_borges.dal.MedidaPneuDAO;
import br.com.martins_borges.model.TipoConfiguracao;
import br.com.martins_borges.model.Veiculo;
import java.awt.CardLayout;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TelaCadastroVeiculos extends javax.swing.JDialog {

    private List<Veiculo> listaDeVeiculos; // Para armazenar a lista completa de veículos
    private final List<Integer> carretaIds = Arrays.asList(4, 7, 8); // IDs que precisam de posição
    private Veiculo veiculoSelecionado = null; // Para rastrear o veículo em modo de edição

    private javax.swing.JLabel[][] slotsDePneus;
    private final VeiculoDAO veiculoDAO;
    private final TipoConfiguracaoDAO tipoConfiguracaoDAO;
    private final MedidaPneuDAO medidaPneuDAO; // Adicionado para gerenciar medidas de pneu

    // ESTE É O CONSTRUTOR CORRIGIDO PARA A SUA CLASSE TelaCadastroVeiculos
    public TelaCadastroVeiculos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setUndecorated(true);

        // Inicializa os DAOs ANTES do bloco try
        this.veiculoDAO = new VeiculoDAO();
        this.tipoConfiguracaoDAO = new TipoConfiguracaoDAO();
        this.medidaPneuDAO = new MedidaPneuDAO();

        // Seu handler de exceções
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.err.println("EXCEÇÃO NÃO CAPTURADA DETECTADA!");
                System.err.println("Thread: " + t.getName());
                System.err.println("Erro: " + e.getMessage());
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(TelaCadastroVeiculos.this,
                            "Erro interno detectado:\n" + e.getMessage()
                            + "\n\nA tela será mantida aberta para análise.",
                            "Erro Capturado",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        });

        try {
            initComponents();

            Tabela_Exibicao_veiculos.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
                public void valueChanged(javax.swing.event.ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting()) {
                        int selectedRow = Tabela_Exibicao_veiculos.getSelectedRow();
                        mostrarVeiculoSelecionado(selectedRow);
                    }
                }
            });

            // Adiciona o KeyListener para a tecla ESC
            this.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                        dispose(); // Fecha a janela
                    }
                }
            });
            this.setFocusable(true); // Garante que o JDialog possa receber foco para o KeyListener
            this.requestFocusInWindow(); // Solicita o foco para o JDialog

             javax.swing.ListCellRenderer renderer = cbposicao_carreta.getRenderer();
    if (renderer instanceof javax.swing.JLabel) {
        ((javax.swing.JLabel) renderer).setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    }
            Exclui_veiculos.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        int selectedRow = Tabela_Exibicao_veiculos.getSelectedRow();
                        // Apenas prossegue se uma linha estiver realmente selecionada
                        if (selectedRow != -1) {
                            int modelRow = Tabela_Exibicao_veiculos.convertRowIndexToModel(selectedRow);
                            int idParaExcluir = (int) Tabela_Exibicao_veiculos.getModel().getValueAt(modelRow, 0);
                            String frotaParaConfirmar = Tabela_Exibicao_veiculos.getModel().getValueAt(modelRow, 1).toString();
                            int resposta = JOptionPane.showConfirmDialog(TelaCadastroVeiculos.this,
                                "Tem certeza que deseja excluir o Veículo Frota " + frotaParaConfirmar + "?\nEsta ação não pode ser desfeita.",
                                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                            if (resposta == JOptionPane.YES_OPTION) {
                                boolean sucesso = veiculoDAO.excluir(idParaExcluir);
                                if (sucesso) {
                                    JOptionPane.showMessageDialog(TelaCadastroVeiculos.this, "Veículo excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                                    atualizarTabelaVeiculos();
                                    atualizarContagemTotalPneus();
                                    atualizarContagemDeVeiculos();
                                } else {
                                    JOptionPane.showMessageDialog(TelaCadastroVeiculos.this, "Falha ao excluir o veículo.", "Erro", JOptionPane.ERROR_MESSAGE);
                                }
                                CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
                                layout.show(EscolhaModelo, "TELA_ZERO_ZERO");
                            }
                        }
                    }
                }
            });

            // Implementa a funcionalidade de arrastar a janela
            final Point initialClick = new Point();
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent e) {
                    initialClick.x = e.getX();
                    initialClick.y = e.getY();
                }
            });
            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                public void mouseDragged(java.awt.event.MouseEvent e) {
                    int thisX = getLocation().x;
                    int thisY = getLocation().y;
                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;
                    setLocation(thisX + xMoved, thisY + yMoved);
                }
            });

            // Listeners dos campos
            cmbTipoVeiculo.addActionListener(e -> gerenciarVisibilidadePosicao());
            txtFrota.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    txtFrotaFocusLost(evt);
                }
            });
            txtFrota.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent evt) {
                    txtFrotaKeyTyped(evt);
                }
            });

            txtPlaca.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    txtPlacaFocusLost(evt);
                }
            });

            lbPosicao.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        // Verifique se um veículo está selecionado e se o label está visível
                        if (veiculoSelecionado != null && lbPosicao.isVisible()) {
                            try {
                                int currentPosition = Integer.parseInt(lbPosicao.getText());
                                int newPosition = (currentPosition == 1) ? 2 : 1;

                                // Atualiza o objeto e o label
                                veiculoSelecionado.setPosicaoCarreta(newPosition);
                                lbPosicao.setText(String.valueOf(newPosition));

                                // Atualiza no banco de dados
                                if (veiculoDAO.atualizarVeiculo(veiculoSelecionado)) {
                                    JOptionPane.showMessageDialog(TelaCadastroVeiculos.this,
                                            "Posição da carreta atualizada para " + newPosition + " com sucesso!",
                                            "Sucesso",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    atualizarTabelaVeiculos(); // Atualiza a tabela para refletir a mudança
                                    limparCampos();
                                    CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
                                    layout.show(EscolhaModelo, "TELA_ZERO_ZERO");
                                } else {
                                    JOptionPane.showMessageDialog(TelaCadastroVeiculos.this,
                                            "Falha ao atualizar a posição da carreta.",
                                            "Erro",
                                            JOptionPane.ERROR_MESSAGE);
                                    // Reverte a mudança no label se a atualização falhar
                                    lbPosicao.setText(String.valueOf(currentPosition));
                                    veiculoSelecionado.setPosicaoCarreta(currentPosition);
                                }
                            } catch (NumberFormatException e) {
                                // Trata o caso onde o texto do label não é um número válido
                                JOptionPane.showMessageDialog(TelaCadastroVeiculos.this,
                                        "Erro ao ler a posição atual da carreta.",
                                        "Erro de Formato",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            });

            loadVehicleConfigs();

            inicializarComponentesChassi();
            carregarTiposVeiculo();
            carregarMedidasPneu();
            atualizarContagemTotalPneus();
            atualizarTabelaVeiculos();
            atualizarContagemDeVeiculos();
            definirTamanhoEPosicao();

            gerenciarVisibilidadePosicao(); // Define o estado inicial

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO no construtor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void definirTamanhoEPosicao() {

        SwingUtilities.invokeLater(() -> {

            int frameWidth = 1100;
            int frameHeight = 682;
            setSize(frameWidth, frameHeight); // Reintroduzido setSize

            // Centraliza a janela em relação ao seu pai (TelaPrincipal)
            setLocationRelativeTo(getParent());
        });
    }
    
    private void mostrarMensagem(String texto, int yPos, int xPos) {
    SwingUtilities.invokeLater(() -> {
        labelMensagem.setText(texto);
        labelMensagem.setForeground(java.awt.Color.RED);
        labelMensagem.setFont(new java.awt.Font("tahoma", java.awt.Font.BOLD, 12));
        java.awt.Dimension tamanhoIdeal = labelMensagem.getPreferredSize();
        labelMensagem.setBounds(xPos, yPos, tamanhoIdeal.width, tamanhoIdeal.height);
        TELA_ZERO.add(labelMensagem);
        labelMensagem.setVisible(true);
        TELA_ZERO.setComponentZOrder(labelMensagem, 0);
        TELA_ZERO.repaint();
    });
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        EscolhaModelo = new javax.swing.JPanel();
        TELA_ZERO_ZERO = new javax.swing.JPanel();
        TELA_ZERO = new javax.swing.JPanel();
        Escolha_0 = new javax.swing.JLabel();
        zero = new javax.swing.JLabel();
        lbespinha_dorsal = new javax.swing.JLabel();
        lbeixo1 = new javax.swing.JLabel();
        lbeixo2 = new javax.swing.JLabel();
        lbeixo3 = new javax.swing.JLabel();
        lbeixo4 = new javax.swing.JLabel();
        lbeixo5 = new javax.swing.JLabel();
        lbeixo6 = new javax.swing.JLabel();
        Label100 = new javax.swing.JLabel();
        Label101 = new javax.swing.JLabel();
        Label102 = new javax.swing.JLabel();
        Label103 = new javax.swing.JLabel();
        labelMensagem = new javax.swing.JLabel();
        Label104 = new javax.swing.JLabel();
        Label105 = new javax.swing.JLabel();
        Label106 = new javax.swing.JLabel();
        Label107 = new javax.swing.JLabel();
        Label108 = new javax.swing.JLabel();
        Label109 = new javax.swing.JLabel();
        Label110 = new javax.swing.JLabel();
        Label111 = new javax.swing.JLabel();
        Label112 = new javax.swing.JLabel();
        Label113 = new javax.swing.JLabel();
        Label114 = new javax.swing.JLabel();
        Label115 = new javax.swing.JLabel();
        Label116 = new javax.swing.JLabel();
        Label117 = new javax.swing.JLabel();
        Label118 = new javax.swing.JLabel();
        Label119 = new javax.swing.JLabel();
        Label120 = new javax.swing.JLabel();
        Label121 = new javax.swing.JLabel();
        Label122 = new javax.swing.JLabel();
        Label123 = new javax.swing.JLabel();
        Frota = new javax.swing.JLabel();
        txtFrota = new javax.swing.JTextField();
        placa = new javax.swing.JLabel();
        txtPlaca = new javax.swing.JTextField();
        numero_pneus = new javax.swing.JLabel();
        Qtd_numeroPneu = new javax.swing.JLabel();
        tipo_equipamento = new javax.swing.JLabel();
        cmbTipoVeiculo = new javax.swing.JComboBox<>();
        lbMedidaPneu = new javax.swing.JLabel();
        cmbMedidaPneu = new javax.swing.JComboBox<>();
        Tabela_Caminhoes = new javax.swing.JScrollPane();
        Tabela_Exibicao_veiculos = new javax.swing.JTable();
        Cadastrar = new javax.swing.JButton();
        qtd_pneus = new javax.swing.JLabel();
        qtd_veiculos = new javax.swing.JLabel();
        Exclui_veiculos = new javax.swing.JLabel();
        BUTTON_BOX = new javax.swing.JPanel();
        cod_zero = new javax.swing.JButton();
        cod_um = new javax.swing.JButton();
        cod_dois = new javax.swing.JButton();
        cod_trez = new javax.swing.JButton();
        cod_quatro = new javax.swing.JButton();
        cod_cinco = new javax.swing.JButton();
        cod_seis = new javax.swing.JButton();
        cod_sete = new javax.swing.JButton();
        cod_oito = new javax.swing.JButton();
        cod_nove = new javax.swing.JButton();
        cod_dez = new javax.swing.JButton();
        cod_onze = new javax.swing.JButton();
        cod_doze = new javax.swing.JButton();
        cod_treze = new javax.swing.JButton();
        cod_quatorze = new javax.swing.JButton();
        cod_quinze = new javax.swing.JButton();
        cod_desesseis = new javax.swing.JButton();
        cod_desessete = new javax.swing.JButton();
        dolly = new javax.swing.JButton();
        lb_carreta = new javax.swing.JLabel();
        lbPosicao = new javax.swing.JLabel();
        cbposicao_carreta = new javax.swing.JComboBox<>();
        btAdd_Pneu = new javax.swing.JButton();
        fechar = new javax.swing.JButton();
        medidaPneu = new javax.swing.JScrollPane();
        Tabela_medidaPneu = new javax.swing.JTable();
        Veiculos_Cadastrados = new javax.swing.JLabel();
        Pneus_necessarios = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1000, 670));
        setName("TelaCadastroVeiculos"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1200, 605));

        EscolhaModelo.setBorder(new javax.swing.border.MatteBorder(null));
        EscolhaModelo.setMinimumSize(new java.awt.Dimension(353, 550));
        EscolhaModelo.setName("EscolhaModelo"); // NOI18N
        EscolhaModelo.setOpaque(false);
        EscolhaModelo.setPreferredSize(new java.awt.Dimension(450, 550));
        EscolhaModelo.setRequestFocusEnabled(false);
        EscolhaModelo.setVerifyInputWhenFocusTarget(false);
        EscolhaModelo.setLayout(new java.awt.CardLayout());

        TELA_ZERO_ZERO.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        TELA_ZERO_ZERO.setAutoscrolls(true);
        TELA_ZERO_ZERO.setDoubleBuffered(false);
        TELA_ZERO_ZERO.setMaximumSize(new java.awt.Dimension(450, 450));
        TELA_ZERO_ZERO.setMinimumSize(new java.awt.Dimension(253, 608));
        TELA_ZERO_ZERO.setName("TELA_ZERO_ZERO"); // NOI18N
        TELA_ZERO_ZERO.setPreferredSize(new java.awt.Dimension(650, 450));
        TELA_ZERO_ZERO.setRequestFocusEnabled(false);
        TELA_ZERO_ZERO.setVerifyInputWhenFocusTarget(false);

        org.jdesktop.layout.GroupLayout TELA_ZERO_ZEROLayout = new org.jdesktop.layout.GroupLayout(TELA_ZERO_ZERO);
        TELA_ZERO_ZERO.setLayout(TELA_ZERO_ZEROLayout);
        TELA_ZERO_ZEROLayout.setHorizontalGroup(
            TELA_ZERO_ZEROLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 364, Short.MAX_VALUE)
        );
        TELA_ZERO_ZEROLayout.setVerticalGroup(
            TELA_ZERO_ZEROLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 662, Short.MAX_VALUE)
        );

        EscolhaModelo.add(TELA_ZERO_ZERO, "TELA_ZERO_ZERO");

        TELA_ZERO.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        TELA_ZERO.setAutoscrolls(true);
        TELA_ZERO.setDoubleBuffered(false);
        TELA_ZERO.setMaximumSize(new java.awt.Dimension(450, 450));
        TELA_ZERO.setMinimumSize(new java.awt.Dimension(253, 608));
        TELA_ZERO.setName("TELA_ZERO"); // NOI18N
        TELA_ZERO.setPreferredSize(new java.awt.Dimension(450, 450));
        TELA_ZERO.setRequestFocusEnabled(false);
        TELA_ZERO.setVerifyInputWhenFocusTarget(false);
        TELA_ZERO.setLayout(null);

        Escolha_0.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        Escolha_0.setText(" ESCOLHA O MODELO DO EQUIPAMENTO ");
        Escolha_0.setFocusable(false);
        Escolha_0.setName("Escolha_0"); // NOI18N
        Escolha_0.setRequestFocusEnabled(false);
        Escolha_0.setVerifyInputWhenFocusTarget(false);
        TELA_ZERO.add(Escolha_0);
        Escolha_0.setBounds(20, 10, 320, 22);

        zero.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        zero.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        zero.setText("0");
        zero.setBorder(new javax.swing.border.MatteBorder(null));
        zero.setEnabled(false);
        zero.setFocusable(false);
        zero.setInheritsPopupMenu(false);
        zero.setName("zero"); // NOI18N
        zero.setRequestFocusEnabled(false);
        zero.setVerifyInputWhenFocusTarget(false);
        TELA_ZERO.add(zero);
        zero.setBounds(150, 40, 60, 40);

        lbespinha_dorsal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbespinha_dorsal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/espinha-dorsal.png"))); // NOI18N
        lbespinha_dorsal.setToolTipText("");
        TELA_ZERO.add(lbespinha_dorsal);
        lbespinha_dorsal.setBounds(166, 120, 7, 460);

        lbeixo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo1);
        lbeixo1.setBounds(62, 120, 220, 7);

        lbeixo2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo2);
        lbeixo2.setBounds(62, 210, 220, 7);

        lbeixo3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo3);
        lbeixo3.setBounds(62, 310, 220, 7);

        lbeixo4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo4);
        lbeixo4.setBounds(62, 400, 220, 7);

        lbeixo5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo5);
        lbeixo5.setBounds(62, 480, 220, 7);

        lbeixo6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo6);
        lbeixo6.setBounds(62, 580, 220, 7);

        Label100.setMaximumSize(null);
        Label100.setMinimumSize(null);
        TELA_ZERO.add(Label100);
        Label100.setBounds(70, 80, 32, 96);

        Label101.setMaximumSize(null);
        Label101.setMinimumSize(null);
        TELA_ZERO.add(Label101);
        Label101.setBounds(100, 80, 32, 96);

        Label102.setMaximumSize(null);
        Label102.setMinimumSize(null);
        TELA_ZERO.add(Label102);
        Label102.setBounds(280, 80, 32, 96);

        Label103.setMaximumSize(null);
        Label103.setMinimumSize(null);
        TELA_ZERO.add(Label103);
        Label103.setBounds(230, 80, 32, 96);

        labelMensagem.setName("labelMensagem"); // NOI18N
        TELA_ZERO.add(labelMensagem);
        labelMensagem.setBounds(30, 710, 320, 70);
        labelMensagem.getAccessibleContext().setAccessibleName("labelMensagem");

        Label104.setMaximumSize(null);
        Label104.setMinimumSize(null);
        TELA_ZERO.add(Label104);
        Label104.setBounds(70, 160, 32, 96);

        Label105.setMaximumSize(null);
        Label105.setMinimumSize(null);
        TELA_ZERO.add(Label105);
        Label105.setBounds(100, 160, 32, 96);

        Label106.setMaximumSize(null);
        Label106.setMinimumSize(null);
        TELA_ZERO.add(Label106);
        Label106.setBounds(280, 160, 32, 96);

        Label107.setMaximumSize(null);
        Label107.setMinimumSize(null);
        TELA_ZERO.add(Label107);
        Label107.setBounds(230, 160, 32, 96);

        Label108.setMaximumSize(null);
        Label108.setMinimumSize(null);
        TELA_ZERO.add(Label108);
        Label108.setBounds(70, 260, 32, 96);

        Label109.setMaximumSize(null);
        Label109.setMinimumSize(null);
        TELA_ZERO.add(Label109);
        Label109.setBounds(100, 260, 32, 96);

        Label110.setMaximumSize(null);
        Label110.setMinimumSize(null);
        TELA_ZERO.add(Label110);
        Label110.setBounds(280, 260, 32, 96);

        Label111.setMaximumSize(null);
        Label111.setMinimumSize(null);
        TELA_ZERO.add(Label111);
        Label111.setBounds(230, 260, 32, 96);

        Label112.setMaximumSize(null);
        Label112.setMinimumSize(null);
        TELA_ZERO.add(Label112);
        Label112.setBounds(70, 350, 32, 96);

        Label113.setMaximumSize(null);
        Label113.setMinimumSize(null);
        TELA_ZERO.add(Label113);
        Label113.setBounds(100, 350, 32, 96);

        Label114.setMaximumSize(null);
        Label114.setMinimumSize(null);
        TELA_ZERO.add(Label114);
        Label114.setBounds(280, 350, 32, 96);

        Label115.setMaximumSize(null);
        Label115.setMinimumSize(null);
        TELA_ZERO.add(Label115);
        Label115.setBounds(230, 350, 32, 96);

        Label116.setMaximumSize(null);
        Label116.setMinimumSize(null);
        TELA_ZERO.add(Label116);
        Label116.setBounds(70, 430, 32, 96);

        Label117.setMaximumSize(null);
        Label117.setMinimumSize(null);
        TELA_ZERO.add(Label117);
        Label117.setBounds(100, 430, 32, 96);

        Label118.setMaximumSize(null);
        Label118.setMinimumSize(null);
        TELA_ZERO.add(Label118);
        Label118.setBounds(280, 430, 32, 96);

        Label119.setMaximumSize(null);
        Label119.setMinimumSize(null);
        TELA_ZERO.add(Label119);
        Label119.setBounds(230, 430, 32, 96);

        Label120.setMaximumSize(null);
        Label120.setMinimumSize(null);
        TELA_ZERO.add(Label120);
        Label120.setBounds(60, 540, 32, 96);

        Label121.setMaximumSize(null);
        Label121.setMinimumSize(null);
        TELA_ZERO.add(Label121);
        Label121.setBounds(90, 540, 32, 96);

        Label122.setMaximumSize(null);
        Label122.setMinimumSize(null);
        TELA_ZERO.add(Label122);
        Label122.setBounds(270, 530, 32, 96);

        Label123.setMaximumSize(null);
        Label123.setMinimumSize(null);
        TELA_ZERO.add(Label123);
        Label123.setBounds(220, 530, 32, 96);

        EscolhaModelo.add(TELA_ZERO, "TELA_ZERO");

        Frota.setText("Frota");
        Frota.setName("Frota"); // NOI18N

        txtFrota.setName("txtFrota"); // NOI18N

        placa.setText("Placa");
        placa.setName("Placa"); // NOI18N

        txtPlaca.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        txtPlaca.setName("Placa"); // NOI18N
        txtPlaca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPlacaKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPlacaKeyTyped(evt);
            }
        });

        numero_pneus.setText("QTD PNEUS");
        numero_pneus.setName("numero_pneus"); // NOI18N

        Qtd_numeroPneu.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        Qtd_numeroPneu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Qtd_numeroPneu.setEnabled(false);
        Qtd_numeroPneu.setName("Qtd_numeroPneu"); // NOI18N

        tipo_equipamento.setText("Tipo  de Equipamento");
        tipo_equipamento.setName("Tipo  de Equipamento"); // NOI18N

        cmbTipoVeiculo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTipoVeiculo.setName("Tipo  de Equipamento"); // NOI18N

        lbMedidaPneu.setText("Medida do Pneu");
        lbMedidaPneu.setName("Medida do Pneu"); // NOI18N

        cmbMedidaPneu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbMedidaPneu.setName("Medida do Pneu"); // NOI18N

        Tabela_Caminhoes.setPreferredSize(new java.awt.Dimension(900, 402));

        Tabela_Exibicao_veiculos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Tabela_Exibicao_veiculos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "FROTA", "PLACA", "TIPO DE VEICULO", "QTD PNEUS"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabela_Exibicao_veiculos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        Tabela_Exibicao_veiculos.setName("Tabela_Exibicao_veiculos"); // NOI18N
        Tabela_Exibicao_veiculos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tabela_Exibicao_veiculosMouseClicked(evt);
            }
        });
        Tabela_Caminhoes.setViewportView(Tabela_Exibicao_veiculos);

        Cadastrar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Cadastrar.setText("CADASTRAR");
        Cadastrar.setName("cadastrar"); // NOI18N
        Cadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CadastrarActionPerformed(evt);
            }
        });

        qtd_pneus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        qtd_pneus.setText("QTD DE PNEUS NECESSÁRIA: ");
        qtd_pneus.setName("qtd_"); // NOI18N

        qtd_veiculos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        qtd_veiculos.setText("QTD DE VEICULOS: ");
        qtd_veiculos.setName("exclui_veiculo"); // NOI18N

        Exclui_veiculos.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        Exclui_veiculos.setForeground(new java.awt.Color(255, 51, 51));
        Exclui_veiculos.setText("\"DUPLO CLIQUE PARA EXCLUIR O VEICULO\"");

        BUTTON_BOX.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        BUTTON_BOX.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                BUTTON_BOXVetoableChange(evt);
            }
        });
        BUTTON_BOX.setLayout(null);

        cod_zero.setText("COD.: 0");
        cod_zero.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_zero.setName("cod:0"); // NOI18N
        cod_zero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_zeroActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_zero);
        cod_zero.setBounds(0, 0, 60, 20);

        cod_um.setText("COD.: 01");
        cod_um.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_um.setPreferredSize(new java.awt.Dimension(44, 20));
        cod_um.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_umActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_um);
        cod_um.setBounds(0, 30, 60, 20);

        cod_dois.setText("COD.: 02");
        cod_dois.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_dois.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_doisActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_dois);
        cod_dois.setBounds(0, 60, 60, 20);

        cod_trez.setText("COD.: 03");
        cod_trez.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_trez.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_trezActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_trez);
        cod_trez.setBounds(0, 90, 60, 20);

        cod_quatro.setText("COD.: 04");
        cod_quatro.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_quatro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_quatroActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_quatro);
        cod_quatro.setBounds(0, 120, 60, 20);

        cod_cinco.setText("COD.: 05");
        cod_cinco.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_cinco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_cincoActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_cinco);
        cod_cinco.setBounds(0, 150, 60, 20);

        cod_seis.setText("COD.: 06");
        cod_seis.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_seis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_seisActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_seis);
        cod_seis.setBounds(0, 180, 60, 20);

        cod_sete.setText("COD.: 07");
        cod_sete.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_sete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_seteActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_sete);
        cod_sete.setBounds(0, 210, 60, 20);

        cod_oito.setText("COD.: 08");
        cod_oito.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_oito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_oitoActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_oito);
        cod_oito.setBounds(0, 240, 60, 20);

        cod_nove.setText("COD.: 9");
        cod_nove.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_nove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_noveActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_nove);
        cod_nove.setBounds(0, 270, 60, 20);

        cod_dez.setText("COD.: 10");
        cod_dez.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_dez.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_dezActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_dez);
        cod_dez.setBounds(0, 300, 60, 20);

        cod_onze.setText("COD.: 11");
        cod_onze.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_onze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_onzeActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_onze);
        cod_onze.setBounds(0, 330, 60, 20);

        cod_doze.setText("COD.: 12");
        cod_doze.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_doze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_dozeActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_doze);
        cod_doze.setBounds(0, 360, 60, 20);

        cod_treze.setText("COD.: 13");
        cod_treze.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_treze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_trezeActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_treze);
        cod_treze.setBounds(0, 390, 60, 20);

        cod_quatorze.setText("COD.: 14");
        cod_quatorze.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_quatorze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_quatorzeActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_quatorze);
        cod_quatorze.setBounds(0, 420, 60, 20);

        cod_quinze.setText("COD.: 15");
        cod_quinze.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_quinze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_quinzeActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_quinze);
        cod_quinze.setBounds(0, 450, 60, 20);

        cod_desesseis.setText("COD.: 16");
        cod_desesseis.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_desesseis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_desesseisActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_desesseis);
        cod_desesseis.setBounds(0, 480, 60, 20);

        cod_desessete.setText("COD.: 17");
        cod_desessete.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cod_desessete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cod_desesseteActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(cod_desessete);
        cod_desessete.setBounds(0, 510, 60, 20);

        dolly.setText("DOLLY");
        dolly.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        dolly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dollyActionPerformed(evt);
            }
        });
        BUTTON_BOX.add(dolly);
        dolly.setBounds(0, 540, 60, 20);

        lb_carreta.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lb_carreta.setForeground(new java.awt.Color(255, 0, 0));
        lb_carreta.setText("QUAL É A POSIÇÃO DESSA CARRETA??");

        lbPosicao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbPosicao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbPosicao.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbPosicao.setEnabled(false);

        cbposicao_carreta.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cbposicao_carreta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "1", "2" }));
        cbposicao_carreta.setToolTipText("");

        btAdd_Pneu.setBackground(new java.awt.Color(0, 204, 0));
        btAdd_Pneu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btAdd_Pneu.setText("ADD + PNEUS");
        btAdd_Pneu.setName("cadastrar"); // NOI18N

        fechar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/close.png"))); // NOI18N
        fechar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fecharMouseClicked(evt);
            }
        });

        medidaPneu.setPreferredSize(new java.awt.Dimension(900, 402));

        Tabela_medidaPneu.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Tabela_medidaPneu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Tabela_medidaPneu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MEDIDA DO PNEU"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabela_medidaPneu.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        Tabela_medidaPneu.setName("Tabela_Exibicao_veiculos"); // NOI18N
        medidaPneu.setViewportView(Tabela_medidaPneu);
        if (Tabela_medidaPneu.getColumnModel().getColumnCount() > 0) {
            Tabela_medidaPneu.getColumnModel().getColumn(0).setResizable(false);
        }

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(lbPosicao, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 57, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(104, 104, 104))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(lb_carreta)
                                .add(44, 44, 44)))
                        .add(cbposicao_carreta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                            .add(54, 54, 54)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(Frota)
                                .add(txtFrota, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(layout.createSequentialGroup()
                                    .add(5, 5, 5)
                                    .add(tipo_equipamento)))
                            .add(18, 18, 18)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(placa, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(txtPlaca, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(176, 176, 176)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(numero_pneus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(Qtd_numeroPneu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(layout.createSequentialGroup()
                            .add(53, 53, 53)
                            .add(cmbTipoVeiculo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 300, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(layout.createSequentialGroup()
                            .add(69, 69, 69)
                            .add(Cadastrar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(76, 76, 76)
                            .add(cmbMedidaPneu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(layout.createSequentialGroup()
                            .add(266, 266, 266)
                            .add(lbMedidaPneu))
                        .add(layout.createSequentialGroup()
                            .add(40, 40, 40)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(layout.createSequentialGroup()
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(layout.createSequentialGroup()
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                                .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                                    .add(qtd_veiculos)
                                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .add(Veiculos_Cadastrados, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                .add(qtd_pneus))
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                            .add(Pneus_necessarios, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .add(Exclui_veiculos))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 87, Short.MAX_VALUE)
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                        .add(btAdd_Pneu, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(medidaPneu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                    .add(60, 60, 60))
                                .add(Tabela_Caminhoes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))))
                .add(18, 18, 18)
                .add(BUTTON_BOX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(4, 4, 4)
                .add(EscolhaModelo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 370, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 81, Short.MAX_VALUE)
                .add(fechar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(EscolhaModelo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 668, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(4, 4, 4)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(layout.createSequentialGroup()
                                                .add(Frota)
                                                .add(4, 4, 4)
                                                .add(txtFrota, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .add(8, 8, 8)
                                                .add(tipo_equipamento))
                                            .add(layout.createSequentialGroup()
                                                .add(placa)
                                                .add(6, 6, 6)
                                                .add(txtPlaca, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                            .add(layout.createSequentialGroup()
                                                .add(6, 6, 6)
                                                .add(numero_pneus)
                                                .add(6, 6, 6)
                                                .add(Qtd_numeroPneu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                        .add(6, 6, 6)
                                        .add(cmbTipoVeiculo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(6, 6, 6)
                                        .add(lbMedidaPneu)
                                        .add(7, 7, 7)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(Cadastrar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                            .add(layout.createSequentialGroup()
                                                .add(3, 3, 3)
                                                .add(cmbMedidaPneu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                        .add(16, 16, 16)
                                        .add(Tabela_Caminhoes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 170, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(cbposicao_carreta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                            .add(layout.createSequentialGroup()
                                                .add(lb_carreta)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(lbPosicao, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                        .add(37, 37, 37)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(layout.createSequentialGroup()
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                    .add(qtd_veiculos)
                                                    .add(Veiculos_Cadastrados, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                .add(14, 14, 14)
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                    .add(qtd_pneus)
                                                    .add(Pneus_necessarios, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                .add(44, 44, 44)
                                                .add(Exclui_veiculos))
                                            .add(medidaPneu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 146, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                    .add(BUTTON_BOX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 560, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btAdd_Pneu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(fechar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleName("TelaCadastroVeiculos");

        setSize(new java.awt.Dimension(1110, 679));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cod_zeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_zeroActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        // --- CONFIGURAÇÕES DO CHASSI ---
        TipoEixo[] tipos = {TipoEixo.SIMPLES, TipoEixo.DUPLO, TipoEixo.DUPLO};
        boolean[] visibilidade = {true, true, true, false, false, false}; // 3 eixos visíveis
        int espacamento = 120; // Controla o comprimento da espinha dorsal
        AlinhamentoVertical alinhamento = AlinhamentoVertical.TOPO; // Opções: TOPO, CENTRO, BASE
        int[] deslocamentos = {-30, -10, -10}; // Ajuste fino da posição dos pneus para cada eixo visível
        int[] ajustesVerticais = {0, 0, 100}; // Ajustes: {BASE, CENTRO, TOPO}
        int[] largurasEixos = {140, 280, 280};

        // Para ajuste fino, descomente a linha abaixo e troque 'null' por 'posicoesEixos' na chamada.
        // int[] posicoesEixos = {0, 0, 0}; // Ajuste em pixels para cada eixo visível.
        desenharChassi(tipos, visibilidade, espacamento, alinhamento, deslocamentos, ajustesVerticais, largurasEixos, null);

        SwingUtilities.invokeLater(() -> {
            labelMensagem.setText("<html>MODELO PARA ENGATE E DESENGATE<br>DE CARRETAS, PARA OUTROS UTILIZE<br>O MODELO 16</html>");
            labelMensagem.setForeground(java.awt.Color.RED);
            labelMensagem.setFont(new java.awt.Font("tahoma", java.awt.Font.BOLD, 12));

            java.awt.Dimension tamanhoIdeal = labelMensagem.getPreferredSize();
            int larguraLabel = tamanhoIdeal.width;
            int alturaLabel = tamanhoIdeal.height;

            int yLabel = 550;
            int xLabel = 70;

            if (yLabel < 0) {
                yLabel = 0;
            }
            if (xLabel < 0) {
                xLabel = 0;
            }

            labelMensagem.setBounds(xLabel, yLabel, larguraLabel, alturaLabel);
            TELA_ZERO.add(labelMensagem);
            labelMensagem.setVisible(true);
            TELA_ZERO.setComponentZOrder(labelMensagem, 0);
            TELA_ZERO.repaint();
        });

        atualizarNumeroModelo(0);
    }//GEN-LAST:event_cod_zeroActionPerformed

    private void cod_umActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_umActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        // --- CONFIGURAÇÕES DO CHASSI ---
        TipoEixo[] tipos = {TipoEixo.SIMPLES, TipoEixo.SIMPLES};
        boolean[] visibilidade = {true, true, false, false, false, false};
        int espacamento = 287; //AUMENTA O TAMANHO DA ESPINHA DORSAL

        AlinhamentoVertical alinhamento = AlinhamentoVertical.TOPO;
        int[] deslocamentos = {-30, -30, -30}; // Ajuste fino da posição dos pneus para cada eixo visível
        int[] ajustesVerticais = {0, 0, 100}; // Ajustes: {BASE, CENTRO, TOPO}
        int[] largurasEixos = {150, 150}; // TAMANHO VERTICAL DOS EIXOS

        // Para ajuste fino, descomente a linha abaixo e troque 'null' por 'posicoesEixos' na chamada.
        // int[] posicoesEixos = {0, 0, 0}; // Ajuste em pixels para cada eixo visível.
        desenharChassi(tipos, visibilidade, espacamento, alinhamento, deslocamentos, ajustesVerticais, largurasEixos, null);
        atualizarNumeroModelo(1);

    }//GEN-LAST:event_cod_umActionPerformed

    private void cod_doisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_doisActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(2);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(2);

    }//GEN-LAST:event_cod_doisActionPerformed

    private void cod_trezActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_trezActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(3);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(3);

    }//GEN-LAST:event_cod_trezActionPerformed

    private void cod_quatroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_quatroActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(4);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(4);
        selecionarTipoVeiculoPorId(4);

    }//GEN-LAST:event_cod_quatroActionPerformed

    private void cod_cincoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_cincoActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(5);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(5);


    }//GEN-LAST:event_cod_cincoActionPerformed

    private void cod_seisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_seisActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(6);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(6);

    }//GEN-LAST:event_cod_seisActionPerformed

    private void cod_seteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_seteActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(7);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }

        SwingUtilities.invokeLater(() -> {
            labelMensagem.setText("<html>MODELO APENAS PARA CARRETAS, PARA<br>OUTROS, UTILIZE O MODELO 17.</html>");
            labelMensagem.setForeground(java.awt.Color.RED);
            labelMensagem.setFont(new java.awt.Font("tahoma", java.awt.Font.BOLD, 12));

            java.awt.Dimension tamanhoIdeal = labelMensagem.getPreferredSize();
            int larguraLabel = tamanhoIdeal.width;
            int alturaLabel = tamanhoIdeal.height;

            int yLabel = 550;
            int xLabel = 70;

            if (yLabel < 0) {
                yLabel = 0;
            }
            if (xLabel < 0) {
                xLabel = 0;
            }

            labelMensagem.setBounds(xLabel, yLabel, larguraLabel, alturaLabel);
                TELA_ZERO.add(labelMensagem);
            labelMensagem.setVisible(true);
            TELA_ZERO.setComponentZOrder(labelMensagem, 0);
            TELA_ZERO.repaint();
        });

        atualizarNumeroModelo(7);
        selecionarTipoVeiculoPorId(7);
    }//GEN-LAST:event_cod_seteActionPerformed

    private void cod_oitoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_oitoActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(8);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(8);
        selecionarTipoVeiculoPorId(8);

    }//GEN-LAST:event_cod_oitoActionPerformed

    private void cod_noveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_noveActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(9);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(9);

    }//GEN-LAST:event_cod_noveActionPerformed

    private void cod_dezActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_dezActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(10);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(10);

    }//GEN-LAST:event_cod_dezActionPerformed

    private void cod_onzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_onzeActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(11);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(11);

    }//GEN-LAST:event_cod_onzeActionPerformed

    private void cod_dozeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_dozeActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(12);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(12);

    }//GEN-LAST:event_cod_dozeActionPerformed

    private void cod_trezeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_trezeActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(13);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(13);

    }//GEN-LAST:event_cod_trezeActionPerformed

    private void cod_quatorzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_quatorzeActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(14);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(14);

    }//GEN-LAST:event_cod_quatorzeActionPerformed

    private void cod_quinzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_quinzeActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(15);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(15);

    }//GEN-LAST:event_cod_quinzeActionPerformed

    private void cod_desesseisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_desesseisActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(16);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(16);

        SwingUtilities.invokeLater(() -> {
            labelMensagem.setText("<html>MODELO PARA CAMINHŐES QUE NÃO<br>TENHAM ENGATES DE CARRETAS:<br> BETONEIRAS, BASCULANTES, MUNCKS E OUTROS.</html>");
            labelMensagem.setForeground(java.awt.Color.RED);
            labelMensagem.setFont(new java.awt.Font("tahoma", java.awt.Font.BOLD, 12));

            java.awt.Dimension tamanhoIdeal = labelMensagem.getPreferredSize();
            int larguraLabel = tamanhoIdeal.width;
            int alturaLabel = tamanhoIdeal.height;

            int yLabel = 620;
            int xLabel = 50;

            if (yLabel < 0) {
                yLabel = 0;
            }
            if (xLabel < 0) {
                xLabel = 0;
            }

            labelMensagem.setBounds(xLabel, yLabel, larguraLabel, alturaLabel);
            TELA_ZERO.add(labelMensagem);
            labelMensagem.setVisible(true);
            TELA_ZERO.setComponentZOrder(labelMensagem, 0);
            TELA_ZERO.repaint();
        });

    }//GEN-LAST:event_cod_desesseisActionPerformed

    private void cod_desesseteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cod_desesseteActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(17);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }
        atualizarNumeroModelo(17);

    }//GEN-LAST:event_cod_desesseteActionPerformed

    private void dollyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dollyActionPerformed
        if (veiculoSelecionado == null) {
            limparCampos();
        }
        TELA_ZERO.remove(labelMensagem);
        TELA_ZERO.repaint();

        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(18);
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento, config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
        }

        SwingUtilities.invokeLater(() -> {
            labelMensagem.setText("<html>MODELO APENAS PARA DOLLY, PARA<br>OUTROS, UTILIZE O MODELO 7.</html>");
            labelMensagem.setForeground(java.awt.Color.RED);
            labelMensagem.setFont(new java.awt.Font("tahoma", java.awt.Font.BOLD, 12));

            java.awt.Dimension tamanhoIdeal = labelMensagem.getPreferredSize();
            int larguraLabel = tamanhoIdeal.width;
            int alturaLabel = tamanhoIdeal.height;

            int yLabel = 620;
            int xLabel = 70;

            if (yLabel < 0) {
                yLabel = 0;
            }
            if (xLabel < 0) {
                xLabel = 0;
            }

            labelMensagem.setBounds(xLabel, yLabel, larguraLabel, alturaLabel);
             TELA_ZERO.add(labelMensagem);
            labelMensagem.setVisible(true);
            TELA_ZERO.setComponentZOrder(labelMensagem, 0);
            TELA_ZERO.repaint();
        });

        atualizarNumeroModelo(18);
    }//GEN-LAST:event_dollyActionPerformed

    private void BUTTON_BOXVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_BUTTON_BOXVetoableChange
// TODO add your handling code here:
    }//GEN-LAST:event_BUTTON_BOXVetoableChange


    private void txtPlacaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPlacaKeyPressed

    }//GEN-LAST:event_txtPlacaKeyPressed

    private void txtPlacaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPlacaKeyTyped
        // Obtém o caractere digitado e converte para caixa alta
        evt.setKeyChar(Character.toUpperCase(evt.getKeyChar()));

        // Obtém o texto atual do campo
        String text = txtPlaca.getText().toUpperCase();

        // Limita o tamanho máximo (10 caracteres: XXX - 0X00 ou XXX - 0000)
        if (text.length() >= 10) {
            evt.consume(); // Impede a inserção de mais caracteres
            return;
        }

        char c = evt.getKeyChar();

        // Permite apenas letras, números, '-' e espaço
        if (!Character.isLetterOrDigit(c) && c != '-' && c != ' ') {
            evt.consume(); // Impede caracteres inválidos
            return;
        }

        // Verifica a posição dos caracteres
        int pos = text.length();

        // Primeiras 3 posições: Apenas letras
        if (pos < 3 && !Character.isLetter(c)) {
            evt.consume(); // Impede inserção de números ou outros caracteres
        } // Se o texto tiver 3 caracteres, adicionar o espaço, hífen e espaço automaticamente
        else if (pos == 3) {
            // Depois de 3 letras, insere automaticamente " - " (espaço, hífen, espaço)
            txtPlaca.setText(text + " - ");
            // Mover o cursor para a posição 6
            SwingUtilities.invokeLater(() -> txtPlaca.setCaretPosition(6));
            evt.consume(); // Impede o caractere digitado (não permite que a tecla digitada seja inserida)
        } // Posição 6: Espaço
        else if (pos == 5 && c != ' ') {
            txtPlaca.setText(text + " "); // Adiciona o espaço automaticamente
            SwingUtilities.invokeLater(() -> txtPlaca.setCaretPosition(7)); // Coloca o cursor na posição 7
            evt.consume(); // Impede o caractere digitado
        } // Posição 7: Somente números
        else if (pos == 6 && !Character.isDigit(c)) {
            evt.consume(); // Impede a inserção de caracteres não numéricos
        } // Posição 8: Aceita letra ou número
        else if (pos == 7 && !(Character.isDigit(c) || Character.isLetter(c))) {
            evt.consume(); // Permite apenas letras ou números
        } // Posições 9 e 10: Somente números
        else if (pos >= 8 && !Character.isDigit(c)) {
            evt.consume(); // Permite apenas números
        }
    }//GEN-LAST:event_txtPlacaKeyTyped

    private void mostrarVeiculoSelecionado(int selectedRow) {
        if (selectedRow == -1) {
            return;
        }
        int modelRow = Tabela_Exibicao_veiculos.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= listaDeVeiculos.size()) {
            return;
        }
        Veiculo veiculoClicado = this.listaDeVeiculos.get(modelRow);

        // Se estiver em modo de edição e clicar em uma linha diferente, limpa a tela.
        if (veiculoSelecionado != null && veiculoClicado.getID() != veiculoSelecionado.getID()) {
            limparCampos();
            CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
            layout.show(EscolhaModelo, "TELA_ZERO_ZERO");
            return;
        }

        // Exibe o chassi do veículo clicado no painel (para qualquer veículo)
        CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
        layout.show(EscolhaModelo, "TELA_ZERO");

        VehicleConfig config = vehicleConfigs.get(veiculoClicado.getID_CONFIG_FK());
        if (config != null) {
            desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                    config.alinhamento,
                    config.deslocamentos,
                    config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
            atualizarNumeroModelo(veiculoClicado.getID_CONFIG_FK());

            // Lógica para exibir mensagens específicas
            switch (veiculoClicado.getID_CONFIG_FK()) {
                case 0:
                    mostrarMensagem("<html>MODELO PARA ENGATE E DESENGATE<br>DE CARRETAS, PARA OUTROS UTILIZE<br>O MODELO 16</html>", 550, 70);
                    break;
                case 7:
                    mostrarMensagem("<html>MODELO APENAS PARA CARRETAS, PARA<br>OUTROS, UTILIZE O MODELO 17.</html>", 550, 70);
                    break;
                case 16:
                    mostrarMensagem("<html>MODELO PARA CAMINHŐES QUE NÃO<br>TENHAM ENGATES DE CARRETAS:<br> BETONEIRAS, BASCULANTES, MUNCKS E OUTROS.</html>", 620, 50);
                    break;
                case 18: // Dolly
                    mostrarMensagem("<html>MODELO APENAS PARA DOLLY, PARA<br>OUTROS, UTILIZE O MODELO 7.</html>", 620, 70);
                    break;
                default:
                    labelMensagem.setVisible(false);
                    TELA_ZERO.remove(labelMensagem);
                    TELA_ZERO.repaint();
                    break;
            }
        } else {
            // Se não houver configuração, limpa o chassi
            desenharChassi(new TipoEixo[]{}, new boolean[]{}, 0, AlinhamentoVertical.CENTRO, new int[]{}, new int[]{}, new int[]{}, null);
            atualizarNumeroModelo(0);
            labelMensagem.setVisible(false);
            TELA_ZERO.remove(labelMensagem);
            TELA_ZERO.repaint();
        }

        // Lógica para exibir/ocultar labels de posição da carreta
        lb_carreta.setVisible(false);
        cbposicao_carreta.setVisible(false);
        lbPosicao.setVisible(false);

        if (carretaIds.contains(veiculoClicado.getID_CONFIG_FK())) {
            lb_carreta.setVisible(true);
            Integer posicao = veiculoClicado.getPosicaoCarreta();
            if (posicao != null && posicao > 0) {
                lbPosicao.setText(String.valueOf(posicao));
                lbPosicao.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
                lbPosicao.setVisible(true);
            } else {
                // Se não tem posição, não mostra o lbPosicao, mas mostra o lb_carreta
                lbPosicao.setVisible(false);
            }
        }
    }

    private void Tabela_Exibicao_veiculosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Tabela_Exibicao_veiculosMouseClicked
        int selectedRow = Tabela_Exibicao_veiculos.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        if (evt.getClickCount() == 1) {
            mostrarVeiculoSelecionado(selectedRow);
        } else if (evt.getClickCount() == 2) {
            int modelRow = Tabela_Exibicao_veiculos.convertRowIndexToModel(selectedRow);
            Veiculo veiculoClicado = this.listaDeVeiculos.get(modelRow);
            // Se clicar duas vezes no mesmo veículo que está sendo editado, sai do modo de edição.
            if (veiculoSelecionado != null && veiculoClicado.getID() == veiculoSelecionado.getID()) {
                limparCampos();
                CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
                layout.show(EscolhaModelo, "TELA_ZERO_ZERO");
                return;
            }

            // Entra em modo de edição completo
            this.veiculoSelecionado = veiculoClicado;
            Cadastrar.setText("ATUALIZAR");
            txtFrota.setEditable(false);

            txtFrota.setText(veiculoSelecionado.getFROTA());
            txtPlaca.setText(veiculoSelecionado.getPLACA());
            Qtd_numeroPneu.setText(String.valueOf(veiculoSelecionado.getQTD_PNEUS()));

            String tipoVeiculoItem = veiculoSelecionado.getID_CONFIG_FK() + " - ";
            for (int i = 0; i < cmbTipoVeiculo.getItemCount(); i++) {
                if (cmbTipoVeiculo.getItemAt(i).startsWith(tipoVeiculoItem)) {
                    cmbTipoVeiculo.setSelectedIndex(i);
                    break;
                }
            }
            cmbMedidaPneu.setSelectedItem(veiculoSelecionado.getMEDIDA_PNEU());

            int idConfig = veiculoSelecionado.getID_CONFIG_FK();
            Integer posicao = veiculoSelecionado.getPosicaoCarreta();

            lb_carreta.setVisible(false);
            cbposicao_carreta.setVisible(false);
            lbPosicao.setVisible(false);

            if (carretaIds.contains(idConfig)) {
                lb_carreta.setVisible(true);
                if (posicao != null && posicao > 0) {
                    lbPosicao.setText(String.valueOf(posicao));
                    lbPosicao.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
                    lbPosicao.setVisible(true);
                } else {
                    cbposicao_carreta.setVisible(true);
                }
            }

            labelMensagem.setVisible(false);
            TELA_ZERO.remove(labelMensagem);
            TELA_ZERO.repaint();

            CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
            layout.show(EscolhaModelo, "TELA_ZERO");

            VehicleConfig config = vehicleConfigs.get(idConfig);
            if (config != null) {
                desenharChassi(config.tipos, config.visibilidade, config.espacamento,
                        config.alinhamento,
                        config.deslocamentos,
                        config.ajustesVerticais, config.largurasEixos, config.posicoesEixos);
                atualizarNumeroModelo(idConfig);

                switch (idConfig) {
                    case 0:
                        mostrarMensagem("<html>MODELO PARA ENGATE E DESENGATE<br>DE CARRETAS, PARA OUTROS UTILIZE<br>O MODELO 16</html>", 550, 70);
                        break;
                    case 7:
                        mostrarMensagem("<html>MODELO APENAS PARA CARRETAS, PARA<br>OUTROS, UTILIZE O MODELO 17.</html>", 550, 70);
                        break;
                    case 16:
                        mostrarMensagem("<html>MODELO PARA CAMINHŐES QUE NÃO<br>TENHAM ENGATES DE CARRETAS:<br> BETONEIRAS, BASCULANTES, MUNCKS E OUTROS.</html>", 620, 50);
                        break;
                    case 18: // Dolly
                        mostrarMensagem("<html>MODELO APENAS PARA DOLLY, PARA<br>OUTROS, UTILIZE O MODELO 7.</html>", 620, 70);
                        break;
                }
            } else {
                desenharChassi(new TipoEixo[]{}, new boolean[]{}, 0, AlinhamentoVertical.CENTRO, new int[]{}, new int[]{}, new int[]{}, null);
                atualizarNumeroModelo(0);
                labelMensagem.setVisible(false);
                TELA_ZERO.remove(labelMensagem);
                TELA_ZERO.repaint();
            }
        }
    }//GEN-LAST:event_Tabela_Exibicao_veiculosMouseClicked

    private void fecharMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fecharMouseClicked
        dispose();
    }//GEN-LAST:event_fecharMouseClicked

    private void CadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CadastrarActionPerformed
     
    if (this.veiculoSelecionado == null && txtFrota.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "O campo 'Frota' é obrigatório.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
        txtFrota.requestFocusInWindow();
        return;
    }
    if (txtPlaca.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "O campo 'Placa' é obrigatório.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
        txtPlaca.requestFocusInWindow();
        return;
    }
    if (cmbTipoVeiculo.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, "Selecione um Tipo de Equipamento.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
        cmbTipoVeiculo.requestFocusInWindow();
        return;
    }
    if (cmbMedidaPneu.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, "Selecione uma Medida de Pneu.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
        cmbMedidaPneu.requestFocusInWindow();
        return;
    }

    // --- ETAPA 2: COLETA E CONVERSÃO SEGURA DOS DADOS ---
    String frota = txtFrota.getText().trim();
    String placa = txtPlaca.getText().trim().toUpperCase();
    String medidaPneu = cmbMedidaPneu.getSelectedItem().toString();
    int idConfigSelecionado = Integer.parseInt(cmbTipoVeiculo.getSelectedItem().toString().split(" - ")[0]);

    Integer posicaoCarreta = null;
    if (cbposicao_carreta.isVisible()) {
        if (cbposicao_carreta.getSelectedIndex() <= 0) { // Valida se o item " " foi selecionado
            JOptionPane.showMessageDialog(this, "Por favor, selecione a posição da carreta.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
            return;
        }
        posicaoCarreta = Integer.valueOf(cbposicao_carreta.getSelectedItem().toString());
    }
    
    // Validação Segura da Quantidade de Pneus
    int qtdPneusCorreta;
    String textoQtdPneus = Qtd_numeroPneu.getText().trim();
    if (textoQtdPneus.isEmpty() || !textoQtdPneus.matches("\\d+") || "0".equals(textoQtdPneus)) {
        JOptionPane.showMessageDialog(this, "Quantidade de pneus inválida. Selecione um modelo de veículo para definir a quantidade.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        return;
    }
    qtdPneusCorreta = Integer.parseInt(textoQtdPneus);


    // --- ETAPA 3: LÓGICA DE DECISÃO: CADASTRAR OU ATUALIZAR ---
    if (this.veiculoSelecionado == null) {
        // --- MODO CADASTRO ---
        Veiculo novoVeiculo = new Veiculo();
        novoVeiculo.setFROTA(frota);
        novoVeiculo.setPLACA(placa);
        novoVeiculo.setID_CONFIG_FK(idConfigSelecionado);
        novoVeiculo.setQTD_PNEUS(qtdPneusCorreta); // <--- USA O VALOR VALIDADO
        novoVeiculo.setDATA_CADASTRO(LocalDate.now());
        novoVeiculo.setMEDIDA_PNEU(medidaPneu);
        novoVeiculo.setSTATUS_VEICULO("ATIVO");
        novoVeiculo.setPosicaoCarreta(posicaoCarreta);

        if (veiculoDAO.salvarVeiculoCompleto(novoVeiculo)) {
            JOptionPane.showMessageDialog(this, "Veículo cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar o veículo.", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }

    } else {
        // --- MODO ATUALIZAÇÃO ---
        this.veiculoSelecionado.setPLACA(placa);
        this.veiculoSelecionado.setID_CONFIG_FK(idConfigSelecionado);
        this.veiculoSelecionado.setMEDIDA_PNEU(medidaPneu);
        // A quantidade de pneus de um veículo não muda, então não precisa de setQTD_PNEUS aqui.
        // A posição pode mudar:
        this.veiculoSelecionado.setPosicaoCarreta(posicaoCarreta);
       
        if (veiculoDAO.atualizarVeiculo(this.veiculoSelecionado)) {
            JOptionPane.showMessageDialog(this, "Veículo atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar o veículo.", "Erro de Atualização", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- ATUALIZA A TELA INDEPENDENTEMENTE DO MODO ---
    limparCampos();
    atualizarTabelaVeiculos();
    atualizarContagemTotalPneus();
    atualizarContagemDeVeiculos();

    // Volta para a tela inicial para forçar a seleção de um novo veículo
    CardLayout layout = (CardLayout) EscolhaModelo.getLayout();
    layout.show(EscolhaModelo, "TELA_ZERO_ZERO");
   

    }//GEN-LAST:event_CadastrarActionPerformed

    private void txtFrotaFocusLost(java.awt.event.FocusEvent evt) {
        String frota = txtFrota.getText().trim();

        if (!frota.isEmpty()) {
            boolean existe = veiculoDAO.frotaExiste(frota);
            if (existe) {
                String mensagem = "<html><b><font color='red'>ATENÇÃO: A FROTA NÚMERO '" + frota + "' JÁ ESTÁ CADASTRADA.</font></b></html>";

                JOptionPane.showMessageDialog(this,
                        mensagem,
                        "FROTA DUPLICADA",
                        JOptionPane.WARNING_MESSAGE);

                txtFrota.setText("");
                txtFrota.requestFocusInWindow();
            }
        }
    }

    private void txtPlacaFocusLost(java.awt.event.FocusEvent evt) {
        String placa = txtPlaca.getText().trim();
        if (!placa.isEmpty()) {
            boolean existe = veiculoDAO.placaExiste(placa);
            if (existe) {
                String mensagem = "<html><b><font color='red'>ATENÇÃO: A PLACA '" + placa + "' JÁ ESTÁ CADASTRADA.</font></b></html>";
                JOptionPane.showMessageDialog(this,
                        mensagem,
                        "PLACA DUPLICADA",
                        JOptionPane.WARNING_MESSAGE);
                txtPlaca.setText("");
                txtPlaca.requestFocusInWindow();
            }
        }
    }

    private void txtFrotaKeyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();

        if (!Character.isDigit(c)) {
            evt.consume(); // Ignora o evento, impedindo que o caractere seja inserido
        }
    }

    private void limparCampos() {
        txtFrota.setText("");
        txtPlaca.setText("");
        cmbTipoVeiculo.setSelectedIndex(0);
        cmbMedidaPneu.setSelectedIndex(-1); // Limpa a seleção
        Qtd_numeroPneu.setText("0");

        // Reseta o modo de edição
        this.veiculoSelecionado = null;
        Cadastrar.setText("CADASTRAR");
        txtFrota.setEditable(true); // Garante que a frota seja editável para novos cadastros

        // Esconde e reseta os campos de posição da carreta
        gerenciarVisibilidadePosicao();
        cbposicao_carreta.setSelectedIndex(0);
    }

    private void carregarTiposVeiculo() {
        List<TipoConfiguracao> tipos = tipoConfiguracaoDAO.listarTodos();
        cmbTipoVeiculo.removeAllItems(); // Limpa itens existentes
        cmbTipoVeiculo.addItem("Selecione um tipo..."); // Item default
        for (TipoConfiguracao tipo : tipos) {
            cmbTipoVeiculo.addItem(tipo.getIdConfig() + " - " + tipo.getNomeConfig());
        }
    }

    private void carregarMedidasPneu() {
        List<String> medidas = medidaPneuDAO.listarNomes();
        cmbMedidaPneu.removeAllItems(); // Limpa itens existentes
        cmbMedidaPneu.addItem("Selecione uma medida..."); // Item default
        for (String medida : medidas) {
            cmbMedidaPneu.addItem(medida);
        }
    }

    private void loadVehicleConfigs() {
        // COD.: 0
        vehicleConfigs.put(0, new VehicleConfig(
                new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.DUPLO, TipoEixo.DUPLO},
                new boolean[]{true, true, true, false, false, false}, // 3 eixos visíveis
                120, // espacamento
                AlinhamentoVertical.TOPO,
                new int[]{-30, -10, -10}, // deslocamentos
                new int[]{100, 0, 100}, // ajustesVerticais
                new int[]{140, 280, 280}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 1
        vehicleConfigs.put(1, new VehicleConfig(
                new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES},
                new boolean[]{true, true, false, false, false, false},
                287, // espacamento
                AlinhamentoVertical.TOPO,
                new int[]{-30, -30, -30}, // deslocamentos
                new int[]{0, 0, 100}, // ajustesVerticais
                new int[]{150, 150}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 2
        vehicleConfigs.put(2, new VehicleConfig(
                new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES},
                new boolean[]{true, false, true, false, false, true},
                200, // espacamento
                AlinhamentoVertical.CENTRO,
                new int[]{-30, -30, -30}, // deslocamentos
                new int[]{0, 0, 0}, // ajustesVerticais
                new int[]{150, 150, 150}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 3
        vehicleConfigs.put(3, new VehicleConfig(
                new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.DUPLO},
                new boolean[]{true, false, false, false, false, true},
                300, // espacamento
                AlinhamentoVertical.BASE,
                new int[]{-30, -30, -30}, // deslocamentos
                new int[]{50, 0, 0}, // ajustesVerticais
                new int[]{150, 220}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 4
        vehicleConfigs.put(4, new VehicleConfig(
                new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO},
                new boolean[]{true, true, true, true, false, false},
                100, // espacamento
                AlinhamentoVertical.TOPO,
                new int[]{-30, -30, -30, -30}, // deslocamentos
                new int[]{0, 0, 110}, // ajustesVerticais
                new int[]{230, 230, 230, 230}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 5
        vehicleConfigs.put(5, new VehicleConfig(
                new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES},
                new boolean[]{true, true, true, true, false, false},
                100, // espacamento
                AlinhamentoVertical.CENTRO,
                new int[]{-30, -30, -30, -30}, // deslocamentos
                new int[]{0, 80, 0}, // ajustesVerticais
                new int[]{150, 150, 150, 150}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 6
        vehicleConfigs.put(6, new VehicleConfig(
                new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.DUPLO, TipoEixo.DUPLO},
                new boolean[]{true, true, false, false, true, true},
                135, // espacamento
                AlinhamentoVertical.BASE,
                new int[]{-30, -30, -30, -30}, // deslocamentos
                new int[]{30, 0, 0}, // ajustesVerticais
                new int[]{150, 150, 280, 280}, // largurasEixos
                new int[]{50, 20, 0, 0, 70, 50} // posicoesEixos
        ));

        // COD.: 7
        vehicleConfigs.put(7, new VehicleConfig(
                new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO},
                new boolean[]{true, true, false, false, false, false},
                100, // espacamento
                AlinhamentoVertical.TOPO,
                new int[]{-30, -30, -30}, // deslocamentos
                new int[]{0, 0, 100}, // ajustesVerticais
                new int[]{220, 220}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 8
        vehicleConfigs.put(8, new VehicleConfig(
                new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO},
                new boolean[]{true, true, true, false, false, false},
                90, // espacamento
                AlinhamentoVertical.BASE,
                new int[]{-30, -30, -30}, // deslocamentos
                new int[]{-15, 0, 0}, // ajustesVerticais
                new int[]{220, 220, 220}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 9
        vehicleConfigs.put(9, new VehicleConfig(
                new TipoEixo[]{TipoEixo.DUPLO},
                new boolean[]{true, false, false, false, false, false},
                100, // espacamento
                AlinhamentoVertical.CENTRO,
                new int[]{-30, -30, -30}, // deslocamentos
                new int[]{0, 50, 0}, // ajustesVerticais
                new int[]{220}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 10
        vehicleConfigs.put(10, new VehicleConfig(
                new TipoEixo[]{TipoEixo.SIMPLES},
                new boolean[]{true, false, false, false, false, false},
                100, // espacamento
                AlinhamentoVertical.CENTRO,
                new int[]{-30, -30, -30}, // deslocamentos
                new int[]{0, 30, 0}, // ajustesVerticais
                new int[]{150}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 11
        vehicleConfigs.put(11, new VehicleConfig(
                new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.SIMPLES},
                new boolean[]{true, true, false, false, false, false},
                200, // espacamento
                AlinhamentoVertical.CENTRO,
                new int[]{-30, -30, -30}, // deslocamentos
                new int[]{0, 0, 0}, // ajustesVerticais
                new int[]{220, 150}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 12
        vehicleConfigs.put(12, new VehicleConfig(
                new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO},
                new boolean[]{true, true, true, true, true, true},
                100, // espacamento
                AlinhamentoVertical.TOPO,
                new int[]{-30, -30, -30, -30, -30, -30}, // deslocamentos
                new int[]{0, 0, 100}, // ajustesVerticais
                new int[]{220, 220, 220, 220, 220, 220}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 13
        vehicleConfigs.put(13, new VehicleConfig(
                new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES},
                new boolean[]{true, true, true, true, true, false},
                100, // espacamento
                AlinhamentoVertical.CENTRO,
                new int[]{-30, -30, -30, -30, -30}, // deslocamentos
                new int[]{0, 0, 0}, // ajustesVerticais
                new int[]{150, 150, 150, 150, 150}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 14
        vehicleConfigs.put(14, new VehicleConfig(
                new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.DUPLO, TipoEixo.DUPLO},
                new boolean[]{true, true, true, true, true, false},
                100, // espacamento
                AlinhamentoVertical.BASE,
                new int[]{-30, -30, -30, -30, -30}, // deslocamentos
                new int[]{20, 0, 30}, // ajustesVerticais
                new int[]{150, 150, 150, 240, 240}, // largurasEixos
                new int[]{-80, -80, -80, 30, 30} // posicoesEixos
        ));

        // COD.: 15
        vehicleConfigs.put(15, new VehicleConfig(
                new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES},
                new boolean[]{true, true, true, true, true, true},
                100, // espacamento
                AlinhamentoVertical.TOPO,
                new int[]{-30, -30, -30, -30, -30, -30}, // deslocamentos
                new int[]{0, 0, 110}, // ajustesVerticais
                new int[]{150, 150, 150, 150, 150, 150}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 16
        vehicleConfigs.put(16, new VehicleConfig(
                new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.DUPLO, TipoEixo.DUPLO},
                new boolean[]{true, false, false, false, true, true},
                150, // espacamento
                AlinhamentoVertical.BASE,
                new int[]{-30, -30, -30}, // deslocamentos
                new int[]{80, 0, 0}, // ajustesVerticais
                new int[]{150, 220, 220}, // largurasEixos
                new int[]{0, 0, 0, 0, 90, 50} // posicoesEixos
        ));

        // COD.: 17
        vehicleConfigs.put(17, new VehicleConfig(
                new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO},
                new boolean[]{true, false, false, false, false, true},
                370, // espacamento
                AlinhamentoVertical.TOPO,
                new int[]{-30, -30, -30}, // deslocamentos
                new int[]{30, 0, 100}, // ajustesVerticais
                new int[]{230, 230}, // largurasEixos
                null // posicoesEixos
        ));

        // COD.: 18 (Dolly)
        vehicleConfigs.put(18, new VehicleConfig(
                new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO},
                new boolean[]{true, false, false, false, false, true},
                120, // espacamento
                AlinhamentoVertical.BASE,
                new int[]{-30, -30, 0}, // deslocamentos
                new int[]{30, 0, 0}, // ajustesVerticais
                new int[]{210, 210}, // largurasEixos
                null // posicoesEixos
        ));
    }

    private void atualizarTabelaVeiculos() {
        // Define as colunas da tabela
        String[] colunas = {"ID", "Frota", "Placa", "Tipo de Veículo", "Qtd Pneus", "Status"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Torna todas as células não editáveis
                return false;
            }
        };

        // Busca os dados do banco e armazena na lista da classe
        this.listaDeVeiculos = veiculoDAO.listarTodos();

        // Popula o modelo com os dados dos veículos
        for (Veiculo veiculo : this.listaDeVeiculos) {
            model.addRow(new Object[]{
                veiculo.getID(),
                veiculo.getFROTA(),
                veiculo.getPLACA(),
                veiculo.getID_CONFIG_FK(), // Assumindo que este é o ID do tipo de veículo
                veiculo.getQTD_PNEUS(),
                veiculo.getSTATUS_VEICULO()
            });
        }

        // Define o modelo na JTable
        Tabela_Exibicao_veiculos.setModel(model);
        formatarTabelaVeiculos();
    }

    private void atualizarContagemTotalPneus() {
        int totalPneus = 0;

        // Pega a lista de todos os veículos do banco de dados
        List<Veiculo> veiculos = veiculoDAO.listarTodos();

        if (veiculos != null) {
            // Para cada veículo na lista, soma a sua quantidade de pneus
            for (Veiculo veiculo : veiculos) {
                totalPneus += veiculo.getQTD_PNEUS();
            }
        }

        // Atualiza o texto do label com o total calculado
        Pneus_necessarios.setText(String.valueOf(totalPneus));
    }

    private void atualizarContagemDeVeiculos() {
        List<Veiculo> veiculos = veiculoDAO.listarTodos();
        int totalVeiculos = (veiculos != null) ? veiculos.size() : 0;
        Veiculos_Cadastrados.setText(String.valueOf(totalVeiculos));
    }

    private void gerenciarVisibilidadePosicao() {
        // Esconde tudo por padrão
        lb_carreta.setVisible(false);
        cbposicao_carreta.setVisible(false);
        lbPosicao.setVisible(false);

        int selectedIndex = cmbTipoVeiculo.getSelectedIndex();
        if (selectedIndex <= 0) {
            return; // Nada selecionado, então não mostra nada
        }

        try {
            String itemSelecionado = cmbTipoVeiculo.getSelectedItem().toString();
            int idConfig = Integer.parseInt(itemSelecionado.split(" - ")[0]);

            // Se o ID selecionado estiver na lista de carretas, mostra os campos de cadastro
            if (carretaIds.contains(idConfig)) {
                lb_carreta.setVisible(true);
                cbposicao_carreta.setVisible(true);
            }
        } catch (Exception e) {
            // Ignora erros de parsing, apenas não mostra os campos
        }
    }

    private void selecionarTipoVeiculoPorId(int idConfig) {
        for (int i = 0; i < cmbTipoVeiculo.getItemCount(); i++) {
            String item = cmbTipoVeiculo.getItemAt(i);
            if (item.startsWith(idConfig + " - ")) {
                cmbTipoVeiculo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void formatarTabelaVeiculos() {
    // Cores para as linhas (efeito zebra)
    final java.awt.Color ZEBRA_COLOR_1 = java.awt.Color.WHITE;
    final java.awt.Color ZEBRA_COLOR_2 = new java.awt.Color(240, 240, 240);

    // 1. Criar um TableCellRenderer customizado para o efeito Zebra e centralização
    class ZebraTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                                                                 boolean isSelected, boolean hasFocus, int row, int column) {
            // Chama a implementação padrão para obter o componente base
            java.awt.Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Lógica de cores alternadas para as linhas
            if (!isSelected) {
                if (row % 2 == 0) {
                    cell.setBackground(ZEBRA_COLOR_1);
                } else {
                    cell.setBackground(ZEBRA_COLOR_2);
                }
            }

            // Centraliza o texto em todas as células
            setHorizontalAlignment(javax.swing.JLabel.CENTER);

            return cell;
        }
    }

    // 2. Configurar o cabeçalho da tabela com texto em maiúsculo, negrito e centralizado
    javax.swing.table.JTableHeader header = Tabela_Exibicao_veiculos.getTableHeader();
    header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
        @Override
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                                                                 boolean isSelected, boolean hasFocus, int row, int column) {
            // Chama o método da superclasse para obter o componente padrão
            javax.swing.JLabel label = (javax.swing.JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Converte o texto do cabeçalho para maiúsculas
            if (value != null) {
                label.setText(value.toString().toUpperCase());
            }

            // Define a fonte como negrito
            label.setFont(label.getFont().deriveFont(java.awt.Font.BOLD));

            // Centraliza o texto
            label.setHorizontalAlignment(javax.swing.JLabel.CENTER);

            // Garante que o fundo seja o padrão do Look and Feel
            label.setBackground(javax.swing.UIManager.getColor("TableHeader.background"));
            label.setForeground(javax.swing.UIManager.getColor("TableHeader.foreground"));
            label.setBorder(javax.swing.UIManager.getBorder("TableHeader.cellBorder"));

            return label;
        }
    });

    // 3. Aplicar o novo renderizador e ajustar as larguras
    ZebraTableCellRenderer zebraRenderer = new ZebraTableCellRenderer();
    for (int i = 0; i < Tabela_Exibicao_veiculos.getColumnCount(); i++) {
        Tabela_Exibicao_veiculos.getColumnModel().getColumn(i).setCellRenderer(zebraRenderer);
    }

    // 4. Ajustar Largura preferencial das Colunas
    Tabela_Exibicao_veiculos.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
    Tabela_Exibicao_veiculos.getColumnModel().getColumn(1).setPreferredWidth(70);  // Frota
    Tabela_Exibicao_veiculos.getColumnModel().getColumn(2).setPreferredWidth(100); // Placa
    Tabela_Exibicao_veiculos.getColumnModel().getColumn(3).setPreferredWidth(150); // Tipo de Veículo
    Tabela_Exibicao_veiculos.getColumnModel().getColumn(4).setPreferredWidth(80);  // Qtd Pneus
    Tabela_Exibicao_veiculos.getColumnModel().getColumn(5).setPreferredWidth(80);  // Status
}

    // Enumeração para tipo de eixo
    public enum TipoEixo {
        SIMPLES,
        DUPLO
    }

    public enum AlinhamentoVertical {
        TOPO,
        CENTRO,
        BASE
    }

    // Classe para armazenar a configuração de cada modelo de veículo
    private static class VehicleConfig {

        final TipoEixo[] tipos;
        final boolean[] visibilidade;
        final int espacamento;
        final AlinhamentoVertical alinhamento;
        final int[] deslocamentos;
        final int[] ajustesVerticais;
        final int[] largurasEixos;
        final int[] posicoesEixos; // Pode ser null

        VehicleConfig(TipoEixo[] tipos, boolean[] visibilidade, int espacamento,
                AlinhamentoVertical alinhamento, int[] deslocamentos,
                int[] ajustesVerticais, int[] largurasEixos, int[] posicoesEixos) {
            this.tipos = tipos;
            this.visibilidade = visibilidade;
            this.espacamento = espacamento;
            this.alinhamento = alinhamento;
            this.deslocamentos = deslocamentos;
            this.ajustesVerticais = ajustesVerticais;
            this.largurasEixos = largurasEixos;
            this.posicoesEixos = posicoesEixos;
        }
    }

    // Mapa para armazenar as configurações dos veículos por ID
    private final Map<Integer, VehicleConfig> vehicleConfigs = new HashMap<>();

    private void inicializarComponentesChassi() {

        slotsDePneus = new javax.swing.JLabel[6][4];

        slotsDePneus[0][0] = Label100;
        slotsDePneus[0][1] = Label101;
        slotsDePneus[0][2] = Label102;
        slotsDePneus[0][3] = Label103;

        // Eixo 2 (índice 1)
        slotsDePneus[1][0] = Label104;
        slotsDePneus[1][1] = Label105;
        slotsDePneus[1][2] = Label106;
        slotsDePneus[1][3] = Label107;

        // Eixo 3 (índice 2)
        slotsDePneus[2][0] = Label108;
        slotsDePneus[2][1] = Label109;
        slotsDePneus[2][2] = Label110;
        slotsDePneus[2][3] = Label111;

        // Eixo 4 (índice 3)
        slotsDePneus[3][0] = Label112;
        slotsDePneus[3][1] = Label113;
        slotsDePneus[3][2] = Label114;
        slotsDePneus[3][3] = Label115;

        // Eixo 5 (índice 4)
        slotsDePneus[4][0] = Label116;
        slotsDePneus[4][1] = Label117;
        slotsDePneus[4][2] = Label118;
        slotsDePneus[4][3] = Label119;

        // Eixo 6 (índice 5)
        slotsDePneus[5][0] = Label120;
        slotsDePneus[5][1] = Label121;
        slotsDePneus[5][2] = Label122;
        slotsDePneus[5][3] = Label123;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                TELA_ZERO.add(slotsDePneus[i][j]);
            }
        }
    }

    private javax.swing.ImageIcon redimensionarIcone(javax.swing.ImageIcon iconeOriginal, int largura, int altura) {
        if (iconeOriginal == null) {
            return null;
        }
        return new javax.swing.ImageIcon(iconeOriginal.getImage().getScaledInstance(largura, altura, java.awt.Image.SCALE_SMOOTH));
    }

    private void desenharChassi(TipoEixo[] tipos, boolean[] visibilidade, int espacamento, AlinhamentoVertical alinhamento, int[] deslocamentos, int[] ajustesVerticais, int[] largurasEixos, int[] posicoesEixos) {
        // --- Validação ---
        int numEixosVisiveis = 0;
        for (boolean v : visibilidade) {
            if (v) {
                numEixosVisiveis++;
            }
        }
        if (numEixosVisiveis != tipos.length || (largurasEixos != null && numEixosVisiveis != largurasEixos.length)) {
            JOptionPane.showMessageDialog(this,
                    "Erro de configuração: O número de eixos visíveis, tipos e larguras devem corresponder.",
                    "Erro de Desenho", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Preparação ---
        javax.swing.JLabel[] todosOsEixos = {lbeixo1, lbeixo2, lbeixo3, lbeixo4, lbeixo5, lbeixo6};
        javax.swing.ImageIcon iconeOriginal = new javax.swing.ImageIcon(getClass().getResource("/br/com/martins_borges/telas/Imagens/pneu.png"));
        int larguraPneu = 45, alturaPneu = 70;
        javax.swing.ImageIcon iconPneu = redimensionarIcone(iconeOriginal, larguraPneu, alturaPneu);
        int centroChassiX = TELA_ZERO.getWidth() / 2;

        // --- Limpeza ---
        for (javax.swing.JLabel eixo : todosOsEixos) {
            if (eixo != null) {
                eixo.setVisible(false);
            }
        }
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                if (slotsDePneus[i][j] != null) {
                    slotsDePneus[i][j].setVisible(false);
                    slotsDePneus[i][j].setIcon(null);
                }
            }
        }
        lbespinha_dorsal.setVisible(false);

        // --- Cálculo de Posição ---
        int alturaTotalChassi = (numEixosVisiveis > 0 ? (numEixosVisiveis - 1) * espacamento + alturaPneu : 0);
        int yInicial;

        // Posição 0 = Ajuste para BASE
        // Posição 1 = Ajuste para CENTRO
        // Posição 2 = Ajuste para TOPO
        int ajusteBase = (ajustesVerticais != null && ajustesVerticais.length >= 1) ? ajustesVerticais[0] : 30; // Padrão: 30
        int ajusteCentro = (ajustesVerticais != null && ajustesVerticais.length >= 2) ? ajustesVerticais[1] : 0;   // Padrão: 0
        int ajusteTopo = (ajustesVerticais != null && ajustesVerticais.length >= 3) ? ajustesVerticais[2] : 30;   // Padrão: 30

        switch (alinhamento) {
            case TOPO:
                yInicial = ajusteTopo;
                break;
            case BASE:
                yInicial = TELA_ZERO.getHeight() - alturaTotalChassi - ajusteBase;
                break;
            case CENTRO:
            default:
                yInicial = (TELA_ZERO.getHeight() - alturaTotalChassi) / 2 + ajusteCentro;
                break;
        }

        // --- Desenho ---
        int contadorDeReceita = 0;
        int yEixoAtual = yInicial;
        int totalTires = 0; // Inicializa o contador de pneus

        for (int i = 0; i < visibilidade.length; i++) {
            if (visibilidade[i]) {
                javax.swing.JLabel eixoAtual = todosOsEixos[i];
                TipoEixo tipoDoEixo = tipos[contadorDeReceita];
                int deslocamento = (deslocamentos != null && contadorDeReceita < deslocamentos.length) ? deslocamentos[contadorDeReceita] : 0;
                int larguraDoEixoAtual = (largurasEixos != null && contadorDeReceita < largurasEixos.length) ? largurasEixos[contadorDeReceita] : (tipoDoEixo == TipoEixo.SIMPLES ? 190 : 280);

                // Pega o ajuste fino para este eixo específico do novo array.
                // Se o array for nulo ou o índice for inválido, o ajuste é 0.
                int ajusteFino = (posicoesEixos != null && i < posicoesEixos.length) ? posicoesEixos[i] : 0;

                eixoAtual.setVisible(true);

                int alturaEixo = eixoAtual.getHeight();
                javax.swing.JLabel pneuEsqExterno = slotsDePneus[i][0], pneuEsqInterno = slotsDePneus[i][1];
                javax.swing.JLabel pneuDirInterno = slotsDePneus[i][2], pneuDirExterno = slotsDePneus[i][3];

                if (tipoDoEixo == TipoEixo.SIMPLES) {
                    eixoAtual.setBounds(centroChassiX - (larguraDoEixoAtual / 2), yEixoAtual + ajusteFino, larguraDoEixoAtual, alturaEixo);
                    int yPneu = yEixoAtual + ajusteFino + (alturaEixo / 2) - (alturaPneu / 2);
                    int xPneuEsquerdo = eixoAtual.getX() + deslocamento;
                    int xPneuDireito = eixoAtual.getX() + larguraDoEixoAtual - larguraPneu - deslocamento;
                    configuraPneu(pneuEsqExterno, iconPneu, xPneuEsquerdo, yPneu);
                    configuraPneu(pneuDirExterno, iconPneu, xPneuDireito, yPneu);
                    pneuEsqInterno.setVisible(false);
                    pneuDirInterno.setVisible(false);
                    totalTires += 2; // Adiciona 2 pneus para eixo simples
                } else if (tipoDoEixo == TipoEixo.DUPLO) {
                    eixoAtual.setBounds(centroChassiX - (larguraDoEixoAtual / 2), yEixoAtual + ajusteFino, larguraDoEixoAtual, alturaEixo);
                    int espacamentoPneus = 5;
                    int yPneu = yEixoAtual + ajusteFino + (alturaEixo / 2) - (alturaPneu / 2);
                    int xPneuEsqExterno = eixoAtual.getX() + deslocamento;
                    int xPneuEsqInterno = eixoAtual.getX() + larguraPneu + espacamentoPneus + deslocamento;
                    int xPneuDirExterno = eixoAtual.getX() + larguraDoEixoAtual - larguraPneu - deslocamento;
                    int xPneuDirInterno = eixoAtual.getX() + larguraDoEixoAtual - (larguraPneu * 2) - espacamentoPneus - deslocamento;
                    configuraPneu(pneuEsqExterno, iconPneu, xPneuEsqExterno, yPneu);
                    configuraPneu(pneuEsqInterno, iconPneu, xPneuEsqInterno, yPneu);
                    configuraPneu(pneuDirInterno, iconPneu, xPneuDirInterno, yPneu);
                    configuraPneu(pneuDirExterno, iconPneu, xPneuDirExterno, yPneu);
                    totalTires += 4; // Adiciona 4 pneus para eixo duplo
                }

                yEixoAtual += espacamento;
                contadorDeReceita++;
            }
        }

        // Atualiza a label Qtd_numeroPneu com o total de pneus
        Qtd_numeroPneu.setText(String.valueOf(totalTires));
        Qtd_numeroPneu.setEnabled(true); // Garante que a label esteja habilitada

        // --- Espinha Dorsal ---
        javax.swing.JLabel primeiroEixoVisivel = null, ultimoEixoVisivel = null;
        for (int i = 0; i < visibilidade.length; i++) {
            if (visibilidade[i]) {
                if (primeiroEixoVisivel == null) {
                    primeiroEixoVisivel = todosOsEixos[i];
                }
                ultimoEixoVisivel = todosOsEixos[i];
            }
        }

        if (primeiroEixoVisivel != null && ultimoEixoVisivel != null) {
            int yInicio = primeiroEixoVisivel.getY() + primeiroEixoVisivel.getHeight() / 2;
            int yFim = ultimoEixoVisivel.getY() + ultimoEixoVisivel.getHeight() / 2;
            lbespinha_dorsal.setBounds(centroChassiX - (lbespinha_dorsal.getWidth() / 2), yInicio, lbespinha_dorsal.getWidth(), yFim - yInicio);
            lbespinha_dorsal.setVisible(true);
        }

        TELA_ZERO.repaint();
    }

    private void configuraPneu(javax.swing.JLabel pneuLabel, javax.swing.ImageIcon icone, int x, int y) {
        if (pneuLabel != null) {
            pneuLabel.setIcon(icone);
            pneuLabel.setBounds(x, y, icone.getIconWidth(), icone.getIconHeight());
            pneuLabel.setVisible(true);
            TELA_ZERO.setComponentZOrder(pneuLabel, 0);
        }
    }

    private void atualizarNumeroModelo(int numero) {
        zero.setText(String.valueOf(numero));
    }

    public static void main(String args[]) {
        // O main de teste para JDialogs é diferente.
        // Geralmente, você criaria um JFrame e então instanciaria o JDialog a partir dele.
        // Exemplo:
        java.awt.EventQueue.invokeLater(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame("Teste de TelaCadastroVeiculos");
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            TelaCadastroVeiculos dialog = new TelaCadastroVeiculos(frame, true); // Modal
            dialog.setVisible(true);

            frame.setVisible(true); // Mostra o frame principal (pode ser invisível se o dialog for modal)
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BUTTON_BOX;
    private javax.swing.JButton Cadastrar;
    private javax.swing.JPanel EscolhaModelo;
    private javax.swing.JLabel Escolha_0;
    private javax.swing.JLabel Exclui_veiculos;
    private javax.swing.JLabel Frota;
    private javax.swing.JLabel Label100;
    private javax.swing.JLabel Label101;
    private javax.swing.JLabel Label102;
    private javax.swing.JLabel Label103;
    private javax.swing.JLabel Label104;
    private javax.swing.JLabel Label105;
    private javax.swing.JLabel Label106;
    private javax.swing.JLabel Label107;
    private javax.swing.JLabel Label108;
    private javax.swing.JLabel Label109;
    private javax.swing.JLabel Label110;
    private javax.swing.JLabel Label111;
    private javax.swing.JLabel Label112;
    private javax.swing.JLabel Label113;
    private javax.swing.JLabel Label114;
    private javax.swing.JLabel Label115;
    private javax.swing.JLabel Label116;
    private javax.swing.JLabel Label117;
    private javax.swing.JLabel Label118;
    private javax.swing.JLabel Label119;
    private javax.swing.JLabel Label120;
    private javax.swing.JLabel Label121;
    private javax.swing.JLabel Label122;
    private javax.swing.JLabel Label123;
    private javax.swing.JLabel Pneus_necessarios;
    private javax.swing.JLabel Qtd_numeroPneu;
    private javax.swing.JPanel TELA_ZERO;
    private javax.swing.JPanel TELA_ZERO_ZERO;
    private javax.swing.JScrollPane Tabela_Caminhoes;
    private javax.swing.JTable Tabela_Exibicao_veiculos;
    private javax.swing.JTable Tabela_medidaPneu;
    private javax.swing.JLabel Veiculos_Cadastrados;
    private javax.swing.JButton btAdd_Pneu;
    private javax.swing.JComboBox<String> cbposicao_carreta;
    private javax.swing.JComboBox<String> cmbMedidaPneu;
    private javax.swing.JComboBox<String> cmbTipoVeiculo;
    private javax.swing.JButton cod_cinco;
    private javax.swing.JButton cod_desesseis;
    private javax.swing.JButton cod_desessete;
    private javax.swing.JButton cod_dez;
    private javax.swing.JButton cod_dois;
    private javax.swing.JButton cod_doze;
    private javax.swing.JButton cod_nove;
    private javax.swing.JButton cod_oito;
    private javax.swing.JButton cod_onze;
    private javax.swing.JButton cod_quatorze;
    private javax.swing.JButton cod_quatro;
    private javax.swing.JButton cod_quinze;
    private javax.swing.JButton cod_seis;
    private javax.swing.JButton cod_sete;
    private javax.swing.JButton cod_trez;
    private javax.swing.JButton cod_treze;
    private javax.swing.JButton cod_um;
    private javax.swing.JButton cod_zero;
    private javax.swing.JButton dolly;
    private javax.swing.JButton fechar;
    private javax.swing.JLabel labelMensagem;
    private javax.swing.JLabel lbMedidaPneu;
    private javax.swing.JLabel lbPosicao;
    private javax.swing.JLabel lb_carreta;
    private javax.swing.JLabel lbeixo1;
    private javax.swing.JLabel lbeixo2;
    private javax.swing.JLabel lbeixo3;
    private javax.swing.JLabel lbeixo4;
    private javax.swing.JLabel lbeixo5;
    private javax.swing.JLabel lbeixo6;
    private javax.swing.JLabel lbespinha_dorsal;
    private javax.swing.JScrollPane medidaPneu;
    private javax.swing.JLabel numero_pneus;
    private javax.swing.JLabel placa;
    private javax.swing.JLabel qtd_pneus;
    private javax.swing.JLabel qtd_veiculos;
    private javax.swing.JLabel tipo_equipamento;
    private javax.swing.JTextField txtFrota;
    private javax.swing.JTextField txtPlaca;
    private javax.swing.JLabel zero;
    // End of variables declaration//GEN-END:variables
// </editor-fold>
}
