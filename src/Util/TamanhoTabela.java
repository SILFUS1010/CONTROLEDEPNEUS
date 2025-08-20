package util;

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat; // Para formatar moeda no TableRenderer
import java.time.LocalDate;    // Para formatar data no TableRenderer (se você usar)
import java.time.format.DateTimeFormatter; // Para formatar data
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class TamanhoTabela {
    
    private static final Color[] HEADER_COLORS = {
        new Color(255, 192, 203), // Rosa Claro (ID - 0)
        new Color(135, 206, 250), // Azul Claro (EMPR - 1)
        new Color(240, 230, 140), // Cáqui Claro (FOGO - 2)
        new Color(152, 251, 152), // Verde Claro (FORNECEDOR - 3)
        new Color(255, 218, 185), // Pêssego (VALOR - 4)
        new Color(230, 230, 250), // Lavanda (FABRICANTE - 5)
        new Color(245, 222, 179), // Trigo (TIPO PNEU - 6)
        new Color(176, 224, 230), // Azul Acinzentado (MODELO - 7)
        new Color(218, 112, 214), // Orquídea Claro (DOT - 8)
        new Color(255, 160, 122), // Salmão Claro (MEDIDA - 9)
        new Color(175, 238, 238), // Turquesa Pálido (PROFUND. - 10)
        new Color(255, 228, 196), // Bisque (RECAP. - agora índice 11 na tabela de Pneus)
        new Color(240, 255, 240), // Honeydew (PROJ. KM - agora índice 12)
        new Color(245, 245, 220), // Bege (OBS - agora índice 13)
        // Adicione mais uma cor se sua tabela de Pneus tiver 15 colunas após remover DATA
        new Color(211, 211, 211)  // Cinza Claro (para a 15ª coluna, que era DATA, agora é OBS)
                                  // Ou ajuste o array para ter 14 cores se sua tabela de pneus tem 14 colunas agora
    };


   
    public static void configurar(JTable tabela) {
        // Chama a versão sobrecarregada do método 'configurar',
        // passando null para o array de larguras.
        // Isso fará com que as larguras padrão sejam usadas ou que o ajuste automático
        // (se descomentado em configurar(tabela, null)) seja tentado.
        configurar(tabela, null); // <<< ESTA É A LINHA CORRIGIDA
    }

  
    public static void configurar(JTable tabela, int[] larguraColunas) {
        if (tabela == null) return;

        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Para barra de rolagem horizontal

        // Aplica o Renderer customizado para os CABEÇALHOS
        if (tabela.getTableHeader() != null) {
            tabela.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        }

        // Aplica o Renderer customizado para as CÉLULAS DE DADOS
        tabela.setDefaultRenderer(Object.class, new TableRenderer());

        // Habilita ordenação por clique no cabeçalho
        tabela.setAutoCreateRowSorter(true);

        // Mostra linhas de grade
        tabela.setShowGrid(true);
        tabela.setGridColor(java.awt.Color.LIGHT_GRAY);

        // Define as larguras das colunas se um array for fornecido
        if (larguraColunas != null) {
            definirLarguraColunas(tabela, larguraColunas);
        }
        // else {
            // ajustarLarguraColunas(tabela); // Descomente se quiser tentar ajuste automático de largura
        // }
    }

   
    private static void definirLarguraColunas(JTable tabela, int[] larguraColunas) {
        TableColumnModel columnModel = tabela.getColumnModel();
        for (int i = 0; i < Math.min(columnModel.getColumnCount(), larguraColunas.length); i++) {
            try {
                columnModel.getColumn(i).setPreferredWidth(larguraColunas[i]);
            } catch (ArrayIndexOutOfBoundsException e) { /* Ignora se o array for menor que o número de colunas */ }
        }
    }

   
    private static void ajustarLarguraColunas(JTable tabela) {
        TableColumnModel columnModel = tabela.getColumnModel();
        for (int coluna = 0; coluna < columnModel.getColumnCount(); coluna++) {
            int largura = 50; // Largura mínima padrão
            TableCellRenderer headerRenderer = tabela.getTableHeader().getDefaultRenderer();
            Object headerValue = columnModel.getColumn(coluna).getHeaderValue();
            Component headerComp = headerRenderer.getTableCellRendererComponent(tabela, headerValue, false, false, -1, coluna);
            largura = Math.max(headerComp.getPreferredSize().width + 15, largura); // Adiciona um pouco mais de padding

            for (int linha = 0; linha < tabela.getRowCount(); linha++) {
                try {
                    TableCellRenderer renderer = tabela.getCellRenderer(linha, coluna);
                    Component comp = tabela.prepareRenderer(renderer, linha, coluna);
                    largura = Math.max(comp.getPreferredSize().width + 15, largura); // Adiciona padding
                } catch (Exception e) { /* Ignora erros de célula ao calcular largura */ }
            }
            try {
                columnModel.getColumn(coluna).setPreferredWidth(largura);
            } catch (ArrayIndexOutOfBoundsException e) { /* Ignora */}
        }
    }

  
    private static class TableRenderer extends DefaultTableCellRenderer {
        private final DateTimeFormatter dateFormatterInternal = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private final NumberFormat currencyFormatterInternal = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado, boolean temFoco, int linha, int coluna) {
            Component componente = super.getTableCellRendererComponent(tabela, valor, selecionado, temFoco, linha, coluna);

            if (selecionado) {
                componente.setBackground(new Color(50, 100, 180)); // Azul escuro para seleção
                componente.setForeground(Color.WHITE);
            } else {
                // Define cores de fundo da CÉLULA baseado na COLUNA
                Color corFundo;
                // LEMBRETE: Se a coluna DATA (índice 11 original) foi removida da sua JTable,
                // os índices das colunas seguintes (RECAP, PROJ. KM, OBS) mudam.
                // Ajuste os 'case' abaixo para refletir os ÍNDICES REAIS da sua JTable.
                // Se sua JTable tem 14 colunas (sem data), os índices vão de 0 a 13.
                switch (coluna) {
                    case 0:  corFundo = new Color(255, 255, 220); break; // ID
                    case 1:  corFundo = new Color(230, 240, 255); break; // EMPR
                    case 2:  corFundo = new Color(255, 230, 230); break; // FOGO
                    case 3:  corFundo = new Color(230, 255, 230); break; // FORNECEDOR
                    case 4:  corFundo = new Color(255, 240, 230); break; // VALOR
                    case 5:  corFundo = new Color(240, 230, 250); break; // FABRICANTE
                    case 6:  corFundo = new Color(245, 245, 220); break; // TIPO PNEU
                    case 7:  corFundo = new Color(225, 245, 245); break; // MODELO
                    case 8:  corFundo = new Color(250, 235, 250); break; // DOT
                    case 9:  corFundo = new Color(255, 245, 230); break; // MEDIDA
                    case 10: corFundo = new Color(240, 240, 240); break; // PROFUND.
                    // Se DATA foi removida, o antigo índice 12 (RECAP) agora é 11
                    case 11: corFundo = new Color(255, 228, 196); break; // RECAP.
                    case 12: corFundo = new Color(240, 255, 240); break; // PROJ. KM
                    case 13: corFundo = Color.WHITE; break;              // OBS
                    default: corFundo = Color.WHITE;
                }
                componente.setBackground(corFundo);
                componente.setForeground(Color.BLACK);
            }

            // Alinhamento e Formatação Específica
            setHorizontalAlignment(JLabel.LEFT); // Padrão
            if (valor instanceof Number && coluna != 0 && coluna != 1 && (coluna != 12 || tabela.getColumnName(coluna).equals("PROJ. KM") )) {
                 setHorizontalAlignment(JLabel.RIGHT); // Números à direita (exceto ID, EMPR, e RECAP que pode ser melhor centralizado)
                 if (coluna == 4 && valor != null) { // Coluna VALOR
                      setText(currencyFormatterInternal.format(valor));
                 } else {
                      setText(valor != null ? valor.toString() : "");
                 }
            } else if (valor instanceof LocalDate) { // Formata LocalDate
                 setText(((LocalDate) valor).format(dateFormatterInternal));
                 setHorizontalAlignment(JLabel.CENTER);
            }
            else { // Para Strings e outros, ou colunas específicas
                 // Centralizar colunas
                 if (coluna == 0 || coluna == 1 || coluna == 2 || coluna == 8 || coluna == 9 || coluna == 10 || coluna == 12 || coluna == 13 ) {
                      setHorizontalAlignment(JLabel.CENTER);
                 }
                 setText((valor == null) ? "" : valor.toString());
            }
            return componente;
        }
    } // Fim da classe TableRenderer


   
    private static class HeaderRenderer extends DefaultTableCellRenderer {
        public HeaderRenderer() {
            setOpaque(true); setForeground(Color.BLACK);
            setHorizontalAlignment(JLabel.CENTER); setFont(getFont().deriveFont(java.awt.Font.BOLD));
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); // Chama o super para configurar o JLabel
            // Define a cor de fundo baseada no índice da coluna
            if (column >= 0 && column < HEADER_COLORS.length) {
                setBackground(HEADER_COLORS[column]);
            } else {
                setBackground(Color.LIGHT_GRAY); // Cor padrão se faltar cor no array
            }
            return this; // Retorna o próprio renderer configurado
        }
    } // Fim da classe HeaderRenderer

} 