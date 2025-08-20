
package br.com.Martins_Borges.telas;


public class Testes extends javax.swing.JPanel {

   
    public Testes() {
        initComponents();
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jEscolha = new javax.swing.JPanel();
        MARTINS = new javax.swing.JCheckBox();
        ALB = new javax.swing.JCheckBox();
        Engeudi = new javax.swing.JCheckBox();
        lbFogo = new javax.swing.JLabel();
        TxtNfogo = new javax.swing.JTextField();

        MARTINS.setText("MARTINS & BORGES");
        MARTINS.setMaximumSize(new java.awt.Dimension(71, 20));
        MARTINS.setMinimumSize(new java.awt.Dimension(71, 20));
        MARTINS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MARTINSActionPerformed(evt);
            }
        });

        ALB.setText("ALB");

        Engeudi.setText("ENGEUDI");

        lbFogo.setText("N° Fogo: ");

        javax.swing.GroupLayout jEscolhaLayout = new javax.swing.GroupLayout(jEscolha);
        jEscolha.setLayout(jEscolhaLayout);
        jEscolhaLayout.setHorizontalGroup(
            jEscolhaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jEscolhaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jEscolhaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbFogo)
                    .addComponent(TxtNfogo, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Engeudi)))
            .addGroup(jEscolhaLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(ALB))
            .addGroup(jEscolhaLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(MARTINS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jEscolhaLayout.setVerticalGroup(
            jEscolhaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jEscolhaLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(MARTINS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ALB)
                .addGap(9, 9, 9)
                .addComponent(Engeudi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbFogo)
                .addGap(4, 4, 4)
                .addComponent(TxtNfogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(1112, Short.MAX_VALUE)
                .addComponent(jEscolha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(257, 257, 257))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jEscolha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(406, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void MARTINSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MARTINSActionPerformed

    }//GEN-LAST:event_MARTINSActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox ALB;
    private javax.swing.JCheckBox Engeudi;
    private javax.swing.JCheckBox MARTINS;
    private javax.swing.JTextField TxtNfogo;
    private javax.swing.JPanel jEscolha;
    private javax.swing.JLabel lbFogo;
    // End of variables declaration//GEN-END:variables
}
