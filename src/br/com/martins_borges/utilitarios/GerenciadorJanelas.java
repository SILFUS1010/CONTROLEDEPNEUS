package br.com.martins_borges.utilitarios; // Ou o pacote correto

import br.com.martins_borges.telas.TelaCadastroVeiculos;
import br.com.martins_borges.telas.TelaControleDePneus;
import br.com.martins_borges.telas.TelaPrincipal;
import java.awt.Component;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Window;

public class GerenciadorJanelas {

    public static void configurarJanelaFilha(TelaControleDePneus cadastroVeiculos, JFrame janelaFilha) {
        Object janelaPrincipal = null;

        if (janelaFilha == null || janelaPrincipal == null) {
            System.err.println("GerenciadorJanelas: Janela filha ou principal é nula.");
            return;
        }

        // 1. Definir fechamento padrão (liberar recursos ao fechar)
        janelaFilha.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 2. Remover o setFocusableWindowState(false) - GARANTIR QUE NÃO ESTÁ SENDO CHAMADO EM OUTRO LUGAR
        // janelaFilha.setFocusableWindowState(true); // Garante que pode receber foco

        // 3. Adicionar o WindowFocusListener para tentar manter o foco
        janelaFilha.addWindowFocusListener(new WindowAdapter() { // Usando WindowAdapter para simplificar
            @Override
            public void windowLostFocus(WindowEvent e) {
                Window oppositeWindow = e.getOppositeWindow();
                // Se perdeu foco para a janela principal...
                if (oppositeWindow == janelaPrincipal) {
                    // Tenta trazer a filha para frente DEPOIS que a EDT processar a perda de foco
                    SwingUtilities.invokeLater(() -> {
                        
                        janelaFilha.toFront();
                        // Pedir foco pode ser complicado, às vezes toFront é suficiente
                        // janelaFilha.requestFocus();
                        // ou
                        // janelaFilha.requestFocusInWindow();
                    });
                }
                 else {
                     // Debug para ver para onde o foco foi
                     String oppositeName = (oppositeWindow == null) ? "null/outra app" : oppositeWindow.getClass().getSimpleName();
                      
                 }
            }
        });

        // 4. (Opcional) Outras configurações comuns
         janelaFilha.setLocationRelativeTo((Component) janelaPrincipal); // Centralizar
         // janelaFilha.setResizable(false);

         
    }

    

    
}