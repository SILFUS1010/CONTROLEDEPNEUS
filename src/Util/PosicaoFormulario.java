package util;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class PosicaoFormulario {

    public void abrirFormulario(JInternalFrame telaCadastro, JDesktopPane desktop) {

        if (telaCadastro.getWidth() == 0 || telaCadastro.getHeight() == 0) {
            telaCadastro.setSize(400, 300);
        }

        int lDesk = desktop.getWidth();
        int aDesk = desktop.getHeight();
        int lIFrame = telaCadastro.getWidth();
        int aIFrame = telaCadastro.getHeight();

        telaCadastro.setLocation((lDesk / 2) - (lIFrame / 2), (aDesk / 2) - (aIFrame / 2));

        desktop.add(telaCadastro);

        telaCadastro.setVisible(true);
    }

}
