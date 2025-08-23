package br.com.martins_borges.utilitarios;

import javax.swing.*;
import java.awt.*;

public class DesenhoChassiUtil {

    public static void desenharChassi(JPanel panel, String tipoVeiculo) {
        // Lógica de desenho do chassi será implementada aqui
        // Por enquanto, vamos apenas desenhar um retângulo simples para teste
        Graphics g = panel.getGraphics();
        g.setColor(Color.BLACK);
        g.drawRect(10, 10, 200, 100); // Exemplo de retângulo
    }
}
