package br.com.martins_borges.telas;

import br.com.martins_borges.dal.ModeloPneuDAO;
import br.com.martins_borges.dal.MedidaPneuDAO;
import br.com.martins_borges.dal.TipoPneuDAO;
import br.com.martins_borges.dal.FabricanteDAO;
import br.com.martins_borges.dal.FornecedorDAO;
import br.com.martins_borges.dal.PneuDAO;
import br.com.martins_borges.model.Fabricante;
import br.com.martins_borges.model.Fornecedor;
import br.com.martins_borges.model.MedidaPneu;
import br.com.martins_borges.model.ModeloPneu;
import br.com.martins_borges.model.Pneu;
import br.com.martins_borges.model.TipoPneu;
import br.com.martins_borges.utilitarios.Utilitarios;
import br.com.martins_borges.utilitarios.TamanhoTabela;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
//import util.TamanhoTabela;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

// <editor-fold defaultstate="collapsed" desc="Declarações de Atributos">
public class TelaCadastroPneus extends javax.swing.JDialog {

    private final FornecedorDAO fornecedorDAO = new FornecedorDAO();
    private final FabricanteDAO fabricanteDAO = new FabricanteDAO();
    private final ModeloPneuDAO modeloPneuDAO = new ModeloPneuDAO();
    private final MedidaPneuDAO medidaPneuDAO = new MedidaPneuDAO();
    private final TipoPneuDAO tipoPneuDAO = new TipoPneuDAO();
    private final PneuDAO pneuDAO = new PneuDAO();
    private ButtonGroup grupoEmpresas;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private boolean modoEdicao = false;
    private int idPneuEmEdicao = -1;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final int ID_EMPRESA_MARTINS_BORGES = 1;
    private static final int ID_EMPRESA_ALB = 2;
    private static final int ID_EMPRESA_ENGEUDI = 3;

    private final List<Pneu> pneusExibidosNoFiltro = new ArrayList<>();
// </editor-fold>
    
