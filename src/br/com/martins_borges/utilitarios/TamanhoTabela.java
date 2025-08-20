package br.com.martins_borges.utilitarios;


import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class TamanhoTabela {

    private static final Color[] HEADER_COLORS = {
        new Color(255, 192, 203), // Rosa
        new Color(135, 206, 250), // Azul claro
        new Color(240, 230, 140), // Cáqui
        new Color(152, 251, 152), // Verde-claro
        new Color(255, 218, 185), // Pêssego
        new Color(230, 230, 250), // Lavanda
        new Color(245, 222, 179), // Trigo
        new Color(176, 224, 230), // Azul acinzentado
        new Color(218, 112, 214), // Orquídea
        new Color(255, 160, 122)  // Salmão claro
    };

    public static void configurar(JTable tabela) {
        configurar(tabela, null);
    }

    public static void configurar(JTable tabela, int[] larguraColunas) {
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Desativar redimensionamento automático

        // Definir o TableCellRenderer personalizado para os cabeçalhos
        tabela.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        tabela.setDefaultRenderer(Object.class, new TableRenderer());

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

    private static class TableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado, boolean temFoco, int linha, int coluna) {
            Component componente = super.getTableCellRendererComponent(tabela, valor, selecionado, temFoco, linha, coluna);

            if (selecionado) {
                componente.setBackground(new Color(0, 0, 255)); // Azul
                componente.setForeground(Color.WHITE); // Fonte branca
            } else {
                if (coluna < 7) {
                    componente.setBackground(new Color(255, 255, 0)); // Amarelo
                } else if (coluna < 11) {
                    componente.setBackground(new Color(255, 182, 193)); // Rosa claro
                } else if (coluna < 15) {
                    componente.setBackground(new Color(32, 178, 170)); // Turquesa
                } else {
                    componente.setBackground(new Color(173, 255, 47)); // Verde limão
                }
                componente.setForeground(Color.BLACK); // Fonte preta
            }

            return componente;
        }
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setBackground(HEADER_COLORS[column % HEADER_COLORS.length]); // Definir a cor do cabeçalho
            label.setForeground(Color.BLACK);
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
        }
    }
}