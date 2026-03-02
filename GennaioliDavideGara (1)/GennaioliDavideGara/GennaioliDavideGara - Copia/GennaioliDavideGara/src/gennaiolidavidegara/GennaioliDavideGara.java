package gennaiolidavidegara;

public class GennaioliDavideGara {

    public static void main(String[] args) {

        // Prova ad applicare il tema Nimbus, già incluso in Java
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break; // trovato → esci dal ciclo
                }
            }
        } catch (Exception ignored) {
            // Se Nimbus non c'è il programma parte lo stesso col tema di default
        }

        // Apre la schermata iniziale sul thread grafico
        java.awt.EventQueue.invokeLater(() -> new JfrmSchermataIniziale().setVisible(true));
    }
}
