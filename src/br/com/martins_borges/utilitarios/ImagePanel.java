package br.com.martins_borges.utilitarios;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

    private Image imagemDeFundo;

    public ImagePanel() {
        // Construtor padrão
    }

    // Este é o método principal que define a imagem a ser exibida.
    public void setImagem(String caminhoDaImagem) {
        if (caminhoDaImagem != null) {
            ImageIcon icon = new ImageIcon(getClass().getResource(caminhoDaImagem));
            this.imagemDeFundo = icon.getImage();
        } else {
            this.imagemDeFundo = null;
        }
        // Pede para o painel se redesenhar para mostrar a nova imagem.
        this.repaint();
    }

    // Este método é chamado automaticamente pelo Swing quando o painel precisa ser desenhado.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagemDeFundo != null) {
            // Usa Graphics2D para ter acesso a opções de renderização avançadas.
            java.awt.Graphics2D g2d = (java.awt.Graphics2D) g.create();

            // Ativa a interpolação bilinear, que oferece um bom equilíbrio entre qualidade e desempenho.
            // Para a mais alta qualidade (mais lento), use VALUE_INTERPOLATION_BICUBIC.
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                                 java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,
                                 java.awt.RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                                 java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int imgWidth = imagemDeFundo.getWidth(null);
            int imgHeight = imagemDeFundo.getHeight(null);

            // Calcula a proporção para manter o aspect ratio da imagem.
            double scale = Math.min((double) panelWidth / imgWidth, (double) panelHeight / imgHeight);

            int newWidth = (int) (imgWidth * scale);
            int newHeight = (int) (imgHeight * scale);

            // Calcula a posição para centralizar a imagem no painel.
            int x = (panelWidth - newWidth) / 2;
            int y = (panelHeight - newHeight) / 2;

            // Desenha a imagem redimensionada e com alta qualidade.
            g2d.drawImage(imagemDeFundo, x, y, newWidth, newHeight, null);

            // Libera os recursos do Graphics2D.
            g2d.dispose();
        }
    }
}
