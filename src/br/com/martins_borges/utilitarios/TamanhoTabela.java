package br.com.martins_borges.utilitarios;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;

public class TamanhoTabela {

    private static Color[] customCellBackgroundColors;

    // NOVA PALETA DE CORES SUAVES (PASTEL) - AGORA COM 10 CORES
    public static final Color[] HEADER_COLORS = {
        new Color(227, 242, 253), // Azul Bebê
        new Color(252, 228, 236), // Rosa Claro
        new Color(232, 245, 233), // Verde Menta
        new Color(255, 249, 231), // Amarelo Baunilha
        new Color(243, 229, 245), // Lilás Claro
        new Color(255, 236, 221), // Pêssego Suave
        new Color(224, 247, 250), // Ciano Claro
        new Color(238, 238, 238), // Cinza Suave
        new Color(255, 240, 245), // Lavanda Rosada
        new Color(240, 255, 240)  // Verde Melão
    };

    public static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void configurar(JTable tabela) { configurar(tabela, null, null); }

    public static void configurar(JTable tabela, int[] larguraColunas) { configurar(tabela, larguraColunas, null); }

    public static void configurar(JTable tabela, int[] larguraColunas, Color[] cellBackgroundColors) {
        customCellBackgroundColors = cellBackgroundColors;
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tabela.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        tabela.setDefaultRenderer(Object.class, new TableRenderer());
        tabela.setDefaultRenderer(String.class, new TableRenderer());
        tabela.setDefaultRenderer(Number.class, new TableRenderer());
        tabela.setDefaultRenderer(LocalDate.class, new DateRenderer());

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
            
            if (largura == 0) {
                tabela.getColumnModel().getColumn(i).setMinWidth(0);
                tabela.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }
    }

    private static void ajustarLarguraColunas(JTable tabela) {
        for (int coluna = 0; coluna < tabela.getColumnCount(); coluna++) {
            int largura = 50;
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
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado, boolean temFoco, int linha, int coluna) {
            Component componente = super.getTableCellRendererComponent(tabela, valor, selecionado, temFoco, linha, coluna);

            if (selecionado) {
                componente.setBackground(new Color(0, 0, 255));
                componente.setForeground(Color.WHITE);
            } else {
                if (customCellBackgroundColors != null && coluna < customCellBackgroundColors.length && customCellBackgroundColors[coluna] != null) {
                    componente.setBackground(customCellBackgroundColors[coluna]);
                } else {
                    componente.setBackground(HEADER_COLORS[coluna % HEADER_COLORS.length]);
                }
                componente.setForeground(Color.BLACK);
            }

            return componente;
        }
    }

    // HEADER RENDERER MODIFICADO PARA INCLUIR ÍCONES DE ORDENAÇÃO
    public static class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value.toString().toUpperCase(), isSelected, hasFocus, row, column);
            
            // Estilo Padrão
            label.setBackground(table.getTableHeader().getBackground());
            label.setForeground(Color.BLACK);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            label.setBorder(BorderFactory.createRaisedBevelBorder()); // Alto relevo

            // Lógica do Ícone de Ordenação
            Icon sortIcon = null;
            if (table != null) {
                RowSorter<? extends TableModel> sorter = table.getRowSorter();
                if (sorter != null) {
                    java.util.List<? extends RowSorter.SortKey> sortKeys = sorter.getSortKeys();
                    if (!sortKeys.isEmpty() && sortKeys.get(0).getColumn() == table.convertColumnIndexToModel(column)) {
                        SortOrder order = sortKeys.get(0).getSortOrder();
                        if (order == SortOrder.ASCENDING) {
                            sortIcon = new SortArrowIcon(SortArrowIcon.Direction.UP);
                        } else if (order == SortOrder.DESCENDING) {
                            sortIcon = new SortArrowIcon(SortArrowIcon.Direction.DOWN);
                        }
                    }
                }
            }
            
            label.setIcon(sortIcon);
            label.setHorizontalTextPosition(JLabel.LEFT);
            label.setIconTextGap(4);

            return label;
        }
    }
    
    // CLASSE INTERNA PARA DESENHAR O ÍCONE DE SETA
    private static class SortArrowIcon implements Icon {
        public enum Direction { UP, DOWN }
        private final Direction direction;
        private final int width = 8;
        private final int height = 8;

        public SortArrowIcon(Direction direction) {
            this.direction = direction;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(Color.DARK_GRAY);
            Polygon p = new Polygon();
            if (direction == Direction.UP) {
                p.addPoint(x, y + height);
                p.addPoint(x + width, y + height);
                p.addPoint(x + width / 2, y);
            } else { // DOWN
                p.addPoint(x, y);
                p.addPoint(x + width, y);
                p.addPoint(x + width / 2, y + height);
            }
            g.fillPolygon(p);
        }

        @Override
        public int getIconWidth() { return width; }
        @Override
        public int getIconHeight() { return height; }
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
