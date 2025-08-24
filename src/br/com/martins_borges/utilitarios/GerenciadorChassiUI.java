package br.com.martins_borges.utilitarios;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class GerenciadorChassiUI extends JPanel {

    private final List<JLabel> labelsDosPneus = new ArrayList<>();
    private final ImageIcon iconeEspinhaDorsal;
    private final ImageIcon iconeEixo;
    private final ImageIcon iconePneuPlaceholder;

    public GerenciadorChassiUI() {
        this.setLayout(null); // Essencial para posicionar os componentes manualmente
        
        // Carrega os ícones
        iconeEspinhaDorsal = new ImageIcon(getClass().getResource("/br/com/martins_borges/telas/Imagens/espinha-dorsal.png"));
        iconeEixo = new ImageIcon(getClass().getResource("/br/com/martins_borges/telas/Imagens/eixo-horizontal .png"));
        iconePneuPlaceholder = new ImageIcon(getClass().getResource("/br/com/martins_borges/telas/Imagens/pneu.png")); // Provisório
    }

    public void desenharChassi(int numeroDeEixos) {
        this.removeAll();
        labelsDosPneus.clear();

        if (numeroDeEixos <= 0) {
            revalidate();
            repaint();
            return;
        }

        // 1. Adicionar a Espinha Dorsal
        JLabel espinhaDorsal = new JLabel(iconeEspinhaDorsal);
        // Posição e tamanho (ajustar conforme o design final)
        espinhaDorsal.setBounds(150, 10, 20, 500); 
        this.add(espinhaDorsal);

        // 2. Adicionar Eixos e Pneus
        int posicaoYEixo = 30; // Posição Y inicial do primeiro eixo
        int espacamentoEntreEixos = 50; // Espaço vertical entre os eixos

        for (int i = 0; i < numeroDeEixos; i++) {
            // Adiciona o eixo
            JLabel eixo = new JLabel(iconeEixo);
            eixo.setBounds(50, posicaoYEixo, 220, 20); // Posição e tamanho do eixo
            this.add(eixo);

            // Adiciona os pneus para este eixo
            // Pneu Esquerdo
            JLabel pneuEsquerdo = new JLabel("Pneu " + i + "-L", iconePneuPlaceholder, JLabel.CENTER);
            pneuEsquerdo.setBounds(10, posicaoYEixo - 10, 40, 40); // Posição e tamanho do pneu
            pneuEsquerdo.setName("pneu_" + i + "_L"); // Nome para identificar o label
            pneuEsquerdo.setBorder(new LineBorder(Color.GRAY)); // Borda para visualização
            this.add(pneuEsquerdo);
            labelsDosPneus.add(pneuEsquerdo);

            // Pneu Direito
            JLabel pneuDireito = new JLabel("Pneu " + i + "-R", iconePneuPlaceholder, JLabel.CENTER);
            pneuDireito.setBounds(270, posicaoYEixo - 10, 40, 40); // Posição e tamanho do pneu
            pneuDireito.setName("pneu_" + i + "_R");
            pneuDireito.setBorder(new LineBorder(Color.GRAY));
            this.add(pneuDireito);
            labelsDosPneus.add(pneuDireito);
            
            posicaoYEixo += espacamentoEntreEixos;
        }

        revalidate();
        repaint();
    }

    /**
     * Retorna a lista de JLabels que representam os pneus.
     * @return Uma lista de JLabels.
     */
    public List<JLabel> getLabelsDosPneus() {
        return labelsDosPneus;
    }
}
