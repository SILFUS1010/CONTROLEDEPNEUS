package br.com.martins_borges.telas;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.JFrame;
import util.PosicaoFormulario;

    public class TelaPrincipal extends javax.swing.JFrame {

    PosicaoFormulario form;

    public TelaPrincipal() {
        initComponents();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int alturaBarraTarefas = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration()).bottom;
        setBounds(0, 0, screenSize.width, screenSize.height - alturaBarraTarefas);
        setLocation(0, 0);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Desktop = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        Cadastro_Veiculos = new javax.swing.JMenuItem();
        Cadastro_Servico = new javax.swing.JMenuItem();
        Cadastro_Pneus = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        Controle_Pneus = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CONTROLE DE PNEUS");
        setExtendedState(6);
        setPreferredSize(new java.awt.Dimension(1900, 549));

        Desktop.setBackground(new java.awt.Color(13, 37, 67));
        Desktop.setBorder(new javax.swing.border.MatteBorder(null));
        Desktop.setName("Desktop"); // NOI18N
        Desktop.setPreferredSize(new java.awt.Dimension(1000, 800));
        Desktop.setLayout(new java.awt.GridBagLayout());
        getContentPane().add(Desktop, java.awt.BorderLayout.CENTER);

        jMenu1.setText("Cadastros");

        Cadastro_Veiculos.setText("Cadastro de Veiculos");
        Cadastro_Veiculos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cadastro_VeiculosActionPerformed(evt);
            }
        });
        jMenu1.add(Cadastro_Veiculos);

        Cadastro_Servico.setText("Cadastro de Serviços");
        Cadastro_Servico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cadastro_ServicoActionPerformed(evt);
            }
        });
        jMenu1.add(Cadastro_Servico);

        Cadastro_Pneus.setText("Cadastro de Pneus");
        Cadastro_Pneus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cadastro_PneusActionPerformed(evt);
            }
        });
        jMenu1.add(Cadastro_Pneus);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Manutenções");

        jMenuItem3.setText("Ressolagem");
        jMenu2.add(jMenuItem3);

        jMenuItem4.setText("Conserto");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        Controle_Pneus.setText("Controle de Pneus");
        Controle_Pneus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Controle_PneusActionPerformed(evt);
            }
        });
        jMenu2.add(Controle_Pneus);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Relatórios");
        jMenuBar1.add(jMenu3);

        jMenu4.setText("Opções");

        jMenuItem5.setText("Sair");
        jMenu4.add(jMenuItem5);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        setSize(new java.awt.Dimension(1916, 758));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private TelaCadastroPneus cadastroPneus;
    
    private void Cadastro_PneusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cadastro_PneusActionPerformed
       
        if (cadastroPneus == null || !cadastroPneus.isShowing()) {

            cadastroPneus = new TelaCadastroPneus(this, false);
            cadastroPneus.setTitle("CADASTRO DE PNEUS");
            cadastroPneus.pack();
            
            try {

                Point parentLocation = this.getLocationOnScreen();
                int parentY = parentLocation.y;

                Insets frameInsets = this.getInsets();
                int topOffset = frameInsets.top + 60;
                int dialogY = parentY + topOffset;
                int parentWidth = this.getWidth();
                int dialogWidth = cadastroPneus.getWidth();
                int dialogX = parentLocation.x + (parentWidth - dialogWidth) / 2;
                GraphicsConfiguration gc = this.getGraphicsConfiguration();
                Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
                Rectangle screenBounds = gc.getBounds();
                if (dialogX < screenBounds.x + screenInsets.left) {
                    dialogX = screenBounds.x + screenInsets.left;
                }
                if (dialogX + dialogWidth > screenBounds.x + screenBounds.width - screenInsets.right) {
                    dialogX = screenBounds.x + screenBounds.width - screenInsets.right - dialogWidth;
                }
                if (dialogY < screenBounds.y + screenInsets.top) {
                    dialogY = screenBounds.y + screenInsets.top;
                }
                if (dialogY + cadastroPneus.getHeight() > screenBounds.y + screenBounds.height - screenInsets.bottom) {
                    dialogY = screenBounds.y + screenBounds.height - screenInsets.bottom - cadastroPneus.getHeight();
                }
                if (dialogY < 0) {
                    dialogY = 0;
                }
                cadastroPneus.setLocation(dialogX, dialogY);
            } catch (HeadlessException e) {
                System.err.println("Erro ao calcular posição do diálogo. Usando centralização na tela.");
                e.printStackTrace();
                cadastroPneus.setLocationRelativeTo(null);
            }
            cadastroPneus.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    cadastroPneus = null;
                }
            });
            cadastroPneus.setVisible(true);
        } else {
            cadastroPneus.toFront();
            cadastroPneus.requestFocus();
        }

    }//GEN-LAST:event_Cadastro_PneusActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        
    }//GEN-LAST:event_jMenuItem4ActionPerformed
    private TelaCadastroVeiculos cadastroVeiculos; 

    private void Cadastro_VeiculosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cadastro_VeiculosActionPerformed

        if (cadastroVeiculos == null || !cadastroVeiculos.isShowing()) {
            cadastroVeiculos = new TelaCadastroVeiculos(this, false);
            cadastroVeiculos.setTitle("CADASTRO DE VEICULOS");
        cadastroVeiculos.pack();

        cadastroVeiculos.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                cadastroVeiculos = null;
            }
        });

        cadastroVeiculos.setVisible(true); // Torna a janela visível

        // Define a posição após a janela se tornar visível
        javax.swing.SwingUtilities.invokeLater(() -> {
            Point currentLoc = cadastroVeiculos.getLocation();
            int newY = currentLoc.y + 18; // Desloca 30 pixels para baixo
            cadastroVeiculos.setLocation(currentLoc.x, newY);
        });
    } // <--- CHAVE DE FECHAMENTO ADICIONADA AQUI
    else {
        // Se a JDialog já existe, apenas a trazemos para frente e damos foco.
        cadastroVeiculos.toFront();
        cadastroVeiculos.requestFocus();
    }
}//GEN-LAST:event_Cadastro_VeiculosActionPerformed
    
    private TelaControleDePneus controlePneus;

    private void Controle_PneusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Controle_PneusActionPerformed

        if (controlePneus == null || !controlePneus.isVisible()) {
            controlePneus = new TelaControleDePneus(this, false);

            controlePneus.setTitle("CONTROLE DE PNEUS");

            controlePneus.setLocationRelativeTo(this);

            controlePneus.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    controlePneus = null;
                }
            });

            controlePneus.setVisible(true);
        } else {

            controlePneus.setState(JFrame.NORMAL);
            controlePneus.toFront();
            controlePneus.requestFocus();

        }

    }//GEN-LAST:event_Controle_PneusActionPerformed
     
    private TelaCadastroServicos cadastroServicos;
    
    private void Cadastro_ServicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cadastro_ServicoActionPerformed

                                                    

    if (cadastroServicos == null || !cadastroServicos.isShowing()) {
        cadastroServicos = new TelaCadastroServicos(this, false);
        cadastroServicos.setTitle("CADASTRO DE SERVIÇOS");
        cadastroServicos.pack();

        try {
            Point parentLocation = this.getLocationOnScreen();
            int parentY = parentLocation.y;
            Insets frameInsets = this.getInsets();
            // Lembre-se de AJUSTAR o '60' para o valor correto para o seu layout
            int topOffset = frameInsets.top + 60; // <<< PONTO DE AJUSTE
            int dialogY = parentY + topOffset;
            int parentWidth = this.getWidth();
            int dialogWidth = cadastroServicos.getWidth();
            int dialogX = parentLocation.x + (parentWidth - dialogWidth) / 2;

            GraphicsConfiguration gc = this.getGraphicsConfiguration();
            Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
            Rectangle screenBounds = gc.getBounds();

            if (dialogX < screenBounds.x + screenInsets.left) {
                dialogX = screenBounds.x + screenInsets.left;
            }
            if (dialogX + dialogWidth > screenBounds.x + screenBounds.width - screenInsets.right) {
                dialogX = screenBounds.x + screenBounds.width - screenInsets.right - dialogWidth;
            }
            if (dialogY < screenBounds.y + screenInsets.top) {
                dialogY = screenBounds.y + screenInsets.top;
            }
            if (dialogY + cadastroServicos.getHeight() > screenBounds.y + screenBounds.height - screenInsets.bottom) {
                dialogY = screenBounds.y + screenBounds.height - screenInsets.bottom - cadastroServicos.getHeight();
            }
            if (dialogY < 0) {
                dialogY = 0;
            }

            cadastroServicos.setLocation(dialogX, dialogY);
        } catch (HeadlessException e) {
            System.err.println("Erro ao calcular posição do diálogo de serviços. Usando centralização na tela.");
            e.printStackTrace();
            cadastroServicos.setLocationRelativeTo(null);
        }

        cadastroServicos.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                cadastroServicos = null;
            }
        });

        cadastroServicos.setVisible(true);
    } else {
        // ---- ESTA É A PARTE CORRIGIDA ----
        // Se a JDialog já existe, apenas a trazemos para frente e damos foco.
        cadastroServicos.toFront();
        cadastroServicos.requestFocus();
        // ---- FIM DA PARTE CORRIGIDA ----
    }



    

    }//GEN-LAST:event_Cadastro_ServicoActionPerformed

    public static void main(String args[]) {
        try {
        javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, "Erro ao definir Look and Feel Metal.", ex);
    }

    java.awt.EventQueue.invokeLater(() -> {
        new TelaPrincipal().setVisible(true);
    });
}

// <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Cadastro_Pneus;
    private javax.swing.JMenuItem Cadastro_Servico;
    private javax.swing.JMenuItem Cadastro_Veiculos;
    private javax.swing.JMenuItem Controle_Pneus;
    private javax.swing.JDesktopPane Desktop;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 
}
