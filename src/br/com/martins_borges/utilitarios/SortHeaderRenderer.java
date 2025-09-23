package br.com.martins_borges.utilitarios;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Polygon;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class SortHeaderRenderer extends DefaultTableCellRenderer {

    private final Color[] headerColors;
    private final Color defaultBackgroundColor;

    public SortHeaderRenderer(Color[] headerColors, Color defaultBackgroundColor) {
        this.headerColors = headerColors;
        this.defaultBackgroundColor = defaultBackgroundColor;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        // 1. Pega o componente JLabel da implementação padrão
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // 2. Define nossa cor de fundo customizada
        if (headerColors != null && column < headerColors.length && headerColors[column] != null) {
            label.setBackground(headerColors[column]);
        } else {
            label.setBackground(defaultBackgroundColor != null ? defaultBackgroundColor : new Color(230, 230, 230));
        }
        label.setOpaque(true);
        label.setBorder(UIManager.getBorder("TableHeader.cellBorder"));

        // 3. Define o ícone de ordenação (ou remove, se não houver)
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
        // Opcional: Define a posição do texto em relação ao ícone
        label.setHorizontalTextPosition(JLabel.LEFT); 
        // Opcional: Define um espaço entre o texto e o ícone
        label.setIconTextGap(4);

        return label;
    }

    /**
     * Uma classe de ícone simples que desenha uma seta (triângulo).
     */
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
            g.setColor(Color.DARK_GRAY); // Cor do triângulo
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
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}
