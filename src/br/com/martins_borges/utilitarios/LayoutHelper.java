package br.com.martins_borges.utilitarios;


import javax.swing.*;
import java.awt.*;

public class LayoutHelper {

    // Método para configurar o layout GridBagLayout em um painel
    public static void configurarGridBagLayout(JPanel painel) {
        if (!(painel.getLayout() instanceof GridBagLayout)) {
            painel.setLayout(new GridBagLayout());
        }
    }

    // Método para configurar o layout BorderLayout em um painel
    public static void configurarBorderLayout(JPanel painel) {
        if (!(painel.getLayout() instanceof BorderLayout)) {
            painel.setLayout(new BorderLayout());
        }
    }

    // Método para configurar o layout BoxLayout em um painel
    public static void configurarBoxLayout(JPanel painel) {
        if (!(painel.getLayout() instanceof BoxLayout)) {
            painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        }
    }

    // Método para configurar o layout com layout null (usado caso precise de posições manuais)
    public static void configurarNullLayout(JPanel painel) {
        if (!(painel.getLayout() == null)) {
            painel.setLayout(null);
        }
    }

    // Método para configurar o CardLayout em um painel
    public static void configurarCardLayout(JPanel painel) {
        if (!(painel.getLayout() instanceof CardLayout)) {
            painel.setLayout(new CardLayout());
        }
    }
}