    public TelaCadastroPneus(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        aplicarFiltroMaiusculasAosCampos();
        adicionarListenersValor();
        criarGrupoEmpresas();
        configuracoesIniciais();
        Utilitarios.aplicarFormatacaoCampos(this.getContentPane());

        // Define um tamanho preferencial e centraliza a janela.
        definirTamanhoEPosicao();
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

                // Adiciona um deslocamento para ajustar a posição.
                x -= 50; // Move 50 pixels para a esquerda para um melhor ajuste visual.
                y += 30; // Move 30 pixels para baixo para não cobrir o menu.

                // Garante que a janela não fique com coordenadas negativas.
                x = Math.max(0, x);
                y = Math.max(0, y);

                setLocation(x, y);
            }
        });
    }

    private void aplicarFiltroMaiusculasAosCampos() {

        if (TxtObs != null) {
            aplicarFiltroMaiusculas(TxtObs);
        }
        if (TxtDot != null) {
            aplicarFiltroMaiusculas(TxtDot);
        }
        aplicarFiltroEditorComboBox(CbFornecedor);
        aplicarFiltroEditorComboBox(CbFabricante);
        aplicarFiltroEditorComboBox(CbModelo);
        aplicarFiltroEditorComboBox(CbMedida);
        aplicarFiltroEditorComboBox(CbTipoPneu);

    }

    private void aplicarFiltroEditorComboBox(JComboBox<?> comboBox) {
        if (comboBox == null) {
            return;
        }
        try {
            Component editorComp = comboBox.getEditor().getEditorComponent();
            if (editorComp instanceof JTextComponent) {
                aplicarFiltroMaiusculas((JTextComponent) editorComp);
            }
        } catch (Exception e) {
            System.err.println("ERRO aplicar filtro editor Combo: " + e.getMessage());
        }
    }

    private void aplicarFiltroMaiusculas(JTextComponent textComponent) {
        if (textComponent != null && textComponent.getDocument() instanceof AbstractDocument) {
            try {

            } catch (Exception e) {
                System.err.println("ERRO setFilter Maiúsculas: " + e.getMessage());
            }
        } else {
        }
    }

    private List<String> obterPrefixosPermitidos(int idEmpresa) {
        switch (idEmpresa) {
            case 1:

                return List.of("275", "295", "900", "1000", "1100");
            case 2:

                return List.of("275", "295");
            case 3:

                return List.of("275", "295", "1200");
            default:
                return new ArrayList<>();
        }
    }

    private void atualizarOpcoesMedida() {
        List<String> prefixosPermitidos = new ArrayList<>();
        boolean algumaEmpresaSelecionada = false;

        if (MARTINS_BORGES.isSelected()) {
            prefixosPermitidos = List.of("275", "295", "900", "1000", "1100");
            algumaEmpresaSelecionada = true;
        } else if (ALB.isSelected()) {
            prefixosPermitidos = List.of("275", "295");
            algumaEmpresaSelecionada = true;
        } else if (ENGEUDI.isSelected()) {
            prefixosPermitidos = List.of("275", "295", "1200");
            algumaEmpresaSelecionada = true;

        }

        if (!algumaEmpresaSelecionada) {
            CbMedida.setEnabled(false);
           popularComboBoxMedida(new ArrayList<>());
            return;
        }

        List<String> todasAsMedidas = medidaPneuDAO.listarNomes();
        List<String> medidasFiltradas = new ArrayList<>();

        if (todasAsMedidas != null) {
            for (String medidaCompletaOriginal : todasAsMedidas) {
                String medidaTrimmed = medidaCompletaOriginal.trim();
                for (String prefixo : prefixosPermitidos) {

                    if (medidaTrimmed.startsWith(prefixo)) {
                        medidasFiltradas.add(medidaCompletaOriginal);
                        break;
                    }
                }
            }
        }

        CbMedida.setEnabled(true);
        popularComboBoxMedida(medidasFiltradas);
    }

    private void adicionarListenersValor() {
        if (TxtValor == null) {
            System.err.println("ERRO: TxtValor não inicializado!");
            return;
        }
        TxtValor.setHorizontalAlignment(SwingConstants.RIGHT);
        TxtValor.setText("");

        TxtValor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                formatarValorAoSair();
            }

        });

        TxtValor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                String texto = TxtValor.getText();

                if (!Character.isDigit(c) && (c != ',' || texto.contains(","))
                        && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE && !Character.isISOControl(c)) {
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
            String textoNumerico = texto.replace(".", "").replace(",", ".");
            if (textoNumerico.isEmpty()) {
                TxtValor.setText("");
                return;
            }
            double valor = Double.parseDouble(textoNumerico);
            TxtValor.setText(currencyFormat.format(valor));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor monetário inválido: " + texto,
                    "Formato Inválido", JOptionPane.WARNING_MESSAGE);
            TxtValor.setText("");
        }
    }

    private void criarGrupoEmpresas() {
        grupoEmpresas = new ButtonGroup();

        java.awt.event.ActionListener empresaListener = evt -> {
            atualizarOpcoesMedida();
            if (Novo.isSelected()) {
                sugerirProximoFogo();
            } else {
                aplicarFiltroPneus();
            }
        };

        MARTINS_BORGES.setActionCommand("MARTINS_BORGES");
        MARTINS_BORGES.addActionListener(empresaListener);
        grupoEmpresas.add(MARTINS_BORGES);
        ALB.setActionCommand("ALB");
        ALB.addActionListener(empresaListener);
        grupoEmpresas.add(ALB);

        ENGEUDI.setActionCommand("ENGEUDI");
        ENGEUDI.addActionListener(empresaListener);
        grupoEmpresas.add(ENGEUDI);
    }

    
    private void adicionarListenerClickFora() {
        // Adiciona um listener global que captura TODOS os cliques na janela
        java.awt.event.MouseAdapter listenerGlobal = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // SIMPLES: Sempre desmarca, exceto se clicou diretamente nos checkboxes
                java.awt.Component clickedComponent = evt.getComponent();
                
                // Se clicou diretamente em um dos checkboxes, NÃO desmarca
                if (clickedComponent == MARTINS_BORGES || 
                    clickedComponent == ALB || 
                    clickedComponent == ENGEUDI) {
                    return; // Não faz nada se clicou nos checkboxes
                }
                
                // QUALQUER OUTRO CLIQUE = DESMARCA
                if (grupoEmpresas != null) {
                    grupoEmpresas.clearSelection();
                    atualizarOpcoesMedida();
                    aplicarFiltroPneus();
                }
            }
        };
        
        // Adiciona o listener em TODOS os componentes principais
        this.addMouseListener(listenerGlobal);
        if (jCadastro1 != null) jCadastro1.addMouseListener(listenerGlobal);
        if (jCadastro2 != null) jCadastro2.addMouseListener(listenerGlobal);
        if (Empresas != null) Empresas.addMouseListener(listenerGlobal);
    }
    
    
    private boolean isClickOnCheckbox(java.awt.event.MouseEvent evt) {
        java.awt.Point clickPoint = evt.getPoint();
        
        // Converte as coordenadas do clique para as coordenadas dos checkboxes
        if (MARTINS_BORGES != null && MARTINS_BORGES.isVisible()) {
            java.awt.Point checkboxLocation = javax.swing.SwingUtilities.convertPoint(
                this, clickPoint, Empresas);
            if (MARTINS_BORGES.getBounds().contains(checkboxLocation)) {
                return true;
            }
        }
        
        if (ALB != null && ALB.isVisible()) {
            java.awt.Point checkboxLocation = javax.swing.SwingUtilities.convertPoint(
                this, clickPoint, Empresas);
            if (ALB.getBounds().contains(checkboxLocation)) {
                return true;
            }
        }
        
        if (ENGEUDI != null && ENGEUDI.isVisible()) {
            java.awt.Point checkboxLocation = javax.swing.SwingUtilities.convertPoint(
                this, clickPoint, Empresas);
            if (ENGEUDI.getBounds().contains(checkboxLocation)) {
                return true;
            }
        }
        
        return false;
    }

    
   private boolean isClickOnCheckboxInPanel(java.awt.event.MouseEvent evt, javax.swing.JPanel sourcePanel) {
        java.awt.Point clickPoint = evt.getPoint();
        
        // Verifica cada checkbox individualmente
        if (MARTINS_BORGES != null && MARTINS_BORGES.isVisible()) {
            java.awt.Point checkboxLocation = javax.swing.SwingUtilities.convertPoint(
                sourcePanel, clickPoint, Empresas);
            if (MARTINS_BORGES.getBounds().contains(checkboxLocation)) {
                return true;
            }
        }
        
        if (ALB != null && ALB.isVisible()) {
            java.awt.Point checkboxLocation = javax.swing.SwingUtilities.convertPoint(
                sourcePanel, clickPoint, Empresas);
            if (ALB.getBounds().contains(checkboxLocation)) {
                return true;
            }
        }
        
        if (ENGEUDI != null && ENGEUDI.isVisible()) {
            java.awt.Point checkboxLocation = javax.swing.SwingUtilities.convertPoint(
                sourcePanel, clickPoint, Empresas);
            if (ENGEUDI.getBounds().contains(checkboxLocation)) {
                return true;
            }
        }
        
        return false;
    }

    private void configuracoesIniciais() {
        adicionarListenerClickFora();

        popularComboBoxFornecedor();
        popularComboBoxFabricante();
        popularComboBoxModelo();
        configurarCampoNumerico(txtFogo);
        configurarCampoNumerico(TxtDot);
        configurarCampoNumerico(TxtProfud);
        configurarCampoNumerico(TxtProjecao);
        configurarCampoNumerico(TxtRecap);
        popularComboBoxTipoPneu();

        try {
            AutoCompleteDecorator.decorate(CbFornecedor);
            AutoCompleteDecorator.decorate(CbFabricante);
            AutoCompleteDecorator.decorate(CbModelo);
            AutoCompleteDecorator.decorate(CbMedida);
            AutoCompleteDecorator.decorate(CbTipoPneu);
        } catch (NoClassDefFoundError e) {
            System.err.println("AVISO: Biblioteca SwingX (AutoCompleteDecorator) não encontrada.");
        } catch (Exception e) {
            System.err.println("Erro ao aplicar AutoCompleteDecorator: " + e.getMessage());
        }

        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID", "EMPR", "FOGO", "FORNECEDOR", "VALOR", "FABRICANTE", "TIPO PNEU", "MODELO", "DOT", "MEDIDA", "PROFUND.", "DATA", "RECAP.", "PROJ. KM", "OBS"
                }
        ) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        Tabela_Exibicao.setModel(tableModel);

        Tabela_Exibicao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tabela_ExibicaoMouseClicked(evt);
            }
        });

        try {
            KeyStroke backspaceKey = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
            KeyStroke deleteKey = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
            String deletePrevAction = DefaultEditorKit.deletePrevCharAction;
            String deleteNextAction = DefaultEditorKit.deleteNextCharAction;
            configurarBindingsEditor(CbFornecedor, backspaceKey, deletePrevAction, deleteKey, deleteNextAction);
            configurarBindingsEditor(CbFabricante, backspaceKey, deletePrevAction, deleteKey, deleteNextAction);
            configurarBindingsEditor(CbModelo, backspaceKey, deletePrevAction, deleteKey, deleteNextAction);
            configurarBindingsEditor(CbMedida, backspaceKey, deletePrevAction, deleteKey, deleteNextAction);
            configurarBindingsEditor(CbTipoPneu, backspaceKey, deletePrevAction, deleteKey, deleteNextAction);
        } catch (Exception e) {
            System.err.println("Erro ao configurar Key Bindings dos ComboBoxes: " + e.getMessage());
        }

        Tabela_Exibicao.setShowGrid(true);
        Tabela_Exibicao.setGridColor(java.awt.Color.LIGHT_GRAY);
        Tabela_Exibicao.setAutoCreateRowSorter(true);

        txtFogo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (!Novo.isSelected()) aplicarFiltroPneus();
            }
            public void removeUpdate(DocumentEvent e) {
                if (!Novo.isSelected()) aplicarFiltroPneus();
            }
            public void changedUpdate(DocumentEvent e) {
                // Não é necessário para JTextField simples
            }
        });

        Novo.addActionListener(evt -> {
            if (Novo.isSelected()) {
                limparCamposParaNovoCadastro();
                sugerirProximoFogo();
                txtFogo.setEditable(true);
                aplicarFiltroPneus(); // Limpa a tabela
            } else {
                txtFogo.setText("");
                txtFogo.setEditable(true);
                aplicarFiltroPneus();
            }
        });

        aplicarFiltroPneus();
        btnExcluir.setEnabled(false);
        CbMedida.setEnabled(false);
    }

    private void configurarBindingsEditor(JComboBox<?> comboBox,
            KeyStroke backspace,
            String backspaceActionKey,
            KeyStroke delete,
            String deleteActionKey) {

        Component editorComp = comboBox.getEditor().getEditorComponent();
        if (editorComp instanceof JTextComponent) {
            JTextComponent editor = (JTextComponent) editorComp;
            InputMap inputMap = editor.getInputMap(JComponent.WHEN_FOCUSED);

            inputMap.put(backspace, backspaceActionKey);
            inputMap.put(delete, deleteActionKey);

        } else {

            System.err.println("WARN: Editor de " + (comboBox.getName()
                    != null ? comboBox.getName() : "ComboBox sem nome")
                    + " não é JTextComponent, bindings não aplicados.");

            btnExcluir.setEnabled(false);

        }

        CbFornecedor.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if (e.getStateChange() == ItemEvent.SELECTED) {

                    boolean habilitar = CbFornecedor.getSelectedIndex() > 0;
                    btnEditarFornecedor.setEnabled(habilitar);
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {

                    btnEditarFornecedor.setEnabled(false);
                }
            }
        });

        btnEditarFornecedor.setEnabled(false);

        CbFabricante.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    boolean habilitar = CbFabricante.getSelectedIndex() > 0;
                    btnEditarFabricante.setEnabled(habilitar);
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    btnEditarFabricante.setEnabled(false);
                }
            }
        });

        btnEditarFabricante.setEnabled(false);

        CbModelo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    boolean habilitar = CbModelo.getSelectedIndex() > 0;
                    btnEditarModelo.setEnabled(habilitar);
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    btnEditarModelo.setEnabled(false);
                }
            }
        });

        btnEditarModelo.setEnabled(false);

        CbMedida.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if (e.getStateChange() == ItemEvent.SELECTED && CbMedida.isEnabled()) {
                    boolean habilitar = CbMedida.getSelectedIndex() > 0;
                    btnEditarMedida.setEnabled(habilitar);
                } else {

                    btnEditarMedida.setEnabled(false);
                }
            }
        });

        btnEditarMedida.setEnabled(false);
        CbMedida.setEnabled(false);

        CbTipoPneu.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    boolean habilitar = CbTipoPneu.getSelectedIndex() > 0;
                    btnEditarTipoPneu.setEnabled(habilitar);
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    btnEditarTipoPneu.setEnabled(false);
                }
            }
        });

        btnEditarTipoPneu.setEnabled(false);

    }

    private void popularComboBoxFornecedor() {
    List<String> nomes = fornecedorDAO.listarNomes();
    CbFornecedor.removeAllItems();
    
    if (!nomes.isEmpty()) {
        for (String nome : nomes) {
            CbFornecedor.addItem(nome);
        }
        CbFornecedor.setSelectedIndex(-1); 
    } else {
        CbFornecedor.setSelectedIndex(-1); 
    }
}
private void popularComboBoxFabricante() {
    List<String> nomes = fabricanteDAO.listarNomes();
    CbFabricante.removeAllItems();

    if (!nomes.isEmpty()) {
        for (String nome : nomes) {
            CbFabricante.addItem(nome);
        }
        CbFabricante.setSelectedIndex(-1); // Nenhum item selecionado inicialmente
    } else {
        CbFabricante.setSelectedIndex(-1); // Mantém vazio
    }
}

   private void popularComboBoxModelo() {
    List<String> nomes = modeloPneuDAO.listarNomes();
    CbModelo.removeAllItems();

    if (!nomes.isEmpty()) {
        for (String nome : nomes) {
            CbModelo.addItem(nome);
        }
        CbModelo.setSelectedIndex(-1); // Nenhum item selecionado inicialmente
    } else {
        CbModelo.setSelectedIndex(-1); // Mantém vazio
    }
}

 private void popularComboBoxMedida(List<String> medidasPermitidas) {
    CbMedida.removeAllItems(); // Limpa todos os itens

    if (medidasPermitidas != null && !medidasPermitidas.isEmpty()) {
        for (String medida : medidasPermitidas) {
            CbMedida.addItem(medida);
        }
    }
    
    CbMedida.setSelectedIndex(-1); // Mantém vazio
}

   private void popularComboBoxTipoPneu() {
    List<String> nomes = tipoPneuDAO.listarNomes();
    CbTipoPneu.removeAllItems();

    if (nomes != null && !nomes.isEmpty()) {
        for (String nome : nomes) {
            CbTipoPneu.addItem(nome);
        }
        CbTipoPneu.setSelectedIndex(-1); 
    } else {
        CbTipoPneu.setSelectedIndex(-1); 
    }
}
   
  private void configurarCampoNumerico(JTextField campo) {
    campo.setHorizontalAlignment(SwingConstants.LEFT);
    
    // Adiciona um KeyListener para aceitar apenas números E LIMITAR A 5 CARACTERES
    campo.addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent evt) {
            char c = evt.getKeyChar();
            // Bloqueia se não for número ou se já tiver 5 caracteres
            if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                evt.consume();
            } else if (Character.isDigit(c) && campo.getText().length() >= 5) {
                evt.consume(); // Bloqueia se já tiver 5 caracteres
            }
        }
    });
    
    // Adiciona um DocumentFilter para garantir que apenas números sejam inseridos
    if (campo.getDocument() instanceof AbstractDocument) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string.matches("d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text.matches("d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}
   
     private void formatarTabela() {
        // Define as larguras das colunas
        int[] larguras = {45, 60, 70, 150, 110, 120, 90, 100, 70, 110, 80, 90, 70, 90, 250};

        // Define as cores de fundo para cada coluna
        Color[] pneuTableColors = {
            TamanhoTabela.HEADER_COLORS[0], // ID - Rosa
            TamanhoTabela.HEADER_COLORS[1], // EMPR - Azul claro
            TamanhoTabela.HEADER_COLORS[2], // FOGO - Cáqui
            TamanhoTabela.HEADER_COLORS[3], // FORNECEDOR - Verde-claro
            TamanhoTabela.HEADER_COLORS[4], // VALOR - Pêssego
            TamanhoTabela.HEADER_COLORS[5], // FABRICANTE - Lavanda
            TamanhoTabela.HEADER_COLORS[6], // TIPO PNEU - Trigo
            TamanhoTabela.HEADER_COLORS[7], // MODELO - Azul acinzentado
            TamanhoTabela.HEADER_COLORS[8], // DOT - Orquídea
            TamanhoTabela.HEADER_COLORS[9], // MEDIDA - Salmão claro
            TamanhoTabela.HEADER_COLORS[0], // PROFUND. - Rosa (repetindo)
            TamanhoTabela.HEADER_COLORS[1], // DATA - Azul claro (repetindo)
            TamanhoTabela.HEADER_COLORS[2], // RECAP. - Cáqui (repetindo)
            TamanhoTabela.HEADER_COLORS[3], // PROJ. KM - Verde-claro (repetindo)
            TamanhoTabela.HEADER_COLORS[4]  // OBS. - Pêssego (repetindo)
        };

        TamanhoTabela.configurar(Tabela_Exibicao, larguras, pneuTableColors);

        // Renderers específicos que precisam ser mantidos
        TamanhoTabela.CenterRenderer centerRenderer = new TamanhoTabela.CenterRenderer();
        TamanhoTabela.CurrencyRenderer currencyRenderer = new TamanhoTabela.CurrencyRenderer();
        TamanhoTabela.DateRenderer dateRenderer = new TamanhoTabela.DateRenderer();

        TableColumnModel columnModel = Tabela_Exibicao.getColumnModel();
        try {
            columnModel.getColumn(0).setCellRenderer(centerRenderer); // ID
            columnModel.getColumn(1).setCellRenderer(centerRenderer); // EMPR
            columnModel.getColumn(2).setCellRenderer(centerRenderer); // FOGO
            columnModel.getColumn(4).setCellRenderer(currencyRenderer); // VALOR
            columnModel.getColumn(8).setCellRenderer(centerRenderer); // DOT
            columnModel.getColumn(9).setCellRenderer(centerRenderer); // MEDIDA
            columnModel.getColumn(10).setCellRenderer(centerRenderer); // PROFUND.
            columnModel.getColumn(11).setCellRenderer(dateRenderer); // DATA
            columnModel.getColumn(12).setCellRenderer(centerRenderer); // RECAP.
            columnModel.getColumn(13).setCellRenderer(centerRenderer); // PROJ. KM
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Erro ao configurar colunas da tabela: " + e.getMessage());
        }
    }

    private void aplicarFiltroPneus() {
        if (Novo.isSelected()) {
            DefaultTableModel modeloTabela = (DefaultTableModel) Tabela_Exibicao.getModel();
            modeloTabela.setNumRows(0);
            return;
        }

        DefaultTableModel modeloTabela = (DefaultTableModel) Tabela_Exibicao.getModel();
        modeloTabela.setNumRows(0);

        List<Integer> idsEmpresasSelecionadas = new ArrayList<>();
        if (MARTINS_BORGES.isSelected()) idsEmpresasSelecionadas.add(ID_EMPRESA_MARTINS_BORGES);
        if (ALB.isSelected()) idsEmpresasSelecionadas.add(ID_EMPRESA_ALB);
        if (ENGEUDI.isSelected()) idsEmpresasSelecionadas.add(ID_EMPRESA_ENGEUDI);

        String fogoDigitado = txtFogo.getText().trim();

        List<Pneu> pneusFiltradosDoBanco = new ArrayList<>();
        if (idsEmpresasSelecionadas.isEmpty() && fogoDigitado.isEmpty()) {
             pneusFiltradosDoBanco = pneuDAO.listarTodosPneus();
        } else if (idsEmpresasSelecionadas.isEmpty()) {
            pneusFiltradosDoBanco.addAll(pneuDAO.buscarPneusPorFiltro(0, fogoDigitado));
        } else {
            for (int idEmpresa : idsEmpresasSelecionadas) {
                pneusFiltradosDoBanco.addAll(pneuDAO.buscarPneusPorFiltro(idEmpresa, fogoDigitado));
            }
        }

        if (pneusFiltradosDoBanco != null) {
            for (Pneu p : pneusFiltradosDoBanco) {
                modeloTabela.addRow(new Object[]{
                    p.getId(),
                    p.getIdEmpresaProprietaria(),
                    p.getFogo(),
                    p.getFornecedor(),
                    p.getValor(),
                    p.getFabricante(),
                    p.getTipoPneu(),
                    p.getModelo(),
                    p.getDot(),
                    p.getMedida(),
                    p.getProfundidade(),
                    p.getDataCadastro(),
                    p.getnRecapagens(),
                    p.getProjetadoKm(),
                    p.getObservacoes()
                });
            }
        }

        formatarTabela();
        Tabela_Exibicao.clearSelection();
    }

    private void limparCampos() {
        if (grupoEmpresas != null) {
            grupoEmpresas.clearSelection();
        }
        txtFogo.setText("");
        TxtDot.setText("");
        TxtProfud.setText("");
        TxtRecap.setText("");
        TxtProjecao.setText("");
        TxtObs.setText("");
        TxtValor.setText("");

        CbFornecedor.setSelectedIndex(-1);
        CbFabricante.setSelectedIndex(-1);
        CbTipoPneu.setSelectedIndex(-1);
        CbModelo.setSelectedIndex(-1);
        CbMedida.setSelectedIndex(-1);

        modoEdicao = false;
        idPneuEmEdicao = -1;
        atualizarEstadoBotoes();
        Tabela_Exibicao.clearSelection();
    }
    
    private void limparCamposParaNovoCadastro() {
        txtFogo.setText("");
        TxtDot.setText("");
        TxtProfud.setText("");
        TxtRecap.setText("");
        TxtProjecao.setText("");
        TxtObs.setText("");
        TxtValor.setText("");

        CbFornecedor.setSelectedIndex(-1);
        CbFabricante.setSelectedIndex(-1);
        CbTipoPneu.setSelectedIndex(-1);
        CbModelo.setSelectedIndex(-1);
        CbMedida.setSelectedIndex(-1);

        modoEdicao = false;
        idPneuEmEdicao = -1;
        atualizarEstadoBotoes();
        Tabela_Exibicao.clearSelection();
    }

    private void carregarDadosParaEdicao(int linhaSelecionadaNaView) {
        try {
            DefaultTableModel modeloTabela = (DefaultTableModel) Tabela_Exibicao.getModel();
            int linhaModelo = Tabela_Exibicao.convertRowIndexToModel(linhaSelecionadaNaView);
            int id = (Integer) modeloTabela.getValueAt(linhaModelo, 0);
            this.idPneuEmEdicao = id;

            int idEmpresa = (Integer) modeloTabela.getValueAt(linhaModelo, 1);
            String fogo = modeloTabela.getValueAt(linhaModelo, 2).toString();
            String fornecedor = (String) modeloTabela.getValueAt(linhaModelo, 3);
            Double valor = null;
            Object valorObj = modeloTabela.getValueAt(linhaModelo, 4);
            if (valorObj instanceof Number) {
                valor = ((Number) valorObj).doubleValue();
            } else if (valorObj != null) {
                try {
                    Number vn = currencyFormat.parse(valorObj.toString());
                    valor = vn.doubleValue();
                } catch (ParseException pe) {
                    System.err.println("WARN: Parse VALOR tabela:" + valorObj);
                    valor = 0.0;
                }
            }
            String fabricante = (String) modeloTabela.getValueAt(linhaModelo, 5);
            String tipoPneu = (String) modeloTabela.getValueAt(linhaModelo, 6);
            String modelo = (String) modeloTabela.getValueAt(linhaModelo, 7);
            String dot = (String) modeloTabela.getValueAt(linhaModelo, 8);
            String medida = (String) modeloTabela.getValueAt(linhaModelo, 9);
            Double profundidade = null;
            Object profObj = modeloTabela.getValueAt(linhaModelo, 10);
            if (profObj instanceof Number) {
                profundidade = ((Number) profObj).doubleValue();
            } else if (profObj != null) {
                try {
                    profundidade = Double.valueOf(profObj.toString().replace(",", "."));
                } catch (NumberFormatException nfe) {
                    System.err.println("WARN: Parse PROF tabela:" + profObj);
                }
            }
            Integer nRecapagens = 0;
            Object recapObj = modeloTabela.getValueAt(linhaModelo, 12);
            if (recapObj instanceof Number) {
                nRecapagens = ((Number) recapObj).intValue();
            } else if (recapObj != null) {
                try {
                    nRecapagens = Integer.valueOf(recapObj.toString());
                } catch (NumberFormatException nfe) {
                    System.err.println("WARN: Parse RECAP tabela:" + recapObj);
                }
            }
            Integer projecaoKm = null;
            Object projObj = modeloTabela.getValueAt(linhaModelo, 13);
            if (projObj instanceof Number) {
                projecaoKm = ((Number) projObj).intValue();
            } else if (projObj != null) {
                try {
                    projecaoKm = Integer.valueOf(projObj.toString());
                } catch (NumberFormatException nfe) {
                    System.err.println("WARN: Parse PROJ KM tabela:" + projObj);
                }
            }
            String obs = "";
            Object obsObj = modeloTabela.getValueAt(linhaModelo, 14);
            if (obsObj != null) {
                obs = obsObj.toString();
            }

            grupoEmpresas.clearSelection();
            switch (idEmpresa) {

                case ID_EMPRESA_MARTINS_BORGES:
                    MARTINS_BORGES.setSelected(true);
                    break;
                case ID_EMPRESA_ALB:
                    ALB.setSelected(true);
                    break;
                case ID_EMPRESA_ENGEUDI:
                    ENGEUDI.setSelected(true);
                    break;
                default:
                    break;
            }

            atualizarOpcoesMedida();

            txtFogo.setText(fogo);
            selecionarItemComboBox(CbFornecedor, fornecedor);
            selecionarItemComboBox(CbFabricante, fabricante);
            selecionarItemComboBox(CbModelo, modelo);
            selecionarItemComboBox(CbMedida, medida);
            selecionarItemComboBox(CbTipoPneu, tipoPneu);

            if (TxtValor != null) {
                TxtValor.setText(valor != null ? currencyFormat.format(valor) : "");
            }
            TxtDot.setText(dot != null ? dot : "");
            TxtProfud.setText(profundidade != null ? String.format(Locale.GERMAN, "%.1f", profundidade) : "");
            TxtRecap.setText(nRecapagens != null ? String.valueOf(nRecapagens) : "0");
            TxtProjecao.setText(projecaoKm != null ? String.valueOf(projecaoKm) : "");
            TxtObs.setText(obs != null ? obs : "");

            modoEdicao = true;
            atualizarEstadoBotoes();

        } catch (ClassCastException cce) {
            JOptionPane.showMessageDialog(this, "Erro ao converter tipo de dado da tabela para edição.nVerifique se os dados na tabela estão corretos.n" + cce.getMessage(), "Erro de Tipo", JOptionPane.ERROR_MESSAGE);
            cancelarEdicao();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro inesperado ao carregar dados para edição:n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            cancelarEdicao();
        }
    }

    private void selecionarItemComboBox(JComboBox<String> comboBox, String itemParaSelecionar) {
        if (itemParaSelecionar == null) {
            comboBox.setSelectedIndex(-1);
            return;
        }
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBox.getModel();
        boolean encontrado = false;
        for (int i = 0; i < model.getSize(); i++) {
            if (itemParaSelecionar.equalsIgnoreCase(model.getElementAt(i))) {
                comboBox.setSelectedIndex(i);
                encontrado = true;
                break;
            }
        }

        if (!encontrado && comboBox.isEditable()) {
            comboBox.setSelectedItem(itemParaSelecionar);
        } else if (!encontrado) {
            comboBox.setSelectedIndex(-1);
        }

    }

    private void atualizarEstadoBotoes() {

        if (modoEdicao) {
            btnCadastrar.setText("SALVAR ALTERAÇÕES");

            btnExcluir.setEnabled(true);
        } else {
            btnCadastrar.setText("CADASTRAR");

            btnExcluir.setEnabled(Tabela_Exibicao.getSelectedRow() != -1);
        }

    }

    private void cancelarEdicao() {
        modoEdicao = false;
        idPneuEmEdicao = -1;
        limparCampos();
        Tabela_Exibicao.clearSelection();

        aplicarFiltroPneus();
    }

    private void prepararParaDuplicacao() {
        this.modoEdicao = false;
        this.idPneuEmEdicao = -1;
        
        txtFogo.setText(""); 
        atualizarEstadoBotoes(); 
        Tabela_Exibicao.clearSelection(); 
        txtFogo.requestFocusInWindow(); 
    }

    private void sugerirProximoFogo() {
        if (!Novo.isSelected()) {
            return;
        }

        int idEmpresa = 0;
        if (MARTINS_BORGES.isSelected()) {
            idEmpresa = ID_EMPRESA_MARTINS_BORGES;
        } else if (ALB.isSelected()) {
            idEmpresa = ID_EMPRESA_ALB;
        } else if (ENGEUDI.isSelected()) {
            idEmpresa = ID_EMPRESA_ENGEUDI;
        }

        if (idEmpresa > 0) {
            int ultimoFogo = pneuDAO.obterUltimoFogoPorEmpresa(idEmpresa);
            txtFogo.setText(String.valueOf(ultimoFogo + 1));
        } else {
            txtFogo.setText("");
        }
    }

    private void atualizarTabelaDados() {
        aplicarFiltroPneus();
    }

    
    
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btnCadastrar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        jCadastro1 = new javax.swing.JPanel();
        lbValor = new javax.swing.JLabel();
        lbTipo_pneu = new javax.swing.JLabel();
        CbTipoPneu = new javax.swing.JComboBox<>();
        lbProfundidade = new javax.swing.JLabel();
        TxtProfud = new javax.swing.JTextField();
        lbRecap = new javax.swing.JLabel();
        TxtRecap = new javax.swing.JTextField();
        btnNovoTipoPneu = new javax.swing.JButton();
        lbProjecao = new javax.swing.JLabel();
        TxtProjecao = new javax.swing.JTextField();
        TxtValor = new javax.swing.JTextField();
        btnEditarTipoPneu = new javax.swing.JButton();
        jCadastro2 = new javax.swing.JPanel();
        lb_Fornecedor = new javax.swing.JLabel();
        CbFornecedor = new javax.swing.JComboBox<>();
        btnNovoFornecedor = new javax.swing.JButton();
        lbFabricante = new javax.swing.JLabel();
        CbFabricante = new javax.swing.JComboBox<>();
        btnNovoFabricante = new javax.swing.JButton();
        lbModelo = new javax.swing.JLabel();
        CbModelo = new javax.swing.JComboBox<>();
        btnNovoModelo = new javax.swing.JButton();
        lbMedida = new javax.swing.JLabel();
        CbMedida = new javax.swing.JComboBox<>();
        btnNovoMedida = new javax.swing.JButton();
        lbObs = new javax.swing.JLabel();
        TxtObs = new javax.swing.JTextField();
        lbDot = new javax.swing.JLabel();
        TxtDot = new javax.swing.JTextField();
        btnEditarMedida = new javax.swing.JButton();
        btnEditarFornecedor = new javax.swing.JButton();
        btnEditarModelo = new javax.swing.JButton();
        btnEditarFabricante = new javax.swing.JButton();
        jScrollPane = new javax.swing.JScrollPane();
        Tabela_Exibicao = new javax.swing.JTable();
        Empresas = new javax.swing.JPanel();
        MARTINS_BORGES = new javax.swing.JCheckBox();
        ALB = new javax.swing.JCheckBox();
        ENGEUDI = new javax.swing.JCheckBox();
        txtFogo = new javax.swing.JTextField();
        lbFogo = new javax.swing.JLabel();
        Novo = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1250, 670));
        setName("TelaControleDePneus"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1000, 670));
        setSize(getPreferredSize());

        btnCadastrar.setText("CADASTRAR");
        btnCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarActionPerformed(evt);
            }
        });

        btnExcluir.setText("EXCLUIR");
        btnExcluir.setToolTipText("Excluir pneu selecionado");
        btnExcluir.setEnabled(false);
        btnExcluir.setMaximumSize(new java.awt.Dimension(96, 23));
        btnExcluir.setMinimumSize(new java.awt.Dimension(96, 23));
        btnExcluir.setName("btnExcluir"); // NOI18N
        btnExcluir.setPreferredSize(new java.awt.Dimension(96, 23));
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        lbValor.setText("Valor");

        lbTipo_pneu.setText("Tipo Pneu");

        lbProfundidade.setText("Profundidade (mm): ");

        lbRecap.setText("N° Recap.: ");

        TxtRecap.setName(""); // NOI18N

        btnNovoTipoPneu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/+.png"))); // NOI18N
        btnNovoTipoPneu.setToolTipText("Adicionar novo tipo de Pneu");
        btnNovoTipoPneu.setMaximumSize(new java.awt.Dimension(25, 25));
        btnNovoTipoPneu.setMinimumSize(new java.awt.Dimension(25, 25));
        btnNovoTipoPneu.setPreferredSize(new java.awt.Dimension(25, 25));
        btnNovoTipoPneu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoTipoPneuActionPerformed(evt);
            }
        });

        lbProjecao.setText("Projeção km: ");

        TxtValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        TxtValor.setMinimumSize(new java.awt.Dimension(72, 22));
        TxtValor.setPreferredSize(new java.awt.Dimension(72, 22));

        btnEditarTipoPneu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/pencil.png"))); // NOI18N
        btnEditarTipoPneu.setMaximumSize(new java.awt.Dimension(25, 25));
        btnEditarTipoPneu.setMinimumSize(new java.awt.Dimension(25, 25));
        btnEditarTipoPneu.setName("btnEditarTipoPneu"); // NOI18N
        btnEditarTipoPneu.setPreferredSize(new java.awt.Dimension(25, 25));
        btnEditarTipoPneu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarTipoPneuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jCadastro1Layout = new javax.swing.GroupLayout(jCadastro1);
        jCadastro1.setLayout(jCadastro1Layout);
        jCadastro1Layout.setHorizontalGroup(
            jCadastro1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jCadastro1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jCadastro1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbValor)
                    .addComponent(lbRecap)
                    .addComponent(lbProjecao)
                    .addGroup(jCadastro1Layout.createSequentialGroup()
                        .addGroup(jCadastro1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CbTipoPneu, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TxtProfud, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TxtRecap, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TxtProjecao, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addComponent(btnNovoTipoPneu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditarTipoPneu, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbProfundidade)
                    .addComponent(lbTipo_pneu, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TxtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jCadastro1Layout.setVerticalGroup(
            jCadastro1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jCadastro1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(lbValor)
                .addGap(8, 8, 8)
                .addComponent(TxtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(lbTipo_pneu)
                .addGap(4, 4, 4)
                .addGroup(jCadastro1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jCadastro1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(CbTipoPneu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnNovoTipoPneu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditarTipoPneu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addComponent(lbProfundidade)
                .addGap(4, 4, 4)
                .addComponent(TxtProfud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(lbRecap)
                .addGap(4, 4, 4)
                .addComponent(TxtRecap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbProjecao)
                .addGap(4, 4, 4)
                .addComponent(TxtProjecao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lb_Fornecedor.setText("Fornecedor: ");

        btnNovoFornecedor.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnNovoFornecedor.setForeground(new java.awt.Color(255, 0, 0));
        btnNovoFornecedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/+.png"))); // NOI18N
        btnNovoFornecedor.setToolTipText("Adicionar novo fornecedor");
        btnNovoFornecedor.setMaximumSize(new java.awt.Dimension(25, 25));
        btnNovoFornecedor.setMinimumSize(new java.awt.Dimension(25, 25));
        btnNovoFornecedor.setPreferredSize(new java.awt.Dimension(25, 25));
        btnNovoFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoFornecedorActionPerformed(evt);
            }
        });

        lbFabricante.setText("Fabricante:");

        btnNovoFabricante.setFont(new java.awt.Font("Agency FB", 1, 14)); // NOI18N
        btnNovoFabricante.setForeground(new java.awt.Color(255, 51, 51));
        btnNovoFabricante.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/+.png"))); // NOI18N
        btnNovoFabricante.setToolTipText("Adicionar novo Fabricante");
        btnNovoFabricante.setMaximumSize(new java.awt.Dimension(25, 25));
        btnNovoFabricante.setMinimumSize(new java.awt.Dimension(25, 25));
        btnNovoFabricante.setPreferredSize(new java.awt.Dimension(25, 25));
        btnNovoFabricante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoFabricanteActionPerformed(evt);
            }
        });

        lbModelo.setText("Modelo:");

        btnNovoModelo.setFont(new java.awt.Font("Agency FB", 1, 14)); // NOI18N
        btnNovoModelo.setForeground(new java.awt.Color(255, 51, 51));
        btnNovoModelo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/+.png"))); // NOI18N
        btnNovoModelo.setToolTipText("Adicionar novo modelo");
        btnNovoModelo.setMaximumSize(new java.awt.Dimension(25, 25));
        btnNovoModelo.setMinimumSize(new java.awt.Dimension(25, 25));
        btnNovoModelo.setName(" btnNovoModelo"); // NOI18N
        btnNovoModelo.setPreferredSize(new java.awt.Dimension(25, 25));
        btnNovoModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoModeloActionPerformed(evt);
            }
        });

        lbMedida.setText("Medida:");

        CbMedida.setEnabled(false);

        btnNovoMedida.setFont(new java.awt.Font("Agency FB", 1, 14)); // NOI18N
        btnNovoMedida.setForeground(new java.awt.Color(255, 51, 51));
        btnNovoMedida.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/+.png"))); // NOI18N
        btnNovoMedida.setToolTipText("Adicionar nova medida");
        btnNovoMedida.setMaximumSize(new java.awt.Dimension(25, 25));
        btnNovoMedida.setMinimumSize(new java.awt.Dimension(25, 25));
        btnNovoMedida.setPreferredSize(new java.awt.Dimension(25, 25));
        btnNovoMedida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoMedidaActionPerformed(evt);
            }
        });

        lbObs.setText("OBS.:");

        lbDot.setText("Dot: ");

        btnEditarMedida.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/pencil.png"))); // NOI18N
        btnEditarMedida.setMaximumSize(new java.awt.Dimension(25, 25));
        btnEditarMedida.setMinimumSize(new java.awt.Dimension(25, 25));
        btnEditarMedida.setName("btnEditarMedida"); // NOI18N
        btnEditarMedida.setPreferredSize(new java.awt.Dimension(25, 25));
        btnEditarMedida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarMedidaActionPerformed(evt);
            }
        });

        btnEditarFornecedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/pencil.png"))); // NOI18N
        btnEditarFornecedor.setToolTipText("Editar fornecedor selecionado");
        btnEditarFornecedor.setMaximumSize(new java.awt.Dimension(25, 25));
        btnEditarFornecedor.setMinimumSize(new java.awt.Dimension(25, 25));
        btnEditarFornecedor.setName("btnEditarFornecedor"); // NOI18N
        btnEditarFornecedor.setPreferredSize(new java.awt.Dimension(25, 25));
        btnEditarFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarFornecedorActionPerformed(evt);
            }
        });

        btnEditarModelo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/pencil.png"))); // NOI18N
        btnEditarModelo.setMaximumSize(new java.awt.Dimension(25, 25));
        btnEditarModelo.setMinimumSize(new java.awt.Dimension(25, 25));
        btnEditarModelo.setName("btnEditarModelo"); // NOI18N
        btnEditarModelo.setPreferredSize(new java.awt.Dimension(25, 25));
        btnEditarModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarModeloActionPerformed(evt);
            }
        });

        btnEditarFabricante.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/pencil.png"))); // NOI18N
        btnEditarFabricante.setMaximumSize(new java.awt.Dimension(25, 25));
        btnEditarFabricante.setMinimumSize(new java.awt.Dimension(25, 25));
        btnEditarFabricante.setName("btnEditarFabricante"); // NOI18N
        btnEditarFabricante.setPreferredSize(new java.awt.Dimension(25, 25));
        btnEditarFabricante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarFabricanteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jCadastro2Layout = new javax.swing.GroupLayout(jCadastro2);
        jCadastro2.setLayout(jCadastro2Layout);
        jCadastro2Layout.setHorizontalGroup(
            jCadastro2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jCadastro2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jCadastro2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jCadastro2Layout.createSequentialGroup()
                        .addGroup(jCadastro2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lb_Fornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CbFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbFabricante, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CbFabricante, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbModelo)
                            .addComponent(lbMedida, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbDot)
                            .addComponent(TxtDot, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jCadastro2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jCadastro2Layout.createSequentialGroup()
                                .addComponent(btnNovoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEditarFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jCadastro2Layout.createSequentialGroup()
                                .addComponent(btnNovoFabricante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEditarFabricante, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(TxtObs, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbObs, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jCadastro2Layout.createSequentialGroup()
                        .addComponent(CbMedida, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNovoMedida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditarMedida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jCadastro2Layout.createSequentialGroup()
                        .addComponent(CbModelo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNovoModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditarModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jCadastro2Layout.setVerticalGroup(
            jCadastro2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jCadastro2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(lb_Fornecedor)
                .addGap(4, 4, 4)
                .addGroup(jCadastro2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnNovoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditarFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(lbFabricante)
                .addGap(4, 4, 4)
                .addGroup(jCadastro2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jCadastro2Layout.createSequentialGroup()
                        .addGroup(jCadastro2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jCadastro2Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(CbFabricante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnNovoFabricante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)
                        .addComponent(lbModelo))
                    .addComponent(btnEditarFabricante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jCadastro2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CbModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNovoModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditarModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addComponent(lbMedida)
                .addGap(4, 4, 4)
                .addGroup(jCadastro2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CbMedida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNovoMedida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditarMedida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(lbDot)
                .addGap(0, 0, 0)
                .addComponent(TxtDot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(lbObs)
                .addGap(4, 4, 4)
                .addComponent(TxtObs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jCadastro2Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(CbFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jScrollPane.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jScrollPane.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPaneMouseClicked(evt);
            }
        });

        Tabela_Exibicao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "EMPR.", "FOGO", "FORNECEDOR", "VALOR", "FABRICANTE", "TIPO PNEU", "MODELO", "DOT", "MEDIDA", "PROFUND", "DATA", "RECAP.", "PROJ. KM", "OBS.:"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabela_Exibicao.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        Tabela_Exibicao.setShowVerticalLines(true);
        Tabela_Exibicao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tabela_ExibicaoMouseClicked(evt);
            }
        });
        jScrollPane.setViewportView(Tabela_Exibicao);

        Empresas.setPreferredSize(new java.awt.Dimension(145, 145));
        Empresas.setLayout(new java.awt.GridBagLayout());

        MARTINS_BORGES.setText("MARTINS E BORGES");
        MARTINS_BORGES.setMaximumSize(new java.awt.Dimension(150, 20));
        MARTINS_BORGES.setMinimumSize(new java.awt.Dimension(150, 20));
        MARTINS_BORGES.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 2);
        Empresas.add(MARTINS_BORGES, gridBagConstraints);

        ALB.setText("ALB");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        Empresas.add(ALB, gridBagConstraints);

        ENGEUDI.setText("ENGEUDI");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipady = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        Empresas.add(ENGEUDI, gridBagConstraints);

        txtFogo.setMaximumSize(null);
        txtFogo.setMinimumSize(new java.awt.Dimension(50, 22));
        txtFogo.setPreferredSize(new java.awt.Dimension(50, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        Empresas.add(txtFogo, gridBagConstraints);

        lbFogo.setText("Nº Fogo");
        lbFogo.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        Empresas.add(lbFogo, gridBagConstraints);

        Novo.setText("Novo Cadastro");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(Empresas, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(15, 15, 15)
                                        .addComponent(Novo)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jCadastro2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCadastro1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnCadastrar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(814, 814, 814)
                                        .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 142, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCadastro2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCadastro1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Empresas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(Novo)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCadastrar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32))
        );

        getAccessibleContext().setAccessibleName("TelaCadastroPneus");

        setSize(new java.awt.Dimension(1110, 662));
        setLocationRelativeTo(null);
    }// </editor-fold>                        

    private void MARTINSActionPerformed(java.awt.event.ActionEvent evt) {                                        

    }                                       

    private void btnCadastrarActionPerformed(java.awt.event.ActionEvent evt) {                                             


    int idEmpresa = 0;
    if (MARTINS_BORGES.isSelected()) { 
        idEmpresa = 1;
    } else if (ALB.isSelected()) {
        idEmpresa = 2;
    } else if (ENGEUDI.isSelected()) {
        idEmpresa = 3;
    }

    String fogoSequencial = txtFogo.getText().trim();
    String fornecedor = (CbFornecedor.getSelectedIndex() != -1) ? CbFornecedor.getSelectedItem().toString().toUpperCase() : "";
    String fabricante = (CbFabricante.getSelectedIndex() != -1) ? CbFabricante.getSelectedItem().toString().toUpperCase() : "";
    String tipoPneu = (CbTipoPneu.getSelectedIndex() != -1) ? CbTipoPneu.getSelectedItem().toString().toUpperCase() : "";
    String modelo = (CbModelo.getSelectedIndex() != -1) ? CbModelo.getSelectedItem().toString().toUpperCase() : "";
    String dot = TxtDot.getText().trim().toUpperCase();
    String medida = (CbMedida.getSelectedIndex() != -1) ? CbMedida.getSelectedItem().toString() : "";
    String profundidadeStr = TxtProfud.getText().trim().replace(",", ".");
    String nRecapStr = TxtRecap.getText().trim();
    String projecaoKmStr = TxtProjecao.getText().trim();
    String obs = TxtObs.getText().trim().toUpperCase();
    String valorTexto = TxtValor.getText().trim();

    
    if (idEmpresa == 0) { JOptionPane.showMessageDialog(this, "Selecione a Empresa Proprietária.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE); MARTINS_BORGES.requestFocusInWindow(); return; }
    if (fogoSequencial.isEmpty()) { JOptionPane.showMessageDialog(this, "O campo 'N° Fogo' é obrigatório.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE); txtFogo.requestFocusInWindow(); return; }
    if (!fogoSequencial.matches("d+")) { JOptionPane.showMessageDialog(this, "O campo 'N° Fogo' deve conter apenas números.", "Formato Inválido", JOptionPane.WARNING_MESSAGE); txtFogo.requestFocusInWindow(); return; }
    if (fogoSequencial.length() > 1 && fogoSequencial.startsWith("0")) { JOptionPane.showMessageDialog(this, "O Nº de Fogo não pode começar com zero (se > 1 dígito).", "Formato Inválido", JOptionPane.WARNING_MESSAGE); txtFogo.requestFocusInWindow(); return; }
    if (fornecedor.isEmpty()) { JOptionPane.showMessageDialog(this, "Selecione o Fornecedor.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE); CbFornecedor.requestFocusInWindow(); return; }
    if (fabricante.isEmpty()) { JOptionPane.showMessageDialog(this, "Selecione o Fabricante.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE); CbFabricante.requestFocusInWindow(); return; }
    if (tipoPneu.isEmpty()) { JOptionPane.showMessageDialog(this, "Selecione o Tipo de Pneu.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE); CbTipoPneu.requestFocusInWindow(); return; }
    if (modelo.isEmpty()) { JOptionPane.showMessageDialog(this, "Selecione o Modelo.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE); CbModelo.requestFocusInWindow(); return; }
    if (dot.isEmpty()) { JOptionPane.showMessageDialog(this, "O campo 'Dot' é obrigatório.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE); TxtDot.requestFocusInWindow(); return; }
    if (medida.isEmpty()) { JOptionPane.showMessageDialog(this, "Selecione a Medida.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE); CbMedida.requestFocusInWindow(); return; }
    if (idEmpresa > 0 && !medida.isEmpty()) {
        List<String> prefixosValidos = obterPrefixosPermitidos(idEmpresa); 
        boolean medidaOk = false;
        for (String prefixo : prefixosValidos) { if (medida.trim().toUpperCase().startsWith(prefixo.toUpperCase())) { medidaOk = true; break; } }
        if (!medidaOk) { JOptionPane.showMessageDialog(this, "A medida '" + medida + "' não parece ser válida para a empresa selecionada.", "Validação Falhou", JOptionPane.WARNING_MESSAGE); CbMedida.requestFocusInWindow(); return; }
    }

    Double valor = null; Double profundidade = null; Integer nRecapagens = null; Integer projecaoKm = null;
    try {
        if (valorTexto != null && !valorTexto.trim().isEmpty()) {
            try { Number valorNum = currencyFormat.parse(valorTexto); valor = valorNum.doubleValue(); }
            catch (java.text.ParseException pe) { String valorNumerico = valorTexto.replace("R$", "").replace(".", "").replace(",", ".").trim(); if (!valorNumerico.isEmpty()) { valor = Double.parseDouble(valorNumerico); } else { valor = 0.0; }}
            if (valor != null && valor < 0) { throw new NumberFormatException("Valor não pode ser negativo."); }
        } else { valor = 0.0; }
        if (profundidadeStr.isEmpty()) { throw new NumberFormatException("Profundidade é obrigatória."); }
        profundidade = Double.parseDouble(profundidadeStr); if (profundidade < 0) { throw new NumberFormatException("Profundidade negativa."); }
        if (nRecapStr.isEmpty()) { throw new NumberFormatException("N° Recapagens é obrigatório."); }
        nRecapagens = Integer.parseInt(nRecapStr); if (nRecapagens < 0) { throw new NumberFormatException("N° Recap. negativo."); }
        if (!projecaoKmStr.isEmpty()) { projecaoKm = Integer.parseInt(projecaoKmStr); if (projecaoKm < 0) { throw new NumberFormatException("Projeção Km negativa."); } }
    } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Erro em Campo Numérico:n" + e.getMessage(), "Inválido", JOptionPane.ERROR_MESSAGE); return; } 

    Pneu pneu = new Pneu();
    pneu.setIdEmpresaProprietaria(idEmpresa);
    pneu.setFogo(fogoSequencial);
    pneu.setFornecedor(fornecedor);
    pneu.setValor(valor);
    pneu.setFabricante(fabricante);
    pneu.setTipoPneu(tipoPneu);
    pneu.setModelo(modelo);
    pneu.setDot(dot);
    pneu.setMedida(medida);
    pneu.setProfundidade(profundidade);
    pneu.setDataCadastro(LocalDate.now());
    pneu.setnRecapagens(nRecapagens != null ? nRecapagens : 0);
    pneu.setProjetadoKm(projecaoKm);
    pneu.setObservacoes(obs);
    
    boolean sucesso;
    String operacao = ""; 
    if (modoEdicao) {
        operacao = "atualizar";
        if (idPneuEmEdicao <= 0) { JOptionPane.showMessageDialog(this, "ID inválido para atualização.", "Erro", JOptionPane.ERROR_MESSAGE); return; }
        pneu.setId(idPneuEmEdicao);

        sucesso = pneuDAO.atualizarPneu(pneu);
    } else {
        operacao = "cadastrar";
        sucesso = pneuDAO.inserirPneu(pneu);
    }

    if (sucesso) {
        JOptionPane.showMessageDialog(this, "Pneu " + (modoEdicao ? "atualizado" : "cadastrado") + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        if (modoEdicao) {
            cancelarEdicao(); 
        } else {
            limparCampos(); 
        }
        atualizarTabelaDados(); 
    } else {
        JOptionPane.showMessageDialog(this, "Falha ao " + operacao + " pneu.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
        
    }



    }                                            

    private void btnNovoFornecedorActionPerformed(java.awt.event.ActionEvent evt) {                                                  

       String nomeNovoFornecedor = Utilitarios.mostrarDialogoComCampoMaiusculo(
            this,
            "Digite o nome do novo Fornecedor:",
            "Novo Fornecedor"
    );

    if (nomeNovoFornecedor != null && !nomeNovoFornecedor.isEmpty()) {
        br.com.martins_borges.model.Fornecedor fornecedorCriado = (br.com.martins_borges.model.Fornecedor) fornecedorDAO.inserirFornecedor(nomeNovoFornecedor);

        if (fornecedorCriado != null) {
            popularComboBoxFornecedor();
            CbFornecedor.setSelectedItem(fornecedorCriado.getNomeFornecedor());
            JOptionPane.showMessageDialog(this, "Fornecedor '" + fornecedorCriado.getNomeFornecedor() + "' cadastrado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    }                                                 

    private void btnNovoFabricanteActionPerformed(java.awt.event.ActionEvent evt) {                                                  
         
                                                   
   
    JTextField textField = new JTextField(20); 
    
  
    textField.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            String textoAtual = textField.getText();
            if (textoAtual != null && !textoAtual.isEmpty()) {
                if (!textoAtual.equals(textoAtual.toUpperCase())) {
                    textField.setText(textoAtual.toUpperCase());
                }
            }
        }
    });

 
    if (textField.getDocument() instanceof AbstractDocument) {
        AbstractDocument doc = (AbstractDocument) textField.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null) {
                    return;
                }
                super.insertString(fb, offset, string.toUpperCase(), attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null) {
                    return;
                }
                super.replace(fb, offset, length, text.toUpperCase(), attrs);
            }
        });
    }
    
    
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    
  
    panel.add(new JLabel("Digite o nome do novo Fabricante:"));
    
    panel.add(Box.createVerticalStrut(5));
    
    panel.add(textField);
    
   
    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Novo Fabricante",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );
    
    if (result == JOptionPane.OK_OPTION) {
        String nomeNovoFabricante = textField.getText().trim();
        
        if (!nomeNovoFabricante.isEmpty()) {
            br.com.martins_borges.model.Fabricante fabricanteCriado = fabricanteDAO.inserirFabricante(nomeNovoFabricante);

            if (fabricanteCriado != null) {
                popularComboBoxFabricante();
                CbFabricante.setSelectedItem(fabricanteCriado.getNomeFabricante());
                JOptionPane.showMessageDialog(this, "Fabricante '" + fabricanteCriado.getNomeFabricante() + "' cadastrado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    }                                                 

    private void btnNovoModeloActionPerformed(java.awt.event.ActionEvent evt) {                                              
        String nomeNovoModelo = Utilitarios.mostrarDialogoComCampoMaiusculo(
        this,
        "Digite o nome do novo Modelo:",
        "Novo Modelo"
    );

    if (nomeNovoModelo != null && !nomeNovoModelo.isEmpty()) {
        ModeloPneu modeloCriado = modeloPneuDAO.inserirModeloPneu(nomeNovoModelo);
        if (modeloCriado != null) {
            popularComboBoxModelo();
            CbModelo.setSelectedItem(modeloCriado.getNomeModelo());
            JOptionPane.showMessageDialog(this, "Modelo '" + modeloCriado.getNomeModelo() + "' cadastrado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    }                                             

    private void btnNovoMedidaActionPerformed(java.awt.event.ActionEvent evt) {                                              
        String novaDescricaoMedida = Utilitarios.mostrarDialogoComCampoMaiusculo(
        this,
        "Digite a nova Medida (Ex: 295/80R22.5):",
        "Nova Medida"
    );

    if (novaDescricaoMedida != null && !novaDescricaoMedida.isEmpty()) {
        MedidaPneu medidaCriada = medidaPneuDAO.inserirMedidaPneu(novaDescricaoMedida);
        if (medidaCriada != null) {
           popularComboBoxMedida(new ArrayList<>());
            CbMedida.setSelectedItem(medidaCriada.getDescricaoMedida());
            JOptionPane.showMessageDialog(this, "Medida '" + medidaCriada.getDescricaoMedida() + "' cadastrada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    }                                             

    private void btnNovoTipoPneuActionPerformed(java.awt.event.ActionEvent evt) {                                                
if (tipoPneuDAO == null) {
        System.err.println("ERRO: tipoPneuDAO não inicializado em btnNovoTipoPneuActionPerformed!");
        return;
    }
    if (CbTipoPneu == null) { 
        System.err.println("ERRO: CbTipoPneu não inicializado!");
        return;
    }

    String nomeNovoTipo = Utilitarios.mostrarDialogoComCampoMaiusculo(
        this,
        "Digite o novo Tipo de Pneu (Ex: Radial, Diagonal, Recapado 1ª):",
        "Novo Tipo de Pneu"
    );

    if (nomeNovoTipo != null && !nomeNovoTipo.isEmpty()) {
        br.com.martins_borges.model.TipoPneu tipoCriado = tipoPneuDAO.inserirTipoPneu(nomeNovoTipo);

        if (tipoCriado != null) {
            popularComboBoxTipoPneu();
            CbTipoPneu.setSelectedItem(tipoCriado.getNomeTipo());
            JOptionPane.showMessageDialog(this, "Tipo de Pneu '" + tipoCriado.getNomeTipo() + "' cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    }                                               

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {                                           
        int linhaSelecionada = Tabela_Exibicao.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pneu na tabela para excluir.", "Nenhum Pneu Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel modeloTabela = (DefaultTableModel) Tabela_Exibicao.getModel();
        int idParaExcluir = -1;
        String fogoParaConfirmar = "";
        try {

            Object idObj = modeloTabela.getValueAt(Tabela_Exibicao.convertRowIndexToModel(linhaSelecionada), 0);
            Object fogoObj = modeloTabela.getValueAt(Tabela_Exibicao.convertRowIndexToModel(linhaSelecionada), 2);

            if (idObj instanceof Integer) {
                idParaExcluir = (Integer) idObj;
            } else {

                idParaExcluir = Integer.parseInt(idObj.toString());
            }
            fogoParaConfirmar = (fogoObj != null) ? fogoObj.toString() : "";

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro ao obter o ID do pneu selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (idParaExcluir <= 0) {
            JOptionPane.showMessageDialog(this, "Não foi possível obter um ID válido para exclusão.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o Pneu (ID: " + idParaExcluir + ", Fogo: " + fogoParaConfirmar + ")?nEsta ação não pode ser desfeita.",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resposta == JOptionPane.YES_OPTION) {

            boolean sucesso = pneuDAO.excluirPneu(idParaExcluir);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Pneu excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCampos();
                aplicarFiltroPneus(); 
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao excluir o pneu.", "Erro DAO", JOptionPane.ERROR_MESSAGE);

            }
        }

    }                                          

    private void jScrollPaneMouseClicked(java.awt.event.MouseEvent evt) {                                         

    }                                        

    private void Tabela_ExibicaoMouseClicked(java.awt.event.MouseEvent evt) {                                             
        int linhaSelecionada = Tabela_Exibicao.getSelectedRow();
        if (linhaSelecionada != -1) {
            btnExcluir.setEnabled(true);

            if (evt.getClickCount() == 2) {
                carregarDadosParaEdicao(linhaSelecionada);

                Object[] options = {"EDITAR Pneu Existente", "DUPLICAR para Novo Cadastro"};
                int escolha = JOptionPane.showOptionDialog(this,
                        "Você deseja editar o pneu selecionado ou duplicá-lo para um novo cadastro?",
                        "Escolha uma Ação",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, 
                        options, 
                        options[0]); 

                switch (escolha) {
                    case JOptionPane.YES_OPTION: 
                        modoEdicao = true;
                        atualizarEstadoBotoes();
                        break;
                    case JOptionPane.NO_OPTION: 
                        prepararParaDuplicacao();
                        break;
                    default: 
                        cancelarEdicao();
                        break;
                }
            }
        } else {

            cancelarEdicao();
        }
    }                                            

    private void btnEditarFornecedorActionPerformed(java.awt.event.ActionEvent evt) {                                                    
                                                          

    Object itemSelecionadoObj = CbFornecedor.getSelectedItem();
    if (itemSelecionadoObj == null || itemSelecionadoObj.toString().isEmpty() || CbFornecedor.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, 
            "Selecione um fornecedor válido na lista para editar.", 
            "Nenhum Fornecedor Selecionado", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    String nomeAntigo = itemSelecionadoObj.toString();
    String nomeNovo = (String) JOptionPane.showInputDialog(this, 
        "Digite o NOVO nome para o fornecedor (atual: '" + nomeAntigo + "'):", 
        "Editar Fornecedor", 
        JOptionPane.PLAIN_MESSAGE, 
        null, 
        null, 
        nomeAntigo);
    
    if (nomeNovo != null && !nomeNovo.isEmpty()) {
        if (nomeNovo.equalsIgnoreCase(nomeAntigo)) {
            JOptionPane.showMessageDialog(this, 
                "O nome informado é igual ao nome atual. Nenhuma alteração foi realizada.", 
                "Aviso", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        nomeNovo = nomeNovo.toUpperCase();
        
        Fornecedor fornecedorParaEditar = (Fornecedor) fornecedorDAO.buscarPorNomeExato(nomeAntigo);

        if (fornecedorParaEditar == null) {
            JOptionPane.showMessageDialog(this, 
                "Não foi possível encontrar o fornecedor original ('"+nomeAntigo+"') no banco para atualizar.", 
                "Erro Interno", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            boolean sucesso = fornecedorDAO.atualizarNomeFornecedor(fornecedorParaEditar.getIdFornecedor(), nomeNovo);
            if (sucesso) {
                popularComboBoxFornecedor();
                CbFornecedor.setSelectedItem(nomeNovo);
                
                if (!nomeNovo.equals(CbFornecedor.getSelectedItem())) { 
                    CbFornecedor.setSelectedIndex(0); 
                    btnEditarFornecedor.setEnabled(false); 
                }

                JOptionPane.showMessageDialog(this, 
                    "Fornecedor '" + nomeAntigo + "' foi atualizado para '" + nomeNovo + "' com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Não foi possível atualizar o fornecedor. Por favor, tente novamente.", 
                    "Erro ao Atualizar", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Ocorreu um erro ao tentar atualizar o fornecedor: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

  


    }                                                   

    private void btnEditarFabricanteActionPerformed(java.awt.event.ActionEvent evt) {                                                    
                                                       
    Object itemSelecionadoObj = CbFabricante.getSelectedItem();
    if (itemSelecionadoObj == null || itemSelecionadoObj.toString().isEmpty() || CbFabricante.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, 
            "Selecione um fabricante válido na lista para editar.", 
            "Nenhum Fabricante Selecionado", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    String nomeAntigo = itemSelecionadoObj.toString();
    String nomeNovo = (String) JOptionPane.showInputDialog(this, 
        "Digite o NOVO nome para o fabricante (atual: '" + nomeAntigo + "'):", 
        "Editar Fabricante", 
        JOptionPane.PLAIN_MESSAGE, 
        null, 
        null, 
        nomeAntigo);
    
    if (nomeNovo != null && !nomeNovo.isEmpty()) {
        if (nomeNovo.equalsIgnoreCase(nomeAntigo)) {
            JOptionPane.showMessageDialog(this, 
                "O nome informado é igual ao nome atual. Nenhuma alteração foi realizada.", 
                "Aviso", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        nomeNovo = nomeNovo.toUpperCase();
        
        Fabricante fabricanteParaEditar = (Fabricante) fabricanteDAO.buscarPorNomeExato(nomeAntigo);

        if (fabricanteParaEditar == null) {
            JOptionPane.showMessageDialog(this, 
                "Não foi possível encontrar o fabricante original ('"+nomeAntigo+"') no banco para atualizar.", 
                "Erro Interno", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            boolean sucesso = fabricanteDAO.atualizarNomeFabricante(fabricanteParaEditar.getIdFabricante(), nomeNovo);
            if (sucesso) {
                popularComboBoxFabricante();
                CbFabricante.setSelectedItem(nomeNovo);
                
                if (!nomeNovo.equals(CbFabricante.getSelectedItem())) { 
                    CbFabricante.setSelectedIndex(0); 
                    btnEditarFabricante.setEnabled(false); 
                }

                JOptionPane.showMessageDialog(this, 
                    "Fabricante '" + nomeAntigo + "' foi atualizado para '" + nomeNovo + "' com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Não foi possível atualizar o fabricante. Por favor, tente novamente.", 
                    "Erro ao Atualizar", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Ocorreu um erro ao tentar atualizar o fabricante: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
                                                                                                    

    
 
     
    }                                                   

    private void btnEditarModeloActionPerformed(java.awt.event.ActionEvent evt) {                                                
                                                     
    Object itemSelecionadoObj = CbModelo.getSelectedItem();
    if (itemSelecionadoObj == null || itemSelecionadoObj.toString().isEmpty() || CbModelo.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, "Selecione um modelo válido para editar.", "Nenhum Modelo", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    String nomeAntigo = itemSelecionadoObj.toString();

    String nomeNovo = (String) JOptionPane.showInputDialog(this, 
        "Digite o NOVO nome para o modelo (atual: '" + nomeAntigo + "'):", 
        "Editar Modelo", 
        JOptionPane.PLAIN_MESSAGE, 
        null, 
        null, 
        nomeAntigo);

    if (nomeNovo != null && !nomeNovo.trim().isEmpty() && !nomeNovo.trim().equalsIgnoreCase(nomeAntigo)) {
        nomeNovo = nomeNovo.trim();

        ModeloPneu modParaEditar = modeloPneuDAO.buscarPorNomeExato(nomeAntigo); 

        if (modParaEditar == null) {
            JOptionPane.showMessageDialog(this, "Não foi possível encontrar o modelo original ('"+nomeAntigo+"') para atualizar.", "Erro Interno", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean sucesso = modeloPneuDAO.atualizarNomeModelo(modParaEditar.getIdModelo(), nomeNovo); 

        if (sucesso) {
            popularComboBoxModelo(); 
            CbModelo.setSelectedItem(nomeNovo);
            if (!nomeNovo.equals(CbModelo.getSelectedItem())) { 
                CbModelo.setSelectedIndex(0); 
                btnEditarModelo.setEnabled(false); 
            }
            JOptionPane.showMessageDialog(this, "Modelo atualizado para '" + nomeNovo + "'!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    } else if (nomeNovo != null && !nomeNovo.trim().isEmpty() && nomeNovo.trim().equalsIgnoreCase(nomeAntigo)) {
        JOptionPane.showMessageDialog(this, "O nome informado é igual ao nome atual. Nenhuma alteração foi realizada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }



    

    }                                               

    private void btnEditarMedidaActionPerformed(java.awt.event.ActionEvent evt) {                                                
    
                                                      
    Object itemSelecionadoObj = CbMedida.getSelectedItem();
    if (itemSelecionadoObj == null || itemSelecionadoObj.toString().isEmpty() || CbMedida.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, 
            "Selecione uma medida válida para editar.", 
            "Nenhuma Medida Selecionada", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    String nomeAntigo = itemSelecionadoObj.toString();
    String nomeNovo = (String) JOptionPane.showInputDialog(this, 
        "Digite o NOVO nome para a medida (atual: '" + nomeAntigo + "'):", 
        "Editar Medida", 
        JOptionPane.PLAIN_MESSAGE, 
        null, 
        null, 
        nomeAntigo);

    if (nomeNovo != null && !nomeNovo.trim().isEmpty() && !nomeNovo.trim().equalsIgnoreCase(nomeAntigo)) {
        nomeNovo = nomeNovo.trim().toUpperCase();
        
        MedidaPneu medidaParaEditar = medidaPneuDAO.buscarPorDescricaoExata(nomeAntigo);

        if (medidaParaEditar == null) {
            JOptionPane.showMessageDialog(this, 
                "Não foi possível encontrar a medida original ('"+nomeAntigo+"') no banco para atualizar.", 
                "Erro Interno", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean sucesso = medidaPneuDAO.atualizarDescricaoMedida(medidaParaEditar.getIdMedida(), nomeNovo);
            if (sucesso) {
               popularComboBoxMedida(new ArrayList<>());
                CbMedida.setSelectedItem(nomeNovo);
                
                if(!nomeNovo.equals(CbMedida.getSelectedItem())){ 
                    CbMedida.setSelectedIndex(0); 
                    btnEditarMedida.setEnabled(false); 
                }

                JOptionPane.showMessageDialog(this, 
                    "Medida '" + nomeAntigo + "' foi atualizada para '" + nomeNovo + "' com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Não foi possível atualizar a medida. Por favor, tente novamente.", 
                    "Erro ao Atualizar", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Ocorreu um erro ao tentar atualizar a medida: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    } else if (nomeNovo != null && !nomeNovo.trim().isEmpty() && nomeNovo.trim().equalsIgnoreCase(nomeAntigo)) {
        JOptionPane.showMessageDialog(this, 
            "O nome informado é igual ao nome atual. Nenhuma alteração foi realizada.", 
            "Aviso", 
            JOptionPane.INFORMATION_MESSAGE);
    }

   

                                                       
    





    }                                               

    private void btnEditarTipoPneuActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        
                                                        
        
    Object itemSelecionadoObj = CbTipoPneu.getSelectedItem();
    if (itemSelecionadoObj == null || itemSelecionadoObj.toString().isEmpty() || CbTipoPneu.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, 
            "Selecione um tipo de pneu válido para editar.", 
            "Nenhum Tipo Selecionado", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    String nomeAntigo = itemSelecionadoObj.toString();

    String nomeNovo = (String) JOptionPane.showInputDialog(this, 
        "Digite o NOVO nome para o Tipo de Pneu:", 
        "Editar Tipo de Pneu", 
        JOptionPane.PLAIN_MESSAGE, 
        null, 
        null, 
        nomeAntigo);

    if (nomeNovo != null && !nomeNovo.trim().isEmpty()) {
        if (nomeNovo.trim().equalsIgnoreCase(nomeAntigo)) {
            JOptionPane.showMessageDialog(this, 
                "O nome informado é igual ao nome atual. Nenhuma alteração foi realizada.", 
                "Aviso", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        nomeNovo = nomeNovo.trim().toUpperCase();
        
        TipoPneu tipoParaEditar = tipoPneuDAO.buscarPorNomeExato(nomeAntigo);

        if (tipoParaEditar == null) {
            JOptionPane.showMessageDialog(this, 
                "Não foi possível encontrar o tipo original ('"+nomeAntigo+"') no banco para atualizar.", 
                "Erro Interno", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean sucesso = tipoPneuDAO.atualizarNomeTipo(tipoParaEditar.getIdTipo(), nomeNovo);
            if (sucesso) {
                popularComboBoxTipoPneu(); 
                CbTipoPneu.setSelectedItem(nomeNovo);
                
                if (!nomeNovo.equals(CbTipoPneu.getSelectedItem())) { 
                    CbTipoPneu.setSelectedIndex(0); 
                    btnEditarTipoPneu.setEnabled(false); 
                }

                JOptionPane.showMessageDialog(this, 
                    "Tipo de Pneu '" + nomeAntigo + "' foi atualizado para '" + nomeNovo + "' com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Não foi possível atualizar o tipo de pneu. Por favor, tente novamente.", 
                    "Erro ao Atualizar", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Ocorreu um erro ao tentar atualizar o tipo de pneu: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
                                               
    

   

    }                                                 

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaCadastroPneus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            TelaCadastroPneus dialog = new TelaCadastroPneus(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify                     
    private javax.swing.JCheckBox ALB;
    private javax.swing.JComboBox<String> CbFabricante;
    private javax.swing.JComboBox<String> CbFornecedor;
    private javax.swing.JComboBox<String> CbMedida;
    private javax.swing.JComboBox<String> CbModelo;
    private javax.swing.JComboBox<String> CbTipoPneu;
    private javax.swing.JCheckBox ENGEUDI;
    private javax.swing.JPanel Empresas;
    private javax.swing.JCheckBox MARTINS_BORGES;
    private javax.swing.JCheckBox Novo;
    private javax.swing.JTable Tabela_Exibicao;
    private javax.swing.JTextField TxtDot;
    private javax.swing.JTextField TxtObs;
    private javax.swing.JTextField TxtProfud;
    private javax.swing.JTextField TxtProjecao;
    private javax.swing.JTextField TxtRecap;
    private javax.swing.JTextField TxtValor;
    private javax.swing.JButton btnCadastrar;
    private javax.swing.JButton btnEditarFabricante;
    private javax.swing.JButton btnEditarFornecedor;
    private javax.swing.JButton btnEditarMedida;
    private javax.swing.JButton btnEditarModelo;
    private javax.swing.JButton btnEditarTipoPneu;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnNovoFabricante;
    private javax.swing.JButton btnNovoFornecedor;
    private javax.swing.JButton btnNovoMedida;
    private javax.swing.JButton btnNovoModelo;
    private javax.swing.JButton btnNovoTipoPneu;
    private javax.swing.JPanel jCadastro1;
    private javax.swing.JPanel jCadastro2;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JLabel lbDot;
    private javax.swing.JLabel lbFabricante;
    private javax.swing.JLabel lbFogo;
    private javax.swing.JLabel lbMedida;
    private javax.swing.JLabel lbModelo;
    private javax.swing.JLabel lbObs;
    private javax.swing.JLabel lbProfundidade;
    private javax.swing.JLabel lbProjecao;
    private javax.swing.JLabel lbRecap;
    private javax.swing.JLabel lbTipo_pneu;
    private javax.swing.JLabel lbValor;
    private javax.swing.JLabel lb_Fornecedor;
    private javax.swing.JTextField txtFogo;
    // End of variables declaration                   
// </editor-fold>  
}