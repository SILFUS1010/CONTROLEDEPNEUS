package br.com.martins_borges.utilitarios;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

public class Utilitarios {

    public static void aplicarFormatacaoCampos(Container container) {
        if (container == null) {
            return;
        }
        for (Component component : container.getComponents()) {
            if (component instanceof JTextComponent) {
                JTextComponent textComponent = (JTextComponent) component;
                configurarParaMaiusculasAoFocarEDigitar(textComponent);
            } else if (component instanceof JComboBox) {
                JComboBox<?> comboBox = (JComboBox<?>) component;
                if (comboBox.isEditable()) {
                    Component editorComponent = comboBox.getEditor().getEditorComponent();
                    if (editorComponent instanceof JTextComponent) {
                        configurarParaMaiusculasAoFocarEDigitar((JTextComponent) editorComponent);
                    }
                }
            } else if (component instanceof Container) {
                aplicarFormatacaoCampos((Container) component);
            }
        }
    }

    private static void configurarParaMaiusculasAoFocarEDigitar(JTextComponent textComponent) {
        if (textComponent == null) {
            return;
        }

        textComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String textoAtual = textComponent.getText();
                if (textoAtual != null && !textoAtual.isEmpty()) {
                    if (!textoAtual.equals(textoAtual.toUpperCase())) {
                        textComponent.setText(textoAtual.toUpperCase());
                    }
                }
            }
        });

        if (textComponent.getDocument() instanceof AbstractDocument) {
            AbstractDocument doc = (AbstractDocument) textComponent.getDocument();
            if (!(doc.getDocumentFilter() instanceof UppercaseDocumentFilter)) {
                doc.setDocumentFilter(new UppercaseDocumentFilter());
            }
        }
    }

    private static class UppercaseDocumentFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) {
                return;
            }
            super.insertString(fb, offset, string.toUpperCase(), attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null) {
                return;
            }
            super.replace(fb, offset, length, text.toUpperCase(), attrs);
        }
    }

    public static String mostrarDialogoComCampoMaiusculo(Component parent, String mensagem, String titulo) {
        // Criar um JTextField personalizado
        JTextField textField = new JTextField(20);

        // Configurar o textField para maiúsculas
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String textoAtual = textField.getText();
                if (textoAtual != null && !textoAtual.isEmpty()) {
                    if (!textoAtual.equals(textoAtual.toUpperCase())) {
                        textField.setText(textoAtual.toUpperCase());
                    }
                }
            }
        });

        // Configurar o DocumentFilter para maiúsculas
        if (textField.getDocument() instanceof AbstractDocument) {
            AbstractDocument doc = (AbstractDocument) textField.getDocument();
            doc.setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                        throws BadLocationException {
                    if (string == null) {
                        return;
                    }
                    super.insertString(fb, offset, string.toUpperCase(), attr);
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                        throws BadLocationException {
                    if (text == null) {
                        return;
                    }
                    super.replace(fb, offset, length, text.toUpperCase(), attrs);
                }
            });
        }

        // Criar o painel do diálogo com layout vertical
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Adicionar o label
        panel.add(new JLabel(mensagem));
        // Adicionar um pequeno espaço entre o label e o campo
        panel.add(Box.createVerticalStrut(5));
        // Adicionar o campo de texto
        panel.add(textField);

        // Mostrar o diálogo personalizado
        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                titulo,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            // Garantir que o texto retornado está em maiúsculas
            return textField.getText().trim().toUpperCase();
        }
        return null;
    }

    public static String removerAcentos(String str) {
        if (str == null) {
            return "";
        }
        String s = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s.toLowerCase();
    }
}
