package br.com.martins_borges.telas;

import br.com.martins_borges.dal.PneuDAO;
import br.com.martins_borges.dal.VeiculoDAO;
import br.com.martins_borges.model.Pneu;
import br.com.martins_borges.model.Veiculo;
import java.awt.Image;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class TelaControleDePneus extends javax.swing.JDialog {

    private final VeiculoDAO veiculoDAO;
    private final PneuDAO pneuDAO;
    private List<Veiculo> listaDeVeiculos;

    private javax.swing.JLabel[][] slotsDePneus;
    private final Map<Integer, VehicleConfig> vehicleConfigs = new HashMap<>();

    public TelaControleDePneus(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.veiculoDAO = new VeiculoDAO();
        this.pneuDAO = new PneuDAO();
        initComponents();
        loadVehicleConfigs();
        inicializarComponentesChassi();

        definirTamanhoEPosicao();
        atualizarTabelaVeiculos();

        Triangulo1.setVisible(false);
        Triangulo2.setVisible(false);
        Triangulo3.setVisible(false);
        Triangulo4.setVisible(false);
        engate1.setVisible(false);
        engate2.setVisible(false);
        engate3.setVisible(false);
        engate4.setVisible(false);

        TELA_ZERO.setComponentZOrder(Triangulo1, 0);
        TELA_ZERO.setComponentZOrder(Triangulo2, 0);
        TELA_ZERO.setComponentZOrder(Triangulo3, 0);
        TELA_ZERO.setComponentZOrder(Triangulo4, 0);
    }

    public enum TipoEixo {
        SIMPLES, DUPLO
    }

    public enum AlinhamentoVertical {
        TOPO, CENTRO, BASE
    }

    private static class VehicleConfig {

        final TipoEixo[] tipos;
        final boolean[] visibilidade;
        final int espacamento;
        final AlinhamentoVertical alinhamento;
        final int[] deslocamentos;
        final int[] ajustesVerticais;
        final int[] largurasEixos;
        final int[] posicoesEixos;

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

    private void loadVehicleConfigs() {
        vehicleConfigs.put(0, new VehicleConfig(new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.DUPLO, TipoEixo.DUPLO}, new boolean[]{true, true, true, false, false, false}, 120, AlinhamentoVertical.TOPO, new int[]{-30, -10, -10}, new int[]{100, 0, 100}, new int[]{140, 280, 280}, null));
        vehicleConfigs.put(1, new VehicleConfig(new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES}, new boolean[]{true, true, false, false, false, false}, 287, AlinhamentoVertical.TOPO, new int[]{-30, -30, -30}, new int[]{0, 0, 100}, new int[]{150, 150}, null));
        vehicleConfigs.put(2, new VehicleConfig(new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES}, new boolean[]{true, false, true, false, false, true}, 200, AlinhamentoVertical.CENTRO, new int[]{-30, -30, -30}, new int[]{0, 0, 0}, new int[]{150, 150, 150}, null));
        vehicleConfigs.put(3, new VehicleConfig(new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.DUPLO}, new boolean[]{true, false, false, false, false, true}, 300, AlinhamentoVertical.BASE, new int[]{-30, -30, -30}, new int[]{50, 0, 0}, new int[]{150, 220}, null));
        vehicleConfigs.put(4, new VehicleConfig(new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO}, new boolean[]{true, true, true, true, false, false}, 100, AlinhamentoVertical.TOPO, new int[]{-30, -30, -30, -30}, new int[]{0, 0, 110}, new int[]{230, 230, 230, 230}, null));
        vehicleConfigs.put(5, new VehicleConfig(new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES}, new boolean[]{true, true, true, true, false, false}, 100, AlinhamentoVertical.CENTRO, new int[]{-30, -30, -30, -30}, new int[]{0, 80, 0}, new int[]{150, 150, 150, 150}, null));
        vehicleConfigs.put(6, new VehicleConfig(new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.DUPLO, TipoEixo.DUPLO}, new boolean[]{true, true, false, false, true, true}, 135, AlinhamentoVertical.BASE, new int[]{-30, -30, -30, -30}, new int[]{30, 0, 0}, new int[]{150, 150, 280, 280}, new int[]{50, 20, 0, 0, 70, 50}));
        vehicleConfigs.put(7, new VehicleConfig(new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO}, new boolean[]{true, true, false, false, false, false}, 100, AlinhamentoVertical.TOPO, new int[]{-30, -30, -30}, new int[]{0, 0, 100}, new int[]{220, 220}, null));
        vehicleConfigs.put(8, new VehicleConfig(new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO}, new boolean[]{true, true, true, false, false, false}, 90, AlinhamentoVertical.BASE, new int[]{-30, -30, -30}, new int[]{-15, 0, 0}, new int[]{220, 220, 220}, null));
        vehicleConfigs.put(9, new VehicleConfig(new TipoEixo[]{TipoEixo.DUPLO}, new boolean[]{true, false, false, false, false, false}, 100, AlinhamentoVertical.CENTRO, new int[]{-30, -30, -30}, new int[]{0, 50, 0}, new int[]{220}, null));
        vehicleConfigs.put(10, new VehicleConfig(new TipoEixo[]{TipoEixo.SIMPLES}, new boolean[]{true, false, false, false, false, false}, 100, AlinhamentoVertical.CENTRO, new int[]{-30, -30, -30}, new int[]{0, 30, 0}, new int[]{150}, null));
        vehicleConfigs.put(11, new VehicleConfig(new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.SIMPLES}, new boolean[]{true, true, false, false, false, false}, 200, AlinhamentoVertical.CENTRO, new int[]{-30, -30, -30}, new int[]{0, 0, 0}, new int[]{220, 150}, null));
        vehicleConfigs.put(12, new VehicleConfig(new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO}, new boolean[]{true, true, true, true, true, true}, 100, AlinhamentoVertical.TOPO, new int[]{-30, -30, -30, -30, -30, -30}, new int[]{0, 0, 100}, new int[]{220, 220, 220, 220, 220, 220}, null));
        vehicleConfigs.put(13, new VehicleConfig(new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES}, new boolean[]{true, true, true, true, true, false}, 100, AlinhamentoVertical.CENTRO, new int[]{-30, -30, -30, -30, -30}, new int[]{0, 0, 0}, new int[]{150, 150, 150, 150, 150}, null));
        vehicleConfigs.put(14, new VehicleConfig(new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.DUPLO, TipoEixo.DUPLO}, new boolean[]{true, true, true, true, true, false}, 100, AlinhamentoVertical.BASE, new int[]{-30, -30, -30, -30, -30}, new int[]{20, 0, 30}, new int[]{150, 150, 150, 240, 240}, new int[]{-80, -80, -80, 30, 30}));
        vehicleConfigs.put(15, new VehicleConfig(new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES}, new boolean[]{true, true, true, true, true, true}, 100, AlinhamentoVertical.TOPO, new int[]{-30, -30, -30, -30, -30, -30}, new int[]{0, 0, 110}, new int[]{150, 150, 150, 150, 150, 150}, null));
        vehicleConfigs.put(16, new VehicleConfig(new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.DUPLO, TipoEixo.DUPLO}, new boolean[]{true, false, false, false, true, true}, 150, AlinhamentoVertical.BASE, new int[]{-30, -30, -30}, new int[]{80, 0, 0}, new int[]{150, 220, 220}, new int[]{0, 0, 0, 0, 90, 50}));
        vehicleConfigs.put(17, new VehicleConfig(new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO}, new boolean[]{true, false, false, false, false, true}, 370, AlinhamentoVertical.TOPO, new int[]{-30, -30, -30}, new int[]{30, 0, 100}, new int[]{230, 230}, null));
        vehicleConfigs.put(18, new VehicleConfig(new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO}, new boolean[]{true, false, false, false, false, true}, 120, AlinhamentoVertical.BASE, new int[]{-30, -30, 0}, new int[]{30, 0, 0}, new int[]{210, 210}, null));
    }

    private void inicializarComponentesChassi() {
        slotsDePneus = new javax.swing.JLabel[6][4];
        slotsDePneus[0][0] = Label100;
        slotsDePneus[0][1] = Label101;
        slotsDePneus[0][2] = Label102;
        slotsDePneus[0][3] = Label103;
        slotsDePneus[1][0] = Label104;
        slotsDePneus[1][1] = Label105;
        slotsDePneus[1][2] = Label106;
        slotsDePneus[1][3] = Label107;
        slotsDePneus[2][0] = Label108;
        slotsDePneus[2][1] = Label109;
        slotsDePneus[2][2] = Label110;
        slotsDePneus[2][3] = Label111;
        slotsDePneus[3][0] = Label112;
        slotsDePneus[3][1] = Label113;
        slotsDePneus[3][2] = Label114;
        slotsDePneus[3][3] = Label115;
        slotsDePneus[4][0] = Label116;
        slotsDePneus[4][1] = Label117;
        slotsDePneus[4][2] = Label118;
        slotsDePneus[4][3] = Label119;
        slotsDePneus[5][0] = Label120;
        slotsDePneus[5][1] = Label121;
        slotsDePneus[5][2] = Label122;
        slotsDePneus[5][3] = Label123;
    }

    private void desenharChassi(TipoEixo[] tipos, boolean[] visibilidade, int espacamento, AlinhamentoVertical alinhamento, int[] deslocamentos, int[] ajustesVerticais, int[] largurasEixos, int[] posicoesEixos) {
        int numEixosVisiveis = 0;
        for (boolean v : visibilidade) {
            if (v) {
                numEixosVisiveis++;
            }
        }
        if (numEixosVisiveis != tipos.length || (largurasEixos != null && numEixosVisiveis != largurasEixos.length)) {
            JOptionPane.showMessageDialog(this, "Erro de configuração: O número de eixos visíveis, tipos e larguras devem corresponder.", "Erro de Desenho", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JLabel[] todosOsEixos = {lbeixo1, lbeixo2, lbeixo3, lbeixo4, lbeixo5, lbeixo6, lbeixo7, lbeixo8, lbeixo9};

        java.net.URL imgUrl = getClass().getResource("/br/com/martins_borges/telas/Imagens/pneu.png");
        if (imgUrl == null) {
            JOptionPane.showMessageDialog(this, "Erro Crítico: Não foi possível encontrar a imagem 'pneu.png'.\nVerifique se o arquivo existe em src/br/com/martins_borges/telas/Imagens e se o nome está correto.", "Recurso Não Encontrado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ImageIcon iconeOriginal = new ImageIcon(imgUrl);

        // Bloco Novo e Corrigido
        int larguraPneu = 45, alturaPneu = 70;
        ImageIcon iconPneuNormal = redimensionarIcone(iconeOriginal, larguraPneu, alturaPneu);

// --- Lógica de colorização inteligente que preserva os detalhes ---
        Image imagemOriginal = iconPneuNormal.getImage();
        java.awt.image.ImageFilter filtroCinza = new java.awt.image.RGBImageFilter() {
            @Override
            public int filterRGB(int x, int y, int rgb) {
                int alpha = (rgb >> 24) & 0xff; // Preserva a transparência
                int red = (rgb >> 16) & 0xff;
                int green = (rgb >> 8) & 0xff;
                int blue = rgb & 0xff;

                // Calcula a "luminosidade" (brilho) do pixel
                int luminosidade = (int) (0.299 * red + 0.587 * green + 0.114 * blue);

                // Cria a nova cor cinza usando a luminosidade
                int cinza = (luminosidade << 16) | (luminosidade << 8) | luminosidade;

                // Retorna a cor final com a transparência original
                return (alpha << 24) | cinza;
            }
        };
        Image imagemCinza = java.awt.Toolkit.getDefaultToolkit().createImage(new java.awt.image.FilteredImageSource(imagemOriginal.getSource(), filtroCinza));
        ImageIcon iconPneuCinza = new ImageIcon(imagemCinza);
// --- Fim da nova lógica ---
        int centroChassiX = TELA_ZERO.getWidth() / 2;
        for (JLabel eixo : todosOsEixos) {
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
        int alturaTotalChassi = (numEixosVisiveis > 0 ? (numEixosVisiveis - 1) * espacamento + alturaPneu : 0);
        int yInicial;
        int ajusteBase = (ajustesVerticais != null && ajustesVerticais.length >= 1) ? ajustesVerticais[0] : 30;
        int ajusteCentro = (ajustesVerticais != null && ajustesVerticais.length >= 2) ? ajustesVerticais[1] : 0;
        int ajusteTopo = (ajustesVerticais != null && ajustesVerticais.length >= 3) ? ajustesVerticais[2] : 30;
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
        int contadorDeReceita = 0;
        int yEixoAtual = yInicial;
        for (int i = 0; i < visibilidade.length; i++) {
            if (visibilidade[i]) {
                JLabel eixoAtual = todosOsEixos[i];
                TipoEixo tipoDoEixo = tipos[contadorDeReceita];
                int deslocamento = (deslocamentos != null && contadorDeReceita < deslocamentos.length) ? deslocamentos[contadorDeReceita] : 0;
                int larguraDoEixoAtual = (largurasEixos != null && contadorDeReceita < largurasEixos.length) ? largurasEixos[contadorDeReceita] : (tipoDoEixo == TipoEixo.SIMPLES ? 190 : 280);
                int ajusteFino = (posicoesEixos != null && i < posicoesEixos.length) ? posicoesEixos[i] : 0;
                eixoAtual.setVisible(true);
                int alturaEixo = eixoAtual.getHeight();
                JLabel pneuEsqExterno = slotsDePneus[i][0], pneuEsqInterno = slotsDePneus[i][1];
                JLabel pneuDirInterno = slotsDePneus[i][2], pneuDirExterno = slotsDePneus[i][3];
                if (tipoDoEixo == TipoEixo.SIMPLES) {
                    eixoAtual.setBounds(centroChassiX - (larguraDoEixoAtual / 2), yEixoAtual + ajusteFino, larguraDoEixoAtual, alturaEixo);
                    int yPneu = yEixoAtual + ajusteFino + (alturaEixo / 2) - (alturaPneu / 2);
                    int xPneuEsquerdo = eixoAtual.getX() + deslocamento;
                    int xPneuDireito = eixoAtual.getX() + larguraDoEixoAtual - larguraPneu - deslocamento;
                    configuraPneu(pneuEsqExterno, iconPneuCinza, xPneuEsquerdo, yPneu);
                    configuraPneu(pneuDirExterno, iconPneuCinza, xPneuDireito, yPneu);
                    pneuEsqInterno.setVisible(false);
                    pneuDirInterno.setVisible(false);
                } else if (tipoDoEixo == TipoEixo.DUPLO) {
                    eixoAtual.setBounds(centroChassiX - (larguraDoEixoAtual / 2), yEixoAtual + ajusteFino, larguraDoEixoAtual, alturaEixo);
                    int espacamentoPneus = 5;
                    int yPneu = yEixoAtual + ajusteFino + (alturaEixo / 2) - (alturaPneu / 2);
                    int xPneuEsqExterno = eixoAtual.getX() + deslocamento;
                    int xPneuEsqInterno = eixoAtual.getX() + larguraPneu + espacamentoPneus + deslocamento;
                    int xPneuDirExterno = eixoAtual.getX() + larguraDoEixoAtual - larguraPneu - deslocamento;
                    int xPneuDirInterno = eixoAtual.getX() + larguraDoEixoAtual - (larguraPneu * 2) - espacamentoPneus - deslocamento;
                    configuraPneu(pneuEsqExterno, iconPneuCinza, xPneuEsqExterno, yPneu);
                    configuraPneu(pneuEsqInterno, iconPneuCinza, xPneuEsqInterno, yPneu);
                    configuraPneu(pneuDirInterno, iconPneuCinza, xPneuDirInterno, yPneu);
                    configuraPneu(pneuDirExterno, iconPneuCinza, xPneuDirExterno, yPneu);
                }
                yEixoAtual += espacamento;
                contadorDeReceita++;
            }
        }
        JLabel primeiroEixoVisivel = null, ultimoEixoVisivel = null;
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

    private javax.swing.ImageIcon redimensionarIcone(javax.swing.ImageIcon iconeOriginal, int largura, int altura) {
        if (iconeOriginal == null) {
            return null;
        }
        return new javax.swing.ImageIcon(iconeOriginal.getImage().getScaledInstance(largura, altura, java.awt.Image.SCALE_SMOOTH));
    }

    private void configuraPneu(javax.swing.JLabel pneuLabel, javax.swing.ImageIcon icone, int x, int y) {
        if (pneuLabel != null) {
            pneuLabel.setIcon(icone);
            pneuLabel.setBounds(x, y, icone.getIconWidth(), icone.getIconHeight());
            pneuLabel.setVisible(true);
            // Define como desabilitado para representar pneu vazio/sem uso
            pneuLabel.setEnabled(false);
            TELA_ZERO.setComponentZOrder(pneuLabel, 0);
        }
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

        Tabela_Exibicao_veiculos.setModel(model);
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Tipo_pneu = new javax.swing.JLabel();
        PainelManutencao = new javax.swing.JPanel();
        lbtitulo = new javax.swing.JLabel();
        lbconserto = new javax.swing.JLabel();
        conserto = new javax.swing.JLabel();
        lbestoque = new javax.swing.JLabel();
        estoque = new javax.swing.JLabel();
        lbsucata = new javax.swing.JLabel();
        sucata = new javax.swing.JLabel();
        PNEUS_ESTOQUE = new javax.swing.JScrollPane();
        Tabela_Exibicao_pneus_em_estoque = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tabela_Exibicao_veiculos = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabela_ExibicaoPneuUsado = new javax.swing.JTable();
        TELA_ZERO = new javax.swing.JPanel();
        lbespinha_dorsal = new javax.swing.JLabel();
        lbeixo1 = new javax.swing.JLabel();
        lbeixo2 = new javax.swing.JLabel();
        lbeixo3 = new javax.swing.JLabel();
        lbeixo4 = new javax.swing.JLabel();
        lbeixo5 = new javax.swing.JLabel();
        lbeixo6 = new javax.swing.JLabel();
        lbeixo7 = new javax.swing.JLabel();
        lbeixo8 = new javax.swing.JLabel();
        lbeixo9 = new javax.swing.JLabel();
        Label100 = new javax.swing.JLabel();
        Label101 = new javax.swing.JLabel();
        Label102 = new javax.swing.JLabel();
        Label103 = new javax.swing.JLabel();
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
        Label124 = new javax.swing.JLabel();
        Label125 = new javax.swing.JLabel();
        Label126 = new javax.swing.JLabel();
        Label127 = new javax.swing.JLabel();
        Label128 = new javax.swing.JLabel();
        Label129 = new javax.swing.JLabel();
        Label130 = new javax.swing.JLabel();
        Label131 = new javax.swing.JLabel();
        Label132 = new javax.swing.JLabel();
        Label133 = new javax.swing.JLabel();
        Label134 = new javax.swing.JLabel();
        Label135 = new javax.swing.JLabel();
        Triangulo1 = new javax.swing.JLabel();
        engate1 = new javax.swing.JLabel();
        Triangulo2 = new javax.swing.JLabel();
        engate2 = new javax.swing.JLabel();
        Triangulo3 = new javax.swing.JLabel();
        engate3 = new javax.swing.JLabel();
        Triangulo4 = new javax.swing.JLabel();
        engate4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("TelaControleDePneus"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1000, 690));
        getContentPane().setLayout(null);
        getContentPane().add(Tipo_pneu);
        Tipo_pneu.setBounds(593, 61, 202, 22);

        PainelManutencao.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PainelManutencao.setLayout(null);

        lbtitulo.setBackground(new java.awt.Color(102, 102, 102));
        lbtitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbtitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbtitulo.setText("AÇOES DO PNEU");
        lbtitulo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbtitulo.setOpaque(true);
        PainelManutencao.add(lbtitulo);
        lbtitulo.setBounds(0, 0, 130, 20);

        lbconserto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbconserto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/support.png"))); // NOI18N
        lbconserto.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lbconserto.setName("AG. CONSERTO"); // NOI18N
        PainelManutencao.add(lbconserto);
        lbconserto.setBounds(9, 30, 100, 104);

        conserto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        conserto.setText("AG. CONSERTO");
        conserto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        PainelManutencao.add(conserto);
        conserto.setBounds(20, 110, 85, 16);

        lbestoque.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbestoque.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/ESTOQUE.png"))); // NOI18N
        lbestoque.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbestoque.setMaximumSize(new java.awt.Dimension(64, 64));
        lbestoque.setMinimumSize(new java.awt.Dimension(64, 64));
        lbestoque.setName("ESTOQUE"); // NOI18N
        lbestoque.setPreferredSize(new java.awt.Dimension(64, 64));
        PainelManutencao.add(lbestoque);
        lbestoque.setBounds(10, 190, 100, 120);

        estoque.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        estoque.setText("ESTOQUE");
        estoque.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        PainelManutencao.add(estoque);
        estoque.setBounds(30, 290, 53, 16);

        lbsucata.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbsucata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/LIXO.png"))); // NOI18N
        lbsucata.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        PainelManutencao.add(lbsucata);
        lbsucata.setBounds(20, 370, 77, 110);

        sucata.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        sucata.setText("SUCATA");
        sucata.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        PainelManutencao.add(sucata);
        sucata.setBounds(35, 460, 50, 16);

        getContentPane().add(PainelManutencao);
        PainelManutencao.setBounds(490, 160, 128, 505);

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
        PNEUS_ESTOQUE.setBounds(643, 18, 330, 150);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(900, 402));

        Tabela_Exibicao_veiculos.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Tabela_Exibicao_veiculos.setModel(new javax.swing.table.DefaultTableModel(
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
        Tabela_Exibicao_veiculos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        Tabela_Exibicao_veiculos.setMaximumSize(null);
        Tabela_Exibicao_veiculos.setName("Tabela_Exibicao_veiculos"); // NOI18N
        Tabela_Exibicao_veiculos.getTableHeader().setReorderingAllowed(false);
        Tabela_Exibicao_veiculos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tabela_Exibicao_veiculosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(Tabela_Exibicao_veiculos);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(640, 180, 330, 348);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(900, 402));

        Tabela_ExibicaoPneuUsado.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Tabela_ExibicaoPneuUsado.setModel(new javax.swing.table.DefaultTableModel(
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
        Tabela_ExibicaoPneuUsado.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        Tabela_ExibicaoPneuUsado.setMaximumSize(null);
        Tabela_ExibicaoPneuUsado.setName("Tabela_Exibicao_veiculos"); // NOI18N
        Tabela_ExibicaoPneuUsado.getTableHeader().setReorderingAllowed(false);
        Tabela_ExibicaoPneuUsado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tabela_ExibicaoPneuUsadoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Tabela_ExibicaoPneuUsado);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(640, 540, 330, 134);

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

        lbespinha_dorsal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbespinha_dorsal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/espinha-dorsal.png"))); // NOI18N
        lbespinha_dorsal.setToolTipText("");
        TELA_ZERO.add(lbespinha_dorsal);
        lbespinha_dorsal.setBounds(166, 30, 7, 550);

        lbeixo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo1);
        lbeixo1.setBounds(60, 30, 220, 7);

        lbeixo2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo2);
        lbeixo2.setBounds(60, 99, 220, 7);

        lbeixo3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo3);
        lbeixo3.setBounds(60, 167, 220, 7);

        lbeixo4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo4);
        lbeixo4.setBounds(60, 236, 220, 7);

        lbeixo5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo5);
        lbeixo5.setBounds(60, 305, 220, 7);

        lbeixo6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo6);
        lbeixo6.setBounds(60, 374, 220, 7);

        lbeixo7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo7);
        lbeixo7.setBounds(60, 443, 220, 7);

        lbeixo8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo8);
        lbeixo8.setBounds(60, 511, 220, 7);

        lbeixo9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbeixo9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/Martins_Borges/telas/Imagens/eixo-horizontal .png"))); // NOI18N
        lbeixo9.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        TELA_ZERO.add(lbeixo9);
        lbeixo9.setBounds(60, 580, 220, 7);
        TELA_ZERO.add(Label100);
        Label100.setBounds(40, 20, 32, 30);
        TELA_ZERO.add(Label101);
        Label101.setBounds(90, 20, 32, 30);
        TELA_ZERO.add(Label102);
        Label102.setBounds(260, 20, 32, 30);
        TELA_ZERO.add(Label103);
        Label103.setBounds(210, 20, 32, 30);
        TELA_ZERO.add(Label104);
        Label104.setBounds(50, 90, 32, 30);
        TELA_ZERO.add(Label105);
        Label105.setBounds(90, 90, 32, 30);
        TELA_ZERO.add(Label106);
        Label106.setBounds(280, 90, 32, 30);
        TELA_ZERO.add(Label107);
        Label107.setBounds(250, 90, 32, 30);
        TELA_ZERO.add(Label108);
        Label108.setBounds(40, 150, 32, 30);
        TELA_ZERO.add(Label109);
        Label109.setBounds(70, 150, 32, 30);
        TELA_ZERO.add(Label110);
        Label110.setBounds(270, 150, 32, 30);
        TELA_ZERO.add(Label111);
        Label111.setBounds(220, 150, 32, 30);
        TELA_ZERO.add(Label112);
        Label112.setBounds(50, 220, 32, 30);
        TELA_ZERO.add(Label113);
        Label113.setBounds(80, 220, 32, 30);
        TELA_ZERO.add(Label114);
        Label114.setBounds(260, 220, 32, 30);
        TELA_ZERO.add(Label115);
        Label115.setBounds(210, 220, 32, 30);
        TELA_ZERO.add(Label116);
        Label116.setBounds(50, 290, 32, 30);
        TELA_ZERO.add(Label117);
        Label117.setBounds(80, 290, 32, 30);
        TELA_ZERO.add(Label118);
        Label118.setBounds(260, 290, 32, 30);
        TELA_ZERO.add(Label119);
        Label119.setBounds(210, 290, 32, 30);
        TELA_ZERO.add(Label120);
        Label120.setBounds(50, 360, 32, 30);
        TELA_ZERO.add(Label121);
        Label121.setBounds(80, 360, 32, 30);
        TELA_ZERO.add(Label122);
        Label122.setBounds(260, 360, 32, 30);
        TELA_ZERO.add(Label123);
        Label123.setBounds(210, 360, 32, 30);
        TELA_ZERO.add(Label124);
        Label124.setBounds(210, 430, 32, 30);
        TELA_ZERO.add(Label125);
        Label125.setBounds(260, 430, 32, 30);
        TELA_ZERO.add(Label126);
        Label126.setBounds(80, 430, 32, 30);
        TELA_ZERO.add(Label127);
        Label127.setBounds(50, 430, 32, 30);
        TELA_ZERO.add(Label128);
        Label128.setBounds(260, 500, 32, 30);
        TELA_ZERO.add(Label129);
        Label129.setBounds(80, 500, 32, 30);
        TELA_ZERO.add(Label130);
        Label130.setBounds(50, 500, 32, 30);
        TELA_ZERO.add(Label131);
        Label131.setBounds(210, 500, 32, 30);
        TELA_ZERO.add(Label132);
        Label132.setBounds(210, 570, 32, 30);
        TELA_ZERO.add(Label133);
        Label133.setBounds(260, 570, 32, 30);
        TELA_ZERO.add(Label134);
        Label134.setBounds(80, 570, 32, 30);
        TELA_ZERO.add(Label135);
        Label135.setBounds(50, 570, 32, 30);

        Triangulo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/martins_borges/telas/Imagens/triangulo.png"))); // NOI18N
        TELA_ZERO.add(Triangulo1);
        Triangulo1.setBounds(150, 150, 40, 40);

        engate1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/martins_borges/telas/Imagens/eixo-engate .png"))); // NOI18N
        TELA_ZERO.add(engate1);
        engate1.setBounds(60, 200, 220, 4);

        Triangulo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/martins_borges/telas/Imagens/triangulo.png"))); // NOI18N
        TELA_ZERO.add(Triangulo2);
        Triangulo2.setBounds(150, 280, 40, 40);

        engate2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/martins_borges/telas/Imagens/eixo-engate .png"))); // NOI18N
        TELA_ZERO.add(engate2);
        engate2.setBounds(60, 340, 220, 4);

        Triangulo3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/martins_borges/telas/Imagens/triangulo.png"))); // NOI18N
        TELA_ZERO.add(Triangulo3);
        Triangulo3.setBounds(150, 360, 40, 40);

        engate3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/martins_borges/telas/Imagens/eixo-engate .png"))); // NOI18N
        TELA_ZERO.add(engate3);
        engate3.setBounds(60, 400, 220, 4);

        Triangulo4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/martins_borges/telas/Imagens/triangulo.png"))); // NOI18N
        TELA_ZERO.add(Triangulo4);
        Triangulo4.setBounds(150, 430, 40, 40);

        engate4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/martins_borges/telas/Imagens/eixo-engate .png"))); // NOI18N
        TELA_ZERO.add(engate4);
        engate4.setBounds(60, 470, 220, 4);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("1");
        TELA_ZERO.add(jLabel1);
        jLabel1.setBounds(10, 20, 10, 25);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("2");
        TELA_ZERO.add(jLabel2);
        jLabel2.setBounds(10, 90, 10, 25);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("3");
        TELA_ZERO.add(jLabel3);
        jLabel3.setBounds(10, 155, 10, 25);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("4");
        TELA_ZERO.add(jLabel4);
        jLabel4.setBounds(10, 225, 10, 25);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("5");
        TELA_ZERO.add(jLabel5);
        jLabel5.setBounds(10, 290, 10, 25);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText("6");
        TELA_ZERO.add(jLabel6);
        jLabel6.setBounds(10, 360, 10, 25);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("7");
        TELA_ZERO.add(jLabel7);
        jLabel7.setBounds(10, 430, 10, 25);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setText("8");
        TELA_ZERO.add(jLabel8);
        jLabel8.setBounds(10, 500, 10, 25);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel9.setText("9");
        TELA_ZERO.add(jLabel9);
        jLabel9.setBounds(10, 570, 10, 25);

        getContentPane().add(TELA_ZERO);
        TELA_ZERO.setBounds(20, 40, 360, 630);

        getAccessibleContext().setAccessibleName("TelaControleDePneus");

        setSize(new java.awt.Dimension(1016, 711));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void Tabela_Exibicao_pneus_em_estoqueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Tabela_Exibicao_pneus_em_estoqueMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_Tabela_Exibicao_pneus_em_estoqueMouseClicked

    private void Tabela_ExibicaoPneuUsadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Tabela_ExibicaoPneuUsadoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_Tabela_ExibicaoPneuUsadoMouseClicked

    private void Tabela_Exibicao_veiculosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Tabela_Exibicao_veiculosMouseClicked
      
   
    int selectedRow = Tabela_Exibicao_veiculos.getSelectedRow();
    if (selectedRow == -1) {
        return;
    }

    // Pega o objeto Veiculo completo da linha selecionada
    Veiculo veiculoSelecionado = this.listaDeVeiculos.get(selectedRow);
    
    // Pega o ID do tipo de veículo, que servirá como chave para a configuração
    int idConfig = veiculoSelecionado.getID_CONFIG_FK();

    // Declara as variáveis que irão guardar as "instruções de desenho"
    TipoEixo[] tipos;
    boolean[] visibilidade;
    int espacamento;
    AlinhamentoVertical alinhamento;
    int[] deslocamentos;
    int[] ajustesVerticais;
    int[] largurasEixos;
    int[] posicoesEixos = null; // Padrão é null, usado para ajustes finos

    // Estrutura de decisão para carregar as instruções de desenho corretas para cada veículo
    switch (idConfig) {
        case 0:
            tipos = new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.DUPLO, TipoEixo.DUPLO}; // Define o tipo de cada eixo (1º simples, 2º duplo, 3º duplo).
            visibilidade = new boolean[]{true, true, true, false, false, false, false, false, false}; // Mostra apenas os 3 primeiros eixos dos 9 disponíveis.
            espacamento = 120;           // Distância VERTICAL entre um eixo e outro. Menor = mais juntos.
            alinhamento = AlinhamentoVertical.TOPO; // Posição VERTICAL de todo o chassi no painel.
            deslocamentos = new int[]{-30, -10, -10}; // Ajuste fino HORIZONTAL dos pneus. Negativo = mais para dentro.
            ajustesVerticais = new int[]{0, 0, 0}; // Ajuste fino VERTICAL de todo o chassi. // Ajustes: {BASE, CENTRO, TOPO}
            largurasEixos = new int[]{140, 280, 280}; // Largura HORIZONTAL de cada eixo visível.
            break;
        case 1:
            tipos = new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES};
            visibilidade = new boolean[]{true, true, false, false, false, false, false, false, false};
            espacamento = 180;
            alinhamento = AlinhamentoVertical.TOPO;
            deslocamentos = new int[]{-30, -30, -30};
            ajustesVerticais = new int[]{0, 0, 40};
            largurasEixos = new int[]{150, 150};
            break;
        case 2:
            tipos = new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES};
            visibilidade = new boolean[]{true, false, true, false, false, true, false, false, false}; // Mostra os eixos 1, 3 e 6.
            espacamento = 200;
            alinhamento = AlinhamentoVertical.CENTRO;
            deslocamentos = new int[]{-30, -30, -30};
            ajustesVerticais = new int[]{0, 0, 0};
            largurasEixos = new int[]{150, 150, 150};
            break;
        case 3:
            tipos = new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.DUPLO};
            visibilidade = new boolean[]{true, false, false, false, false, true, false, false, false}; // Mostra os eixos 1 e 6.
            espacamento = 300;
            alinhamento = AlinhamentoVertical.BASE;
            deslocamentos = new int[]{-30, -30, -30};
            ajustesVerticais = new int[]{50, 0, 0};
            largurasEixos = new int[]{150, 220};
            break;
        case 4:
            tipos = new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO};
            visibilidade = new boolean[]{true, true, true, true, false, false, false, false, false}; // Mostra os 4 primeiros eixos.
            espacamento = 100;
            alinhamento = AlinhamentoVertical.TOPO;
            deslocamentos = new int[]{-30, -30, -30, -30};
            ajustesVerticais = new int[]{0, 0, 110};
            largurasEixos = new int[]{230, 230, 230, 230};
            break;
        case 5:
            tipos = new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES};
            visibilidade = new boolean[]{true, true, true, true, false, false, false, false, false};
            espacamento = 100;
            alinhamento = AlinhamentoVertical.CENTRO;
            deslocamentos = new int[]{-30, -30, -30, -30};
            ajustesVerticais = new int[]{0, 80, 0};
            largurasEixos = new int[]{150, 150, 150, 150};
            break;
        case 6:
            tipos = new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.DUPLO, TipoEixo.DUPLO};
            visibilidade = new boolean[]{true, true, false, false, true, true, false, false, false};
            espacamento = 135;
            alinhamento = AlinhamentoVertical.BASE;
            deslocamentos = new int[]{-30, -30, -30, -30};
            ajustesVerticais = new int[]{30, 0, 0};
            largurasEixos = new int[]{150, 150, 280, 280};
            posicoesEixos = new int[]{50, 20, 0, 0, 70, 50, 0, 0, 0}; // Ajuste fino VERTICAL de cada eixo individualmente.
            break;
        case 7:
            tipos = new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO};
            visibilidade = new boolean[]{true, true, false, false, false, false, false, false, false};
            espacamento = 100;
            alinhamento = AlinhamentoVertical.TOPO;
            deslocamentos = new int[]{-30, -30, -30};
            ajustesVerticais = new int[]{0, 0, 100};
            largurasEixos = new int[]{220, 220};
            break;
        case 8:
            tipos = new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO};
            visibilidade = new boolean[]{true, true, true, false, false, false, false, false, false};
            espacamento = 90;
            alinhamento = AlinhamentoVertical.BASE;
            deslocamentos = new int[]{-30, -30, -30};
            ajustesVerticais = new int[]{-15, 0, 0};
            largurasEixos = new int[]{220, 220, 220};
            break;
        case 9:
            tipos = new TipoEixo[]{TipoEixo.DUPLO};
            visibilidade = new boolean[]{true, false, false, false, false, false, false, false, false};
            espacamento = 100;
            alinhamento = AlinhamentoVertical.CENTRO;
            deslocamentos = new int[]{-30, -30, -30};
            ajustesVerticais = new int[]{0, 50, 0};
            largurasEixos = new int[]{220};
            break;
        case 10:
            tipos = new TipoEixo[]{TipoEixo.SIMPLES};
            visibilidade = new boolean[]{true, false, false, false, false, false, false, false, false};
            espacamento = 100;
            alinhamento = AlinhamentoVertical.CENTRO;
            deslocamentos = new int[]{-30, -30, -30};
            ajustesVerticais = new int[]{0, 30, 0};
            largurasEixos = new int[]{150};
            break;
        case 11:
            tipos = new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.SIMPLES};
            visibilidade = new boolean[]{true, true, false, false, false, false, false, false, false};
            espacamento = 200;
            alinhamento = AlinhamentoVertical.CENTRO;
            deslocamentos = new int[]{-30, -30, -30};
            ajustesVerticais = new int[]{0, 0, 0};
            largurasEixos = new int[]{220, 150};
            break;
        case 12:
            tipos = new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO, TipoEixo.DUPLO};
            visibilidade = new boolean[]{true, true, true, true, true, true, false, false, false};
            espacamento = 100;
            alinhamento = AlinhamentoVertical.TOPO;
            deslocamentos = new int[]{-30, -30, -30, -30, -30, -30};
            ajustesVerticais = new int[]{0, 0, 100};
            largurasEixos = new int[]{220, 220, 220, 220, 220, 220};
            break;
        case 13:
            tipos = new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES};
            visibilidade = new boolean[]{true, true, true, true, true, false, false, false, false};
            espacamento = 100;
            alinhamento = AlinhamentoVertical.CENTRO;
            deslocamentos = new int[]{-30, -30, -30, -30, -30};
            ajustesVerticais = new int[]{0, 0, 0};
            largurasEixos = new int[]{150, 150, 150, 150, 150};
            break;
        case 14:
            tipos = new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.DUPLO, TipoEixo.DUPLO};
            visibilidade = new boolean[]{true, true, true, true, true, false, false, false, false};
            espacamento = 100;
            alinhamento = AlinhamentoVertical.BASE;
            deslocamentos = new int[]{-30, -30, -30, -30, -30};
            ajustesVerticais = new int[]{20, 0, 30};
            largurasEixos = new int[]{150, 150, 150, 240, 240};
            posicoesEixos = new int[]{-80, -80, -80, 30, 30, 0, 0, 0, 0};
            break;
        case 15:
            tipos = new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES, TipoEixo.SIMPLES};
            visibilidade = new boolean[]{true, true, true, true, true, true, false, false, false};
            espacamento = 100;
            alinhamento = AlinhamentoVertical.TOPO;
            deslocamentos = new int[]{-30, -30, -30, -30, -30, -30};
            ajustesVerticais = new int[]{0, 0, 110};
            largurasEixos = new int[]{150, 150, 150, 150, 150, 150};
            break;
        case 16:
             tipos = new TipoEixo[]{TipoEixo.SIMPLES, TipoEixo.DUPLO, TipoEixo.DUPLO};
             visibilidade = new boolean[]{true, false, false, false, true, true, false, false, false};
             espacamento = 150;
             alinhamento = AlinhamentoVertical.BASE;
             deslocamentos = new int[]{-30, -30, -30};
             ajustesVerticais = new int[]{80, 0, 0};
             largurasEixos = new int[]{150, 220, 220};
             posicoesEixos = new int[]{0, 0, 0, 0, 90, 50, 0, 0, 0};
             break;
        case 17:
            tipos = new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO};
            visibilidade = new boolean[]{true, false, false, false, false, true, false, false, false};
            espacamento = 370;
            alinhamento = AlinhamentoVertical.TOPO;
            deslocamentos = new int[]{-30, -30, -30};
            ajustesVerticais = new int[]{30, 0, 100};
            largurasEixos = new int[]{230, 230};
            break;
        case 18: // Dolly
            tipos = new TipoEixo[]{TipoEixo.DUPLO, TipoEixo.DUPLO};
            visibilidade = new boolean[]{true, false, false, false, false, true, false, false, false};
            espacamento = 120;
            alinhamento = AlinhamentoVertical.BASE;
            deslocamentos = new int[]{-30, -30, 0};
            ajustesVerticais = new int[]{30, 0, 0};
            largurasEixos = new int[]{210, 210};
            break;
        default:
            // Se o veículo não tem uma configuração conhecida, limpa o painel e para.
            desenharChassi(null, null, 0, null, null, null, null, null);
            return;
    }

    // Após definir as instruções, manda o desenhista trabalhar com elas
    desenharChassi(tipos, visibilidade, espacamento, alinhamento, deslocamentos, ajustesVerticais, largurasEixos, posicoesEixos);

    // --- ATUALIZA AS OUTRAS TABELAS DA TELA ---
    String medidaNecessaria = veiculoSelecionado.getMEDIDA_PNEU();
    if (medidaNecessaria == null || medidaNecessaria.trim().isEmpty()) {
        popularTabelaPneusEstoque(new java.util.ArrayList<>());
    } else {
        List<Pneu> pneusCompativeis = pneuDAO.listarPneusPorStatusEMedida("ESTOQUE", medidaNecessaria);
        popularTabelaPneusEstoque(pneusCompativeis);
    }
    
    Tabela_ExibicaoPneuUsado.setModel(new DefaultTableModel(
        new Object[][]{},
        new String[]{"N° FOGO", "FABRICANTE", "PROFUNDIDADE"}
    ));


    }//GEN-LAST:event_Tabela_Exibicao_veiculosMouseClicked

    public static void main(String args[]) {
        
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JLabel Label124;
    private javax.swing.JLabel Label125;
    private javax.swing.JLabel Label126;
    private javax.swing.JLabel Label127;
    private javax.swing.JLabel Label128;
    private javax.swing.JLabel Label129;
    private javax.swing.JLabel Label130;
    private javax.swing.JLabel Label131;
    private javax.swing.JLabel Label132;
    private javax.swing.JLabel Label133;
    private javax.swing.JLabel Label134;
    private javax.swing.JLabel Label135;
    private javax.swing.JScrollPane PNEUS_ESTOQUE;
    private javax.swing.JPanel PainelManutencao;
    private javax.swing.JPanel TELA_ZERO;
    private javax.swing.JTable Tabela_ExibicaoPneuUsado;
    private javax.swing.JTable Tabela_Exibicao_pneus_em_estoque;
    private javax.swing.JTable Tabela_Exibicao_veiculos;
    private javax.swing.JLabel Tipo_pneu;
    private javax.swing.JLabel Triangulo1;
    private javax.swing.JLabel Triangulo2;
    private javax.swing.JLabel Triangulo3;
    private javax.swing.JLabel Triangulo4;
    private javax.swing.JLabel conserto;
    private javax.swing.JLabel engate1;
    private javax.swing.JLabel engate2;
    private javax.swing.JLabel engate3;
    private javax.swing.JLabel engate4;
    private javax.swing.JLabel estoque;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbconserto;
    private javax.swing.JLabel lbeixo1;
    private javax.swing.JLabel lbeixo2;
    private javax.swing.JLabel lbeixo3;
    private javax.swing.JLabel lbeixo4;
    private javax.swing.JLabel lbeixo5;
    private javax.swing.JLabel lbeixo6;
    private javax.swing.JLabel lbeixo7;
    private javax.swing.JLabel lbeixo8;
    private javax.swing.JLabel lbeixo9;
    private javax.swing.JLabel lbespinha_dorsal;
    private javax.swing.JLabel lbestoque;
    private javax.swing.JLabel lbsucata;
    private javax.swing.JLabel lbtitulo;
    private javax.swing.JLabel sucata;
    // End of variables declaration//GEN-END:variables

    void setState(int NORMAL) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
