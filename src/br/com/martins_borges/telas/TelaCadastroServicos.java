package br.com.martins_borges.telas;

import br.com.martins_borges.dal.PneuDAO;
import br.com.martins_borges.dal.ParceiroDAO;
import br.com.martins_borges.dal.TipoServicoDAO;
import br.com.martins_borges.dal.TipoPneuDAO;
import br.com.martins_borges.dal.OrdemServicoPneuDAO;
import br.com.martins_borges.model.Pneu;
import br.com.martins_borges.model.OrdemServicoPneu;
import br.com.martins_borges.model.Parceiro;
import br.com.martins_borges.model.TipoServico;
import br.com.martins_borges.utilitarios.TamanhoTabela;
import br.com.martins_borges.utilitarios.Utilitarios;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.time.ZoneId;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.swing.ButtonGroup;
import javax.swing.SwingConstants;
import java.text.ParseException;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;

import java.awt.Component;
import javax.swing.JTextField;

public class TelaCadastroServicos extends javax.swing.JDialog {// <editor-fold defaultstate="collapsed" desc="">

    private DefaultTableModel modelPneusFiltros;
    private DefaultTableModel modelListaOS;
    private CardLayout cardLayout;
    private List<Pneu> pneusExibidosNoFiltro;
    private Pneu pneuSelecionadoParaOS;
    private final PneuDAO pneuDAO;
    private final OrdemServicoPneuDAO osPneuDAO;
    private final ParceiroDAO parceiroDAO;
    private final TipoServicoDAO tipoServicoDAO;
    private final TipoPneuDAO tipoPneuDAO;
    private static final int ID_EMPRESA_MARTINS_BORGES = 1;
    private static final int ID_EMPRESA_ALB = 2;
    private static final int ID_EMPRESA_ENGEUDI = 3;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // </editor-fold>
    

    public TelaCadastroServicos(java.awt.Frame parent, boolean modal) {

        super(parent, modal);
        initComponents();

        dataRetornoChooser.setDateFormatString("dd/MM/yyyy");

        Utilitarios.aplicarFormatacaoCampos(this.getContentPane());
        pneuDAO = new PneuDAO();
        osPneuDAO = new OrdemServicoPneuDAO();
        parceiroDAO = new ParceiroDAO();
        tipoServicoDAO = new TipoServicoDAO();
        tipoPneuDAO = new TipoPneuDAO();

        adicionarListenersValor();
        pneusExibidosNoFiltro = new ArrayList<>();

        // Configurar campos numéricos
        configurarCampoNumerico(TxtN_Orcamento);
        configurarCampoNumerico(txtFogo);

        configurarTabelaFiltroPneus();

        // Configuração da tabela de OSs (tbltabelaServicos)
        modelListaOS = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID OS", "Pneu ID", "Nº Fogo", "Nº Orçamento", "Data Envio",
                    "Parceiro", "Tipo Serviço", "Valor", "Motivo", "Data Retorno",
                    "Status Pneu OS", "Observações"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int columnIndex) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1) {
                    return Integer.class;
                }
                if (columnIndex == 7) {
                    return Double.class;
                }
                return Object.class;
            }
        };
        tbltabelaServicos.setModel(modelListaOS);
        TableColumnModel tcm = tbltabelaServicos.getColumnModel();
        // The background colors are now handled by TamanhoTabela.configurar
        TamanhoTabela.configurar(tbltabelaServicos, new int[]{80, 80, 80, 80, 80, 320, 120, 80, 100, 80, 300, 700});
        // Apply specific renderers that TamanhoTabela.configurar doesn't handle by default
        tcm.getColumn(7).setCellRenderer(new TamanhoTabela.CurrencyRenderer()); // Valor column

        if (Panel_Tabelas.getLayout() instanceof CardLayout) {
            cardLayout = (CardLayout) Panel_Tabelas.getLayout();
        } else {
            System.err.println("Layout do Panel_Tabelas não é CardLayout!");
        }

        criarGrupoEmpresas();

        if (PanelContador_Ordens != null) {
            PanelContador_Ordens.setVisible(false);
        }

        mostrarTelaFiltroPneus();

        MARTINS_BORGES.addActionListener(e -> aplicarFiltroPneus());
        ALB.addActionListener(e -> aplicarFiltroPneus());
        ENGEUDI.addActionListener(e -> aplicarFiltroPneus());

        txtFogo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aplicarFiltroPneus();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aplicarFiltroPneus();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aplicarFiltroPneus();
            }
        });

        this.getContentPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Component source = (Component) e.getSource();
                if (source != MARTINS_BORGES && source != ALB && source != ENGEUDI
                        && source != txtFogo
                        && !(source instanceof javax.swing.JTable)
                        && !(source instanceof javax.swing.JTextField || source instanceof javax.swing.JTextArea || source instanceof javax.swing.JComboBox || source instanceof javax.swing.JButton || source instanceof com.toedter.calendar.JDateChooser)) {
                    if (scrollPaneFiltroPneus.isVisible() && !Cadastros.isVisible()) {
                        if (grupoEmpresas != null && grupoEmpresas.getSelection() != null) {
                            grupoEmpresas.clearSelection();
                            aplicarFiltroPneus();
                        }
                        if (txtFogo != null && !txtFogo.getText().isEmpty()) {
                            txtFogo.setText("");
                            aplicarFiltroPneus(); // Aplica após limpar o fogo também
                        }
                    }
                }
            }
        });

        tbltabelaServicos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int linhaSelecionada = tbltabelaServicos.getSelectedRow();
                    if (linhaSelecionada != -1) {
                        mostrarTelaFiltroPneus();
                    }
                }
            }
        });

        tblPneusFiltros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int linhaSelecionadaView = tblPneusFiltros.getSelectedRow();
                    if (linhaSelecionadaView != -1) {
                        int linhaSelecionadaModel = tblPneusFiltros.convertRowIndexToModel(linhaSelecionadaView);
                        if (linhaSelecionadaModel >= 0 && linhaSelecionadaModel < pneusExibidosNoFiltro.size()) {
                            pneuSelecionadoParaOS = pneusExibidosNoFiltro.get(linhaSelecionadaModel);
                            String status = pneuSelecionadoParaOS.getStatusPneu();

                            if (null == status) {
                                JOptionPane.showMessageDialog(TelaCadastroServicos.this, "Pneu com status desconhecido. Não é possível prosseguir.", "Aviso", JOptionPane.WARNING_MESSAGE);
                                limparFiltros();
                                return;
                            }

                            switch (status) {
                                case "EM_SERVICO":
                                    String[] options = {"Registrar Retorno (Estoque)", "Registrar Descarte"};
                                    int escolha = JOptionPane.showOptionDialog(
                                            TelaCadastroServicos.this,
                                            "O pneu (Fogo: " + pneuSelecionadoParaOS.getFogo() + ") está 'EM SERVIÇO'.\nO que você deseja fazer?",
                                            "Pneu em Serviço",
                                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                    String novoStatusParaPneu = null;
                                    if (escolha == 0) {
                                        novoStatusParaPneu = "ESTOQUE";
                                    } else if (escolha == 1) {
                                        int confirmDescarte = JOptionPane.showConfirmDialog(TelaCadastroServicos.this,
                                                "ATENÇÃO: Esta ação marcará o pneu (Fogo: " + pneuSelecionadoParaOS.getFogo() + ") como DESCARTADO e não poderá ser desfeita.\nTem certeza que deseja descartar este pneu?",
                                                "Confirmar Descarte de Pneu", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                                        if (confirmDescarte == JOptionPane.YES_OPTION) {
                                            novoStatusParaPneu = "DESCARTADO";
                                        }
                                    }
                                    if (novoStatusParaPneu != null) {
                                        boolean sucessoPneu = pneuDAO.atualizarStatusERetorno(pneuSelecionadoParaOS.getId(), novoStatusParaPneu, new Date());
                                        if (sucessoPneu) {
                                            OrdemServicoPneu osAberta = osPneuDAO.buscarUltimaOSAbertaPorPneu(pneuSelecionadoParaOS.getId());
                                            if (osAberta != null) {
                                                osAberta.setDataRetorno(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                                                String obsOriginal = osAberta.getObservacoesServico() != null ? osAberta.getObservacoesServico() : "";
                                                String obsFinal = obsOriginal + "\nFinalizado: Pneu " + novoStatusParaPneu + " em " + new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                                                osAberta.setObservacoesServico(obsFinal.trim());
                                                osPneuDAO.atualizarOrdemServico(osAberta);
                                                JOptionPane.showMessageDialog(TelaCadastroServicos.this, "Pneu (Fogo: " + pneuSelecionadoParaOS.getFogo() + ") atualizado para " + novoStatusParaPneu + " e OS associada finalizada.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                                            } else {
                                                JOptionPane.showMessageDialog(TelaCadastroServicos.this, "Aviso: Pneu (Fogo: " + pneuSelecionadoParaOS.getFogo() + ") atualizado para " + novoStatusParaPneu + ", mas não foi encontrada OS aberta para finalizar.", "Sucesso com Aviso", JOptionPane.WARNING_MESSAGE);
                                            }
                                            aplicarFiltroPneus();
                                        } else {
                                            JOptionPane.showMessageDialog(TelaCadastroServicos.this, "Falha ao atualizar o status do pneu.", "Erro", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                    limparFiltros();
                                    break;
                                case "ESTOQUE":
                                case "Livre":
                                    preencherCamposFiltroPneuSelecionado(pneuSelecionadoParaOS);
                                    mostrarTelaCadastroOS();
                                    carregarTabelaPrincipalOS();
                                    prepararParaNovaOS(pneuSelecionadoParaOS);
                                    break;
                                case "DESCARTADO":
                                    JOptionPane.showMessageDialog(TelaCadastroServicos.this,
                                            "Este pneu (Fogo: " + pneuSelecionadoParaOS.getFogo() + ") já foi marcado como DESCARTADO.",
                                            "Pneu Descartado", JOptionPane.WARNING_MESSAGE);
                                    limparFiltros();
                                    break;
                                default:
                                    JOptionPane.showMessageDialog(TelaCadastroServicos.this, "Ação não disponível para pneus com status '" + status + "'.", "Aviso", JOptionPane.WARNING_MESSAGE);
                                    limparFiltros();
                                    break;
                            }
                        } else {
                            JOptionPane.showMessageDialog(TelaCadastroServicos.this, "Erro interno ao obter dados do pneu selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
                            pneuSelecionadoParaOS = null;
                            limparFiltros();
                        }
                    }
                }
            }
        });

        btnCadastrar.addActionListener(e -> cadastrarNovaOS());

        popularComboBoxesServico();

        // Define um tamanho preferencial e centraliza a janela.
        definirTamanhoEPosicao();
    }

    private void definirTamanhoEPosicao() {
        // O código é executado no Event Dispatch Thread para garantir que o Frame pai já tenha um tamanho.
        javax.swing.SwingUtilities.invokeLater(() -> {
            java.awt.Window parent = javax.swing.SwingUtilities.getWindowAncestor(this);
            if (parent != null) {
                // Define um tamanho fixo, porém razoável, para a janela.
                int frameWidth = 1115;
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

    private void adicionarListenersValor() {
        if (TxtValor == null) {
            return;
        }
        TxtValor.setHorizontalAlignment(SwingConstants.RIGHT);
        TxtValor.setText("");
        TxtValor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                formatarValorAoSair();
            }

            @Override
            public void focusGained(FocusEvent e) {
                String texto = TxtValor.getText().trim();
                if (texto.startsWith("R$")) {
                    texto = texto.replace("R$", "").replace(".", "").trim();
                }
                if (texto.endsWith(",00")) {
                    texto = texto.substring(0, texto.length() - 3);
                }
                TxtValor.setText(texto);
                if (!texto.isEmpty()) {
                    TxtValor.setCaretPosition(TxtValor.getText().length());
                }
            }
        });
        TxtValor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                String textoAtual = TxtValor.getText();
                if (!Character.isDigit(c) && c != ',' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE && !Character.isISOControl(c)) {
                    evt.consume();
                }
                if (c == ',' && textoAtual.contains(",")) {
                    evt.consume();
                }
            }
        });
    }

    private void formatarValorAoSair() {
        if (TxtValor == null) {
            return;
        }
        String texto = TxtValor.getText().trim();
        if (texto.isEmpty()) {
            TxtValor.setText("");
            return;
        }
        try {
            texto = texto.replace("R$", "").replace(" ", "").trim();
            String textoParaParseSimples = texto.replace(",", ".");
            textoParaParseSimples = textoParaParseSimples.replaceAll("[^\\d.]", "");
            if (textoParaParseSimples.isEmpty() || ".".equals(textoParaParseSimples)) {
                TxtValor.setText("");
                return;
            }
            double valor = Double.parseDouble(textoParaParseSimples);
            TxtValor.setText(currencyFormat.format(valor));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido. Use vírgula como decimal (Ex: 123,45).", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
            TxtValor.setText("");
        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this, "Erro inesperado ao formatar valor.", "Erro", JOptionPane.ERROR_MESSAGE);
            TxtValor.setText("");
        }
    }

    private String getNomeEmpresaPorId(int id) {
        switch (id) {
            case ID_EMPRESA_MARTINS_BORGES:
                return "Martins Borges";
            case ID_EMPRESA_ALB:
                return "ALB";
            case ID_EMPRESA_ENGEUDI:
                return "Engeudi";
            default:
                return "";
        }
    }

    private void configurarTabelaFiltroPneus() {
        modelPneusFiltros = new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Empresa", "Nº Fogo", "Tipo Pneu", "Medida", "Status"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPneusFiltros.setModel(modelPneusFiltros);
        TableColumnModel tcm = tblPneusFiltros.getColumnModel();
        
        // Define as cores de fundo para cada coluna da tblPneusFiltros
        Color[] pneusFiltrosTableColors = {
            TamanhoTabela.HEADER_COLORS[0], // ID - Rosa
            TamanhoTabela.HEADER_COLORS[1], // Empresa - Azul claro
            TamanhoTabela.HEADER_COLORS[2], // Nº Fogo - Cáqui
            TamanhoTabela.HEADER_COLORS[3], // Tipo Pneu - Verde-claro
            TamanhoTabela.HEADER_COLORS[4], // Medida - Pêssego
            TamanhoTabela.HEADER_COLORS[5]  // Status - Lavanda
        };

        TamanhoTabela.configurar(tblPneusFiltros, new int[]{150, 300, 150, 200, 300, 250}, pneusFiltrosTableColors);
    }

    private void mostrarTelaFiltroPneus() {
        cardLayout.show(Panel_Tabelas, "filtroPneusCard");
        Cadastros.setVisible(false);
        Empresas.setVisible(true);
        lbFogo.setVisible(true);
        txtFogo.setVisible(true);
        lbN_Orcamento.setVisible(false);
        TxtN_Orcamento.setVisible(false);
        habilitarCamposFiltro(true);
        btnCadastrar.setEnabled(false);

        limparFiltros();
        limparCamposCadastroOS();
        if (PanelContador_Ordens != null) {
            PanelContador_Ordens.setVisible(false);
        }

        // Garante que o txtFogo comece desabilitado
        if (txtFogo != null) {
            txtFogo.setEnabled(false);
        }
    }

    private void limparFiltros() {
        if (grupoEmpresas != null) {
            grupoEmpresas.clearSelection();
        }
        if (txtFogo != null) {
            txtFogo.setText("");
            txtFogo.setEnabled(false); // Garante que o campo fique desabilitado ao limpar
        }
        aplicarFiltroPneus();
    }

    private void habilitarCamposFiltro(boolean habilitar) {
        if (MARTINS_BORGES != null) {
            MARTINS_BORGES.setEnabled(habilitar);
        }
        if (ALB != null) {
            ALB.setEnabled(habilitar);
        }
        if (ENGEUDI != null) {
            ENGEUDI.setEnabled(habilitar);
        }
        atualizarEstadoTxtFogo(); // Sempre atualiza o estado do txtFogo baseado nas empresas selecionadas
    }

    private void atualizarEstadoTxtFogo() {
        if (txtFogo != null) {
            boolean algumaEmpresaSelecionada = (MARTINS_BORGES != null && MARTINS_BORGES.isSelected())
                    || (ALB != null && ALB.isSelected())
                    || (ENGEUDI != null && ENGEUDI.isSelected());
            txtFogo.setEnabled(algumaEmpresaSelecionada);
            if (!algumaEmpresaSelecionada) {
                txtFogo.setText("");
                aplicarFiltroPneus();
            }
        }
    }

    private void mostrarTelaCadastroOS() {
        cardLayout.show(Panel_Tabelas, "listaOSCard");
        Cadastros.setVisible(true);

        Empresas.setVisible(true);
        lbFogo.setVisible(true);
        txtFogo.setVisible(true);
        lbN_Orcamento.setVisible(true);
        TxtN_Orcamento.setVisible(true);
        habilitarCamposFiltro(false);
        if (PanelContador_Ordens != null) {
            PanelContador_Ordens.setVisible(true);
        }
    }

    private void criarGrupoEmpresas() {
        grupoEmpresas = new ButtonGroup();
        if (MARTINS_BORGES != null) {
            MARTINS_BORGES.addActionListener(e -> {
                aplicarFiltroPneus();
                atualizarEstadoTxtFogo();
            });
            grupoEmpresas.add(MARTINS_BORGES);
        }
        if (ALB != null) {
            ALB.addActionListener(e -> {
                aplicarFiltroPneus();
                atualizarEstadoTxtFogo();
            });
            grupoEmpresas.add(ALB);
        }
        if (ENGEUDI != null) {
            ENGEUDI.addActionListener(e -> {
                aplicarFiltroPneus();
                atualizarEstadoTxtFogo();
            });
            grupoEmpresas.add(ENGEUDI);
        }
    }

    private void preencherCamposFiltroPneuSelecionado(Pneu pneu) {
        if (pneu == null) {
            if (grupoEmpresas != null) {
                grupoEmpresas.clearSelection();
            }
            if (txtFogo != null) {
                txtFogo.setText("");
            }
            return;
        }
        if (grupoEmpresas != null) {
            grupoEmpresas.clearSelection();
        }
        int idEmpresa = pneu.getIdEmpresaProprietaria();
        if (MARTINS_BORGES != null && idEmpresa == ID_EMPRESA_MARTINS_BORGES) {
            MARTINS_BORGES.setSelected(true);
        } else if (ALB != null && idEmpresa == ID_EMPRESA_ALB) {
            ALB.setSelected(true);
        } else if (ENGEUDI != null && idEmpresa == ID_EMPRESA_ENGEUDI) {
            ENGEUDI.setSelected(true);
        }
        if (txtFogo != null) {
            txtFogo.setText(pneu.getFogo());
        }
    }

    private void habilitarCamposCadastroOS(boolean habilitar) {
        TxtN_Orcamento.setEnabled(false);
        TxtValor.setEnabled(habilitar);
        cbParceiroServico.setEnabled(habilitar);
        cbTipoServico.setEnabled(habilitar);
        TxtTipo_pneu.setEnabled(false);
        TxtStatus.setEnabled(false);
        TxtMotivo.setEnabled(habilitar);
        dataRetornoChooser.setEnabled(habilitar);
        txtObservacoesServico.setEnabled(habilitar);
        btnNovoParceiro.setEnabled(habilitar);
        btnEditarParceiro.setEnabled(habilitar);
    }

    private void limparCamposCadastroOS() {
        if (grupoEmpresas != null) {
            grupoEmpresas.clearSelection();
        }
        if (txtFogo != null) {
            txtFogo.setText("");
        }

        // Só limpa os campos se não estiver na tela de filtros
        if (!scrollPaneFiltroPneus.isVisible()) {
            TxtN_Orcamento.setText("");
            TxtTipo_pneu.setText("");
            TxtValor.setText("");
            cbParceiroServico.setSelectedItem(null);
            cbTipoServico.setSelectedItem(null);
            TxtMotivo.setText("");
            dataRetornoChooser.setDate(null);
            TxtStatus.setText("");
            txtObservacoesServico.setText("");
            if (modelListaOS != null) {
                modelListaOS.setRowCount(0);
            }
            pneuSelecionadoParaOS = null;
            btnCadastrar.setText("CADASTRAR");
            for (java.awt.event.ActionListener al : btnCadastrar.getActionListeners()) {
                btnCadastrar.removeActionListener(al);
            }
            btnCadastrar.addActionListener(e -> cadastrarNovaOS());
            btnCadastrar.setEnabled(false);
        }
    }

    private void limparCamposCadastroOS_DadosEntrada() {
        TxtValor.setText("");
        cbParceiroServico.setSelectedItem(null);
        cbTipoServico.setSelectedItem(null);
        TxtMotivo.setText("");
        txtObservacoesServico.setText("");
    }

    private void popularComboBoxesServico() {
        try {
            List<String> parceiros = parceiroDAO.listarNomes();
            cbParceiroServico.removeAllItems();
            cbParceiroServico.addItem(null);
            for (String nome : parceiros) {
                cbParceiroServico.addItem(nome);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar Parceiros: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);

        }
        try {
            List<String> tiposServico = tipoServicoDAO.listarNomes();
            cbTipoServico.removeAllItems();
            cbTipoServico.addItem(null);
            for (String nome : tiposServico) {
                cbTipoServico.addItem(nome);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar Tipos de Serviço: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);

        }
    }

    private void aplicarFiltroPneus() {
        pneusExibidosNoFiltro.clear();
        modelPneusFiltros.setRowCount(0);
        List<Integer> idsEmpresasSelecionadas = new ArrayList<>();
        if (MARTINS_BORGES.isSelected()) {
            idsEmpresasSelecionadas.add(ID_EMPRESA_MARTINS_BORGES);
        }
        if (ALB.isSelected()) {
            idsEmpresasSelecionadas.add(ID_EMPRESA_ALB);
        }
        if (ENGEUDI.isSelected()) {
            idsEmpresasSelecionadas.add(ID_EMPRESA_ENGEUDI);
        }
        String fogoDigitado = txtFogo.getText().trim();
        List<Pneu> pneusFiltradosDoBanco;
        if (idsEmpresasSelecionadas.isEmpty()) {
            pneusFiltradosDoBanco = pneuDAO.buscarPneusPorFiltro(0, fogoDigitado);
        } else {
            pneusFiltradosDoBanco = new ArrayList<>();
            for (int idEmpresa : idsEmpresasSelecionadas) {
                pneusFiltradosDoBanco.addAll(pneuDAO.buscarPneusPorFiltro(idEmpresa, fogoDigitado));
            }
        }
        if (pneusFiltradosDoBanco != null) {
            for (Pneu pneu : pneusFiltradosDoBanco) {
                pneusExibidosNoFiltro.add(pneu);
                modelPneusFiltros.addRow(new Object[]{
                    pneu.getId(), getNomeEmpresaPorId(pneu.getIdEmpresaProprietaria()), pneu.getFogo(),
                    pneu.getTipoPneu(), pneu.getMedida(), pneu.getStatusPneu()
                });
            }
        }
        tblPneusFiltros.clearSelection();
    }

    private void prepararParaNovaOS(Pneu pneu) {
        if (pneu == null) {
            JOptionPane.showMessageDialog(this, "Erro interno: Pneu selecionado é nulo.", "Erro", JOptionPane.ERROR_MESSAGE);
            mostrarTelaFiltroPneus();
            return;
        }
        limparCamposCadastroOS_DadosEntrada();
        // A tabela principal de OSs (tbltabelaServicos) já deve ter sido carregada ao mostrar a tela de OS.
        // Não precisa chamar carregarListaOSDoPneu(pneu.getId()); aqui.
        habilitarCamposCadastroOS(true);
        int proximoOrcamentoInt = osPneuDAO.getProximoNumeroOrcamento();
        if (proximoOrcamentoInt > 0) {
            String proximoOrcamentoFormatado = String.format("%02d", proximoOrcamentoInt);
            TxtN_Orcamento.setText(proximoOrcamentoFormatado);
            btnCadastrar.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Não foi possível gerar o nº do Orçamento.", "Erro", JOptionPane.ERROR_MESSAGE);
            TxtN_Orcamento.setText("ERRO");
            btnCadastrar.setEnabled(false);
        }
        TxtN_Orcamento.setEnabled(false);
        cbParceiroServico.requestFocusInWindow();
        TxtStatus.setText(pneu.getStatusPneu());
        TxtTipo_pneu.setText(pneu.getTipoPneu());
        btnCadastrar.setText("CADASTRAR NOVA OS");
        for (java.awt.event.ActionListener al : btnCadastrar.getActionListeners()) {
            btnCadastrar.removeActionListener(al);
        }
        btnCadastrar.addActionListener(e -> cadastrarNovaOS());
    }

    private void prepararParaFinalizarOS(Pneu pneu, String statusDestino) {
        if (pneu == null || !"EM_SERVICO".equals(pneu.getStatusPneu())) {
            JOptionPane.showMessageDialog(this, "Apenas pneus 'EM SERVIÇO' podem ser finalizados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        OrdemServicoPneu osParaFinalizar = osPneuDAO.buscarUltimaOSAbertaPorPneu(pneu.getId());
        if (osParaFinalizar == null) {
            JOptionPane.showMessageDialog(this, "Nenhuma OS aberta encontrada para este pneu.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        limparCamposCadastroOS_DadosEntrada();
        habilitarCamposCadastroOS(true);

        TxtN_Orcamento.setText(osParaFinalizar.getNumOrcamento());
        if (osParaFinalizar.getIdParceiroFk() != null) {
            cbParceiroServico.setSelectedItem(parceiroDAO.buscarNomePorId(osParaFinalizar.getIdParceiroFk()));
        }
        cbTipoServico.setSelectedItem(tipoServicoDAO.buscarNomePorId(osParaFinalizar.getIdTipoServicoFk()));
        if (osParaFinalizar.getValorServico() != null) {
            TxtValor.setText(currencyFormat.format(osParaFinalizar.getValorServico()));
        } else {
            TxtValor.setText("");
        }

        TxtN_Orcamento.setEnabled(false);
        cbParceiroServico.setEnabled(false);
        cbTipoServico.setEnabled(false);
        TxtValor.setEnabled(false);
        btnNovoParceiro.setEnabled(false);
        btnEditarParceiro.setEnabled(false);

        TxtTipo_pneu.setText(pneu.getTipoPneu());
        TxtStatus.setText("EM SERVIÇO -> " + statusDestino.toUpperCase());
        dataRetornoChooser.setDate(new Date());
        dataRetornoChooser.setEnabled(true);
        txtObservacoesServico.setText(osParaFinalizar.getObservacoesServico());
        TxtMotivo.setText("");
        TxtMotivo.setEnabled(true);
        txtObservacoesServico.setEnabled(true);

        String buttonText = ("ESTOQUE".equals(statusDestino)) ? "FINALIZAR OS / RETORNAR PNEU" : "FINALIZAR OS / DESCARTAR PNEU";
        btnCadastrar.setText(buttonText);
        btnCadastrar.setEnabled(true);
        for (java.awt.event.ActionListener al : btnCadastrar.getActionListeners()) {
            btnCadastrar.removeActionListener(al);
        }
        btnCadastrar.addActionListener(e -> {
            Date dataRetornoDate = dataRetornoChooser.getDate();
            if (dataRetornoDate == null) {
                JOptionPane.showMessageDialog(this, "Informe a data de retorno/descarte.", "Validação", JOptionPane.WARNING_MESSAGE);
                dataRetornoChooser.requestFocusInWindow();
                return;
            }
            if ("DESCARTADO".equals(statusDestino)) {
                int confirm = JOptionPane.showConfirmDialog(this, "ATENÇÃO: Descartar o pneu é irreversível.\nConfirma?", "Confirmar Descarte", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            String motivoFinal = TxtMotivo.getText().trim();
            String obsDaTela = txtObservacoesServico.getText().trim();
            String obsFinalParaSalvar = obsDaTela;
            if (!motivoFinal.isEmpty()) {
                obsFinalParaSalvar = obsDaTela.isEmpty() ? "Motivo (" + statusDestino.toUpperCase() + "): " + motivoFinal : obsDaTela + "\nMotivo (" + statusDestino.toUpperCase() + "): " + motivoFinal;
            }
            osParaFinalizar.setObservacoesServico(obsFinalParaSalvar.isEmpty() ? null : obsFinalParaSalvar);
            osParaFinalizar.setDataRetorno(dataRetornoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            boolean sucessoOS = osPneuDAO.atualizarOrdemServico(osParaFinalizar);
            boolean sucessoPneu = false;
            if (sucessoOS) {
                pneu.setStatusPneu(statusDestino);
                pneu.setDataRetorno(dataRetornoDate);
                sucessoPneu = pneuDAO.atualizarStatusERetorno(pneu.getId(), pneu.getStatusPneu(), pneu.getDataRetorno());
            }
            if (sucessoOS && sucessoPneu) {
                JOptionPane.showMessageDialog(this, "OS finalizada e pneu atualizado para '" + statusDestino + "'!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarTabelaPrincipalOS();
                mostrarTelaFiltroPneus();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao finalizar OS ou atualizar pneu.", "Erro", JOptionPane.ERROR_MESSAGE);
                mostrarTelaFiltroPneus();
            }
        });
    }

    private void carregarTabelaPrincipalOS() {
        modelListaOS.setRowCount(0);
        try {
            List<OrdemServicoPneu> ordensRelevantes = osPneuDAO.listarOsComPneusEmServicoOuDescartados();
            int numOrdens = (ordensRelevantes != null) ? ordensRelevantes.size() : 0;
            if (lblNumero_Ordens != null) {
                lblNumero_Ordens.setText("Ordens Listadas: " + numOrdens);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            PneuDAO pneuDAO = new PneuDAO();

            if (ordensRelevantes != null) {
                for (OrdemServicoPneu os : ordensRelevantes) {
                    String nomeParceiro = (os.getIdParceiroFk() != null) ? parceiroDAO.buscarNomePorId(os.getIdParceiroFk()) : "";
                    String nomeTipoServico = tipoServicoDAO.buscarNomePorId(os.getIdTipoServicoFk());
                    String statusOSVisual = "Em Serviço";
                    if (os.getDataRetorno() != null) {
                        statusOSVisual = "Concluída";
                        TipoServico ts = tipoServicoDAO.buscarPorId(os.getIdTipoServicoFk());
                        if (ts != null && "DESCARTE".equalsIgnoreCase(ts.getNomeTipoServico())) {
                            statusOSVisual = "Descarte Associado";
                        } else if (ts != null) {
                            statusOSVisual = "Retorno ao Estoque";
                        } else {
                            statusOSVisual = "Concluída (Tipo Inválido)";
                        }
                    }
                    String dataEnvioStr = (os.getDataEnvio() != null) ? dateFormat.format(Date.from(os.getDataEnvio().atZone(ZoneId.systemDefault()).toInstant())) : "";
                    String dataRetornoStr = (os.getDataRetorno() != null) ? dateFormat.format(Date.from(os.getDataRetorno().atZone(ZoneId.systemDefault()).toInstant())) : "";
                    Pneu pneuAssociado = pneuDAO.buscarPorId(os.getIdPneuFk());
                    String numeroFogoPneu = (pneuAssociado != null) ? pneuAssociado.getFogo() : "N/A";
                    String observacoesCompletas = os.getObservacoesServico();
                    String motivoExtraido = "";
                    String restoObservacoes = "";
                    if (observacoesCompletas != null && !observacoesCompletas.trim().isEmpty()) {
                        String[] linhas = observacoesCompletas.trim().split("\n");
                        StringBuilder restoObsBuilder = new StringBuilder();
                        boolean motivoEncontrado = false;
                        for (String linha : linhas) {
                            String linhaTrimmed = linha.trim();
                            if (!motivoEncontrado && (linhaTrimmed.toUpperCase().startsWith("MOTIVO:") || linhaTrimmed.toUpperCase().matches("MOTIVO\\s*\\([^)]*\\):.*"))) {
                                motivoExtraido = linhaTrimmed.replaceFirst("Motivo\\s*(\\(.*\\))?:\\s*", "").trim();
                                motivoEncontrado = true;
                            } else {
                                if (restoObsBuilder.length() > 0) {
                                    restoObsBuilder.append("\n");
                                }
                                restoObsBuilder.append(linha);
                            }
                        }
                        restoObservacoes = restoObsBuilder.toString().trim();
                    }
                    modelListaOS.addRow(new Object[]{
                        os.getIdServico(), os.getIdPneuFk(), numeroFogoPneu, os.getNumOrcamento(),
                        dataEnvioStr, nomeParceiro, nomeTipoServico, os.getValorServico(),
                        motivoExtraido, dataRetornoStr, statusOSVisual, restoObservacoes
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar Ordens de Serviço: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);

            if (lblNumero_Ordens != null) {
                lblNumero_Ordens.setText("Erro ao carregar ordens.");
            }
        }
    }

    private void cadastrarNovaOS() {
        if (pneuSelecionadoParaOS == null) {
            JOptionPane.showMessageDialog(this, "Nenhum pneu selecionado para cadastrar a Ordem de Serviço.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!"ESTOQUE".equals(pneuSelecionadoParaOS.getStatusPneu()) && !"Livre".equals(pneuSelecionadoParaOS.getStatusPneu())) {
            JOptionPane.showMessageDialog(this, "Não é possível criar uma nova OS para pneus que não estão no ESTOQUE ou status inicial.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String numOrcamento = TxtN_Orcamento.getText().trim();
        String valorStr = TxtValor.getText().trim();
        Date dataEnvioUtil = new Date();
        String motivoDesc = TxtMotivo.getText().trim();
        String observacoes = txtObservacoesServico.getText().trim();
        String parceiroNome = (String) cbParceiroServico.getSelectedItem();
        String tipoServicoNome = (String) cbTipoServico.getSelectedItem();

        if (parceiroNome == null || parceiroNome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione o Parceiro do Serviço.", "Validação", JOptionPane.WARNING_MESSAGE);
            cbParceiroServico.requestFocusInWindow();
            return;
        }
        if (tipoServicoNome == null || tipoServicoNome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione o Tipo de Serviço.", "Validação", JOptionPane.WARNING_MESSAGE);
            cbTipoServico.requestFocusInWindow();
            return;
        }
        if (numOrcamento.isEmpty() || "ERRO".equals(numOrcamento)) {
            JOptionPane.showMessageDialog(this, "Número do Orçamento inválido ou não gerado. Não é possível cadastrar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (osPneuDAO.existeOrdemServicoComOrcamento(numOrcamento)) {
            JOptionPane.showMessageDialog(this, "Já existe uma Ordem de Serviço com o número '" + numOrcamento + "'. (Erro na Geração Automática ou Duplicidade).", "Erro de Duplicidade", JOptionPane.WARNING_MESSAGE);
            btnCadastrar.setEnabled(false);
            return;
        }

        Double valorServico = null;
        if (!valorStr.isEmpty()) {
            try {
                Number parsedNumber = currencyFormat.parse(valorStr);
                valorServico = parsedNumber.doubleValue();
                if (valorServico < 0) {
                    JOptionPane.showMessageDialog(this, "O valor do serviço não pode ser negativo.", "Validação", JOptionPane.WARNING_MESSAGE);
                    TxtValor.requestFocusInWindow();
                    return;
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Formato de valor inválido. Digite apenas números, usando vírgula como separador decimal (Ex: 123,45).", "Validação", JOptionPane.WARNING_MESSAGE);
                TxtValor.requestFocusInWindow();
                return;
            } catch (HeadlessException ex) {

                JOptionPane.showMessageDialog(this, "Erro inesperado ao processar valor.", "Erro", JOptionPane.ERROR_MESSAGE);
                TxtValor.requestFocusInWindow();
                return;
            }
        } else {
            valorServico = null;
        }

        Integer idParceiroFk = null;
        Parceiro parceiro = parceiroDAO.buscarPorNomeExato(parceiroNome);
        if (parceiro != null) {
            idParceiroFk = parceiro.getIdParceiro();
        } else {
            System.err.println("Erro interno: Parceiro selecionado '" + parceiroNome + "' não encontrado no banco ao cadastrar OS.");
            JOptionPane.showMessageDialog(this, "Erro interno: Parceiro selecionado não encontrado no banco.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idTipoServicoFk = -1;
        TipoServico tipoServico = tipoServicoDAO.buscarPorNomeExato(tipoServicoNome);
        if (tipoServico != null) {
            idTipoServicoFk = tipoServico.getIdTipoServico();
        } else {
            System.err.println("Erro interno: Tipo de Serviço selecionado '" + tipoServicoNome + "' não encontrado no banco ao cadastrar OS.");
            JOptionPane.showMessageDialog(this, "Erro interno: Tipo de Serviço selecionado não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer idMotivoFk = null;

        OrdemServicoPneu novaOS = new OrdemServicoPneu();
        novaOS.setIdPneuFk(pneuSelecionadoParaOS.getId());
        novaOS.setNumOrcamento(numOrcamento);
        java.time.LocalDate dataEnvioLocalDate = dataEnvioUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        java.time.LocalDateTime dataEnvioLocalDateTime = dataEnvioLocalDate.atStartOfDay();
        novaOS.setDataEnvio(dataEnvioLocalDateTime);
        novaOS.setIdParceiroFk(idParceiroFk);
        novaOS.setIdTipoServicoFk(idTipoServicoFk);
        novaOS.setValorServico(valorServico);
        novaOS.setIdMotivoFk(idMotivoFk);

        String obsCompleta = observacoes.trim();
        if (!motivoDesc.isEmpty()) {
            if (obsCompleta.isEmpty()) {
                obsCompleta = "Motivo: " + motivoDesc;
            } else {
                obsCompleta = obsCompleta + "\nMotivo: " + motivoDesc;
            }
        }
        novaOS.setObservacoesServico(obsCompleta.isEmpty() ? null : obsCompleta);
        novaOS.setUsuarioRegistroServico("UsuarioAtualFixo");
        novaOS.setDataRetorno(null);

        boolean sucessoOS = osPneuDAO.inserirOrdemServico(novaOS);

        if (sucessoOS) {
            if (pneuSelecionadoParaOS != null) {
                pneuSelecionadoParaOS.setStatusPneu("EM_SERVICO");
                boolean sucessoAtualizacaoPneu = pneuDAO.atualizarStatusPneu(pneuSelecionadoParaOS.getId(), "EM_SERVICO");
                if (!sucessoAtualizacaoPneu) {
                    System.err.println("Falha ao atualizar status do pneu ID " + pneuSelecionadoParaOS.getId() + " para EM_SERVICO após cadastrar OS.");
                    JOptionPane.showMessageDialog(this, "OS cadastrada, mas falha ao atualizar status do pneu. Verifique.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }

            JOptionPane.showMessageDialog(this, "Ordem de Serviço cadastrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarTabelaPrincipalOS(); // Atualiza a tabela de OSs para mostrar a nova OS em serviço
            limparCamposCadastroOS_DadosEntrada(); // Limpa campos de entrada
            // Prepara para uma nova OS (se houver)
            int proximoOrcamentoInt = osPneuDAO.getProximoNumeroOrcamento();
            if (proximoOrcamentoInt > 0) {
                String proximoOrcamentoFormatado = String.format("%02d", proximoOrcamentoInt);
                TxtN_Orcamento.setText(proximoOrcamentoFormatado);
                TxtN_Orcamento.setEnabled(false);
                btnCadastrar.setEnabled(true); // Habilita para a próxima OS
            } else {
                TxtN_Orcamento.setText("ERRO");
                TxtN_Orcamento.setEnabled(false);
                btnCadastrar.setEnabled(false);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao cadastrar a Ordem de Serviço.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarCampoNumerico(JTextField campo) {
        campo.setHorizontalAlignment(SwingConstants.RIGHT);

        // Adiciona um KeyListener para aceitar apenas números
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    evt.consume();
                }
            }
        });

        // Adiciona um DocumentFilter para garantir que apenas números sejam inseridos
        if (campo.getDocument() instanceof AbstractDocument) {
            ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                        throws BadLocationException {
                    if (string.matches("\\d*")) {
                        super.insertString(fb, offset, string, attr);
                    }
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                        throws BadLocationException {
                    if (text.matches("\\d*")) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            });
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grupoEmpresas = new javax.swing.ButtonGroup();
        Empresas = new javax.swing.JPanel();
        MARTINS_BORGES = new javax.swing.JCheckBox();
        ALB = new javax.swing.JCheckBox();
        ENGEUDI = new javax.swing.JCheckBox();
        txtFogo = new javax.swing.JTextField();
        lbFogo = new javax.swing.JLabel();
        Panel_Tabelas = new javax.swing.JPanel();
        scrollPaneFiltroPneus = new javax.swing.JScrollPane();
        tblPneusFiltros = new javax.swing.JTable();
        scrollPaneListaOS = new javax.swing.JScrollPane();
        tbltabelaServicos = new javax.swing.JTable();
        Cadastros = new javax.swing.JPanel();
        lbTipo_pneu = new javax.swing.JLabel();
        lbValor = new javax.swing.JLabel();
        TxtValor = new javax.swing.JTextField();
        lbParceiro = new javax.swing.JLabel();
        cbParceiroServico = new javax.swing.JComboBox<>();
        lbTipo_Servico = new javax.swing.JLabel();
        cbTipoServico = new javax.swing.JComboBox<>();
        lbMotivo = new javax.swing.JLabel();
        TxtMotivo = new javax.swing.JTextField();
        lbData = new javax.swing.JLabel();
        dataRetornoChooser = new com.toedter.calendar.JDateChooser();
        lbStatus = new javax.swing.JLabel();
        TxtStatus = new javax.swing.JTextField();
        txtObservacoesServico = new javax.swing.JTextField();
        lbObs = new javax.swing.JLabel();
        btnNovoParceiro = new javax.swing.JButton();
        btnEditarParceiro = new javax.swing.JButton();
        TxtTipo_pneu = new javax.swing.JTextField();
        lbN_Orcamento = new javax.swing.JLabel();
        TxtN_Orcamento = new javax.swing.JTextField();
        btnCadastrar = new javax.swing.JButton();
        PanelContador_Ordens = new javax.swing.JPanel();
        lblNumero_Ordens = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1000, 670));
        setName("TelaCadastroDeServicos"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1200, 605));

        MARTINS_BORGES.setText("MARTINS E BORGES");

        ALB.setText("ALB");

        ENGEUDI.setText("ENGEUDI");

        lbFogo.setText("Nº Fogo");
        lbFogo.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout EmpresasLayout = new javax.swing.GroupLayout(Empresas);
        Empresas.setLayout(EmpresasLayout);
        EmpresasLayout.setHorizontalGroup(
            EmpresasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EmpresasLayout.createSequentialGroup()
                .addComponent(ENGEUDI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(84, 84, 84))
            .addGroup(EmpresasLayout.createSequentialGroup()
                .addGroup(EmpresasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ALB)
                    .addComponent(txtFogo, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbFogo)
                    .addComponent(MARTINS_BORGES, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        EmpresasLayout.setVerticalGroup(
            EmpresasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EmpresasLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(MARTINS_BORGES)
                .addGap(6, 6, 6)
                .addComponent(ALB)
                .addGap(6, 6, 6)
                .addComponent(ENGEUDI, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(lbFogo, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(txtFogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Panel_Tabelas.setName("Panel_Tabelas"); // NOI18N
        Panel_Tabelas.setPreferredSize(new java.awt.Dimension(1125, 80));
        Panel_Tabelas.setLayout(new java.awt.CardLayout());

        scrollPaneFiltroPneus.setName(""); // NOI18N

        tblPneusFiltros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "", "", "", ""
            }
        ));
        scrollPaneFiltroPneus.setViewportView(tblPneusFiltros);

        Panel_Tabelas.add(scrollPaneFiltroPneus, "filtroPneusCard");

        tbltabelaServicos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "", "", "", ""
            }
        ));
        scrollPaneListaOS.setViewportView(tbltabelaServicos);

        Panel_Tabelas.add(scrollPaneListaOS, "listaOSCard");

        lbTipo_pneu.setText("Tipo de Pneu:");

        lbValor.setText("Valor:");

        TxtValor.setName("Valor"); // NOI18N

        lbParceiro.setText("Parceiro:");

        lbTipo_Servico.setText("Tipo de Serviço");

        lbMotivo.setText("Motivo: ");

        lbData.setText("Data do Retorno: ");

        dataRetornoChooser.setDateFormatString("dd/MM/YYYY");

        lbStatus.setText("Status: ");

        lbObs.setText("OBS.:");
        lbObs.setPreferredSize(new java.awt.Dimension(38, 16));

        btnNovoParceiro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/+.png"))); // NOI18N
        btnNovoParceiro.setMaximumSize(new java.awt.Dimension(25, 25));
        btnNovoParceiro.setMinimumSize(new java.awt.Dimension(25, 25));
        btnNovoParceiro.setPreferredSize(new java.awt.Dimension(25, 25));
        btnNovoParceiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoParceiroActionPerformed(evt);
            }
        });

        btnEditarParceiro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/pencil.png"))); // NOI18N
        btnEditarParceiro.setMaximumSize(new java.awt.Dimension(25, 25));
        btnEditarParceiro.setMinimumSize(new java.awt.Dimension(25, 25));
        btnEditarParceiro.setPreferredSize(new java.awt.Dimension(25, 25));
        btnEditarParceiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarParceiroActionPerformed(evt);
            }
        });

        TxtTipo_pneu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        javax.swing.GroupLayout CadastrosLayout = new javax.swing.GroupLayout(Cadastros);
        Cadastros.setLayout(CadastrosLayout);
        CadastrosLayout.setHorizontalGroup(
            CadastrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CadastrosLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(CadastrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CadastrosLayout.createSequentialGroup()
                        .addGroup(CadastrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbParceiro, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbParceiroServico, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNovoParceiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEditarParceiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(CadastrosLayout.createSequentialGroup()
                        .addComponent(lbMotivo)
                        .addGap(75, 75, 75)
                        .addComponent(lbData)
                        .addGap(27, 27, 27)
                        .addComponent(lbStatus))
                    .addGroup(CadastrosLayout.createSequentialGroup()
                        .addComponent(TxtMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(dataRetornoChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(TxtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(CadastrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CadastrosLayout.createSequentialGroup()
                        .addGroup(CadastrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbTipo_pneu)
                            .addComponent(TxtTipo_pneu, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(CadastrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbTipo_Servico)
                            .addComponent(cbTipoServico, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(CadastrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbValor)
                            .addComponent(TxtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lbObs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtObservacoesServico, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        CadastrosLayout.setVerticalGroup(
            CadastrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CadastrosLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(CadastrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(CadastrosLayout.createSequentialGroup()
                        .addComponent(lbParceiro)
                        .addGap(4, 4, 4)
                        .addComponent(cbParceiroServico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnNovoParceiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditarParceiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(CadastrosLayout.createSequentialGroup()
                        .addComponent(lbTipo_Servico)
                        .addGap(4, 4, 4)
                        .addComponent(cbTipoServico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(CadastrosLayout.createSequentialGroup()
                        .addComponent(lbTipo_pneu)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TxtTipo_pneu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(CadastrosLayout.createSequentialGroup()
                        .addComponent(lbValor)
                        .addGap(4, 4, 4)
                        .addComponent(TxtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CadastrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(CadastrosLayout.createSequentialGroup()
                        .addGroup(CadastrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbMotivo)
                            .addComponent(lbData)
                            .addComponent(lbStatus))
                        .addGap(4, 4, 4)
                        .addGroup(CadastrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TxtMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dataRetornoChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TxtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(CadastrosLayout.createSequentialGroup()
                        .addComponent(lbObs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtObservacoesServico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        lbN_Orcamento.setText("N° Orçamento: ");

        btnCadastrar.setText("CADASTRAR");

        lblNumero_Ordens.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N

        javax.swing.GroupLayout PanelContador_OrdensLayout = new javax.swing.GroupLayout(PanelContador_Ordens);
        PanelContador_Ordens.setLayout(PanelContador_OrdensLayout);
        PanelContador_OrdensLayout.setHorizontalGroup(
            PanelContador_OrdensLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelContador_OrdensLayout.createSequentialGroup()
                .addComponent(lblNumero_Ordens)
                .addContainerGap(349, Short.MAX_VALUE))
        );
        PanelContador_OrdensLayout.setVerticalGroup(
            PanelContador_OrdensLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelContador_OrdensLayout.createSequentialGroup()
                .addComponent(lblNumero_Ordens)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(btnCadastrar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(Empresas, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Cadastros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Panel_Tabelas, javax.swing.GroupLayout.PREFERRED_SIZE, 1073, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbN_Orcamento)
                            .addComponent(TxtN_Orcamento, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(296, 296, 296)
                        .addComponent(PanelContador_Ordens, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(8, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Empresas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Cadastros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbN_Orcamento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(TxtN_Orcamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addComponent(Panel_Tabelas, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                        .addComponent(btnCadastrar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(83, 83, 83))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PanelContador_Ordens, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        Panel_Tabelas.getAccessibleContext().setAccessibleName("Panel_Tabelas");

        getAccessibleContext().setAccessibleName("TelaCadastroDeServicos");

        setSize(new java.awt.Dimension(1110, 662));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tabelaServicosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelaServicosMouseClicked

    }//GEN-LAST:event_tabelaServicosMouseClicked

    private void btnNovoParceiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoParceiroActionPerformed

        String nomeNovoParceiro = JOptionPane.showInputDialog(
                this,
                "Digite o nome do novo Parceiro:",
                "Novo Parceiro",
                JOptionPane.PLAIN_MESSAGE
        );

        if (nomeNovoParceiro != null && !nomeNovoParceiro.trim().isEmpty()) {
            nomeNovoParceiro = nomeNovoParceiro.trim();

            br.com.martins_borges.model.Parceiro parceiroCriado = parceiroDAO.inserirParceiro(nomeNovoParceiro);

            if (parceiroCriado != null) {
                popularComboBoxesServico();
                cbParceiroServico.setSelectedItem(parceiroCriado.getNomeParceiro());

                JOptionPane.showMessageDialog(this,
                        "Parceiro '" + parceiroCriado.getNomeParceiro() + "' cadastrado!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Nome do parceiro não pode ser vazio.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
        }

    }//GEN-LAST:event_btnNovoParceiroActionPerformed

    private void btnEditarParceiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarParceiroActionPerformed

        Object itemSelecionadoObj = cbParceiroServico.getSelectedItem();

        if (itemSelecionadoObj == null || itemSelecionadoObj.toString().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um Parceiro na lista para editar.", "Nenhum Parceiro Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomeAntigo = itemSelecionadoObj.toString();

        String nomeNovo = (String) JOptionPane.showInputDialog(
                this,
                "Digite o NOVO nome para o Parceiro:",
                "Editar Parceiro",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                nomeAntigo
        );

        if (nomeNovo != null && !nomeNovo.trim().isEmpty() && !nomeNovo.trim().equalsIgnoreCase(nomeAntigo)) {
            nomeNovo = nomeNovo.trim();

            br.com.martins_borges.model.Parceiro parceiroParaEditar = parceiroDAO.buscarPorNomeExato(nomeAntigo);

            if (parceiroParaEditar == null) {
                JOptionPane.showMessageDialog(this, "Não foi possível encontrar o Parceiro original ('" + nomeAntigo + "') no banco para atualizar.", "Erro Interno", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean sucesso = parceiroDAO.atualizarNomeParceiro(parceiroParaEditar.getIdParceiro(), nomeNovo);

            if (sucesso) {
                popularComboBoxesServico();
                cbParceiroServico.setSelectedItem(nomeNovo);

                JOptionPane.showMessageDialog(this, "Parceiro atualizado para '" + nomeNovo + "'!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (nomeNovo != null && !nomeNovo.trim().isEmpty() && nomeNovo.trim().equalsIgnoreCase(nomeAntigo)) {
            JOptionPane.showMessageDialog(this, "Nenhuma alteração no nome do Parceiro.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_btnEditarParceiroActionPerformed

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox ALB;
    private javax.swing.JPanel Cadastros;
    private javax.swing.JCheckBox ENGEUDI;
    private javax.swing.JPanel Empresas;
    private javax.swing.JCheckBox MARTINS_BORGES;
    private javax.swing.JPanel PanelContador_Ordens;
    private javax.swing.JPanel Panel_Tabelas;
    private javax.swing.JTextField TxtMotivo;
    private javax.swing.JTextField TxtN_Orcamento;
    private javax.swing.JTextField TxtStatus;
    private javax.swing.JTextField TxtTipo_pneu;
    private javax.swing.JTextField TxtValor;
    private javax.swing.JButton btnCadastrar;
    private javax.swing.JButton btnEditarParceiro;
    private javax.swing.JButton btnNovoParceiro;
    private javax.swing.JComboBox<String> cbParceiroServico;
    private javax.swing.JComboBox<String> cbTipoServico;
    private com.toedter.calendar.JDateChooser dataRetornoChooser;
    private javax.swing.ButtonGroup grupoEmpresas;
    private javax.swing.JLabel lbData;
    private javax.swing.JLabel lbFogo;
    private javax.swing.JLabel lbMotivo;
    private javax.swing.JLabel lbN_Orcamento;
    private javax.swing.JLabel lbObs;
    private javax.swing.JLabel lbParceiro;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbTipo_Servico;
    private javax.swing.JLabel lbTipo_pneu;
    private javax.swing.JLabel lbValor;
    private javax.swing.JLabel lblNumero_Ordens;
    private javax.swing.JScrollPane scrollPaneFiltroPneus;
    private javax.swing.JScrollPane scrollPaneListaOS;
    private javax.swing.JTable tblPneusFiltros;
    private javax.swing.JTable tbltabelaServicos;
    private javax.swing.JTextField txtFogo;
    private javax.swing.JTextField txtObservacoesServico;
    // End of variables declaration//GEN-END:variables
// </editor-fold>  
}
