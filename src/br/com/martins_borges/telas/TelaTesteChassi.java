package br.com.martins_borges.telas;

import br.com.martins_borges.utilitarios.DesenhoChassiUtil;

import javax.swing.*;
import java.awt.*;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class TelaTesteChassi extends JFrame {

    private JPanel painelDesenho;

    public TelaTesteChassi() {
        setTitle("Teste de Desenho de Chassi");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Adiciona o listener para a tecla ESC
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        getRootPane().getActionMap().put("escape", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a janela
            }
        });

        painelDesenho = new JPanel();
        add(painelDesenho);

        // Adiciona um botão para acionar o desenho
        JButton btnDesenhar = new JButton("Desenhar Chassi 'Caminhão Truck'");
        btnDesenhar.addActionListener(e -> {
            // Limpa o painel antes de desenhar
            Graphics g = painelDesenho.getGraphics();
            g.clearRect(0, 0, painelDesenho.getWidth(), painelDesenho.getHeight());
            DesenhoChassiUtil.desenharChassi(painelDesenho, "Caminhão Truck");
        });

        // Adiciona um segundo botão para outro tipo de chassi
        JButton btnDesenhar2 = new JButton("Desenhar Chassi 'Carreta LS'");
        btnDesenhar2.addActionListener(e -> {
            // Limpa o painel antes de desenhar
            Graphics g = painelDesenho.getGraphics();
            g.clearRect(0, 0, painelDesenho.getWidth(), painelDesenho.getHeight());
            DesenhoChassiUtil.desenharChassi(painelDesenho, "Carreta LS");
        });

        // Painel para os botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnDesenhar);
        painelBotoes.add(btnDesenhar2);

        // Adiciona os paineis ao frame
        add(painelBotoes, BorderLayout.SOUTH);
        add(painelDesenho, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaTesteChassi().setVisible(true);
        });
    }
}
