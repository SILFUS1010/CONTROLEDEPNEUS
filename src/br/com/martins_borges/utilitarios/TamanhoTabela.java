package br.com.martins_borges.utilitarios;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;

public class TamanhoTabela {

    private static Color[] customCellBackgroundColors; // Campo para cores personalizadas das células
//135,206,250
    //222,184,135
        public static final Color[] HEADER_COLORS = {
        new Color(135, 206, 250), // Rosa
        new Color(222, 184, 135), // Azul claro
        new Color(240, 230, 140), // Cáqui
        new Color(152, 251, 152), // Verde-claro
        new Color(255, 218, 185), // Pêssego
        new Color(230, 230, 250), // Lavanda
        new Color(245, 222, 179), // Trigo
        new Color(176, 224, 230), // Azul acinzentado
        new Color(218, 112, 214), // Orquídea
        new Color(255, 160, 122)  // Salmão claro
    };

    public static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void configurar(JTable tabela) { configurar(tabela, null, null); }

    public static void configurar(JTable tabela, int[] larguraColunas) { configurar(tabela, larguraColunas, null); }

    public static void configurar(JTable tabela, int[] larguraColunas, Color[] cellBackgroundColors) {
        customCellBackgroundColors = cellBackgroundColors; // Define as cores personalizadas
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Desativar redimensionamento automático

        // Definir o TableCellRenderer personalizado para os cabeçalhos
        tabela.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        tabela.setDefaultRenderer(Object.class, new TableRenderer());
        tabela.setDefaultRenderer(String.class, new TableRenderer());
        tabela.setDefaultRenderer(Number.class, new TableRenderer());
        tabela.setDefaultRenderer(LocalDate.class, new DateRenderer()); // Adicionado para LocalDate

        if (larguraColunas != null) {
            definirLarguraColunas(tabela, larguraColunas);
        } else {
            ajustarLarguraColunas(tabela);
        }
    }

    private static void definirLarguraColunas(JTable tabela, int[] larguraColunas) {
    for (int i = 0; i < Math.min(tabela.getColumnCount(), larguraColunas.length); i++) {
        int largura = larguraColunas[i];
        tabela.getColumnModel().getColumn(i).setPreferredWidth(largura);
        
        // Se a largura for 0, força a coluna a ficar oculta
        if (largura == 0) {
            tabela.getColumnModel().getColumn(i).setMinWidth(0);
            tabela.getColumnModel().getColumn(i).setMaxWidth(0);
        }
    }
}

    private static void ajustarLarguraColunas(JTable tabela) {
        for (int coluna = 0; coluna < tabela.getColumnCount(); coluna++) {
            int largura = 50; // Largura mínima
            for (int linha = 0; linha < tabela.getRowCount(); linha++) {
                TableCellRenderer renderer = tabela.getCellRenderer(linha, coluna);
                Component comp = tabela.prepareRenderer(renderer, linha, coluna);
                largura = Math.max(comp.getPreferredSize().width + 10, largura);
            }
            tabela.getColumnModel().getColumn(coluna).setPreferredWidth(largura);
        }
    }

    public static class TableRenderer extends DefaultTableCellRenderer {
        public TableRenderer() {
            super();
            setHorizontalAlignment(SwingConstants.CENTER); // Centraliza o conteúdo da célula
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado, boolean temFoco, int linha, int coluna) {
            Component componente = super.getTableCellRendererComponent(tabela, valor, selecionado, temFoco, linha, coluna);

            if (selecionado) {
                componente.setBackground(new Color(0, 0, 255)); // Azul para seleção
                componente.setForeground(Color.WHITE);
            } else {
                if (customCellBackgroundColors != null && coluna < customCellBackgroundColors.length && customCellBackgroundColors[coluna] != null) {
                    componente.setBackground(customCellBackgroundColors[coluna]);
                } else {
                    componente.setBackground(HEADER_COLORS[coluna % HEADER_COLORS.length]);
                }
                componente.setForeground(Color.BLACK); // Mantém a fonte preta
            }

            return componente;
        }
    }

    public static class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value.toString().toUpperCase(), isSelected, hasFocus, row, column);
            label.setBackground(table.getTableHeader().getBackground()); // Usa a cor de fundo padrão do cabeçalho
            label.setForeground(Color.BLACK);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD)); // Negrito
            label.setBorder(BorderFactory.createRaisedBevelBorder()); // Alto relevo
            return label;
        }
    }

    public static class CurrencyRenderer extends DefaultTableCellRenderer {
        public CurrencyRenderer() {
            super();
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        public void setValue(Object value) {
            if (value instanceof Number) {
                setText(currencyFormatter.format(value));
            } else {
                setText((value == null) ? "" : value.toString());
            }
        }
    }

    public static class DateRenderer extends DefaultTableCellRenderer {
        public DateRenderer() {
            super();
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public void setValue(Object value) {
            if (value instanceof LocalDate) {
                setText(((LocalDate) value).format(dateFormatter));
            } else {
                setText((value == null) ? "" : value.toString());
            }
        }
    }

    public static class CenterRenderer extends DefaultTableCellRenderer {
        public CenterRenderer() {
            super();
            setHorizontalAlignment(SwingConstants.CENTER);
        }
    }
}