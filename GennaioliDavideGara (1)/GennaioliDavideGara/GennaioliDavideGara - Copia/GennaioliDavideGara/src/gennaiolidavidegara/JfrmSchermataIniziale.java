package gennaiolidavidegara;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class JfrmSchermataIniziale extends javax.swing.JFrame {

    // I 10 circuiti disponibili 
    private static final String[] CIRCUITI = {
        "Monza - Italian GP",
        "Silverstone - British GP",
        "Monaco - Monaco GP",
        "Spa-Francorchamps - Belgian GP",
        "Suzuka - Japanese GP",
        "Interlagos - Brazilian GP",
        "Melbourne - Australian GP",
        "Bahrain International Circuit",
        "Hungaroring - Hungarian GP",
        "Zandvoort - Dutch GP"
    };

    // I 20 piloti F1 
    private static final String[][] PILOTI_F1 = {
        {"Max Verstappen", "Red Bull Racing"},
        {"Lando Norris", "McLaren"},
        {"Charles Leclerc", "Ferrari"},
        {"Carlos Sainz", "Williams"},
        {"Lewis Hamilton", "Ferrari"},
        {"George Russell", "Mercedes"},
        {"Fernando Alonso", "Aston Martin"},
        {"Sergio Perez", "Red Bull Racing"},
        {"Oscar Piastri", "McLaren"},
        {"Kimi Antonelli", "Mercedes"},
        {"Lance Stroll", "Aston Martin"},
        {"Pierre Gasly", "Alpine"},
        {"Esteban Ocon", "Haas"},
        {"Yuki Tsunoda", "RB"},
        {"Nico Hulkenberg", "Sauber"},
        {"Alexander Albon", "Williams"},
        {"Franco Colapinto", "Alpine"},
        {"Oliver Bearman", "Haas"},
        {"Isack Hadjar", "RB"},
        {"Valtteri Bottas", "Sauber"}
    };

    // Componenti grafici
    private javax.swing.JLabel lblTitolo;
    private javax.swing.JLabel lblCircuito;
    private javax.swing.JComboBox<String> cmbCircuiti;
    private javax.swing.JLabel lblGiri;
    private javax.swing.JSlider sliderGiri;
    private javax.swing.JLabel lblGiriValore;  // numero accanto allo slider
    private javax.swing.JLabel lblMeteo;
    private javax.swing.JLabel lblPiloti;
    private javax.swing.JLabel[] lblPos = new javax.swing.JLabel[6];
    private javax.swing.JComboBox<String>[] comboPiloti = new javax.swing.JComboBox[6];
    private javax.swing.JComboBox<String>[] comboGomme = new javax.swing.JComboBox[6];
    private javax.swing.JButton btnGara;
    private meteo meteoCorrente;

    public JfrmSchermataIniziale() {
        initComponents();
    }

    /**
     * Crea e posiziona tutti i componenti della finestra.
     */
    private void initComponents() {
        setTitle("F1 Race Simulator");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // Stringhe "Nome - Scuderia" per il combobox
        String[] nomiPiloti = new String[PILOTI_F1.length];
        for (int i = 0; i < PILOTI_F1.length; i++) {
            nomiPiloti[i] = PILOTI_F1[i][0] + " - " + PILOTI_F1[i][1];
        }

        String[] nomiGomme = {"Morbide", "Dure", "Intermedie"};
        int[] defPilota = {0, 1, 2, 4, 6, 9}; // piloti preselezionati di default

        lblTitolo = new javax.swing.JLabel("Selezione");
        lblCircuito = new javax.swing.JLabel("Circuito");
        cmbCircuiti = new javax.swing.JComboBox<>(CIRCUITI);
        lblGiri = new javax.swing.JLabel("N° Giri");
        sliderGiri = new javax.swing.JSlider(5, 80, 30);
        sliderGiri.setMajorTickSpacing(25);
        sliderGiri.setPaintTicks(true);
        sliderGiri.setPaintLabels(true);
        lblGiriValore = new javax.swing.JLabel("30");
        lblMeteo = new javax.swing.JLabel("Meteo: ...");
        lblPiloti = new javax.swing.JLabel("PILOTI");
        btnGara = new javax.swing.JButton("GARA");

        // Quando sposti lo slider aggiorna il numero accanto
        sliderGiri.addChangeListener(e
                -> lblGiriValore.setText(String.valueOf(sliderGiri.getValue())));

        // Crea le 6 righe: etichetta P1..P6 + combobox pilota + combobox gomme
        for (int i = 0; i < 6; i++) {
            lblPos[i] = new javax.swing.JLabel("P" + (i + 1));
            comboPiloti[i] = new javax.swing.JComboBox<>(nomiPiloti);
            comboPiloti[i].setSelectedIndex(defPilota[i]);
            comboGomme[i] = new javax.swing.JComboBox<>(nomiGomme);
        }
        
        generaMeteo();
        btnGara.addActionListener(e -> avviaGara());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.CENTER,
                        layout.createSequentialGroup().addComponent(lblTitolo))
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(lblCircuito)
                                .addComponent(cmbCircuiti, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20)
                        .addGroup(layout.createParallelGroup()
                                .addComponent(lblGiri)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(sliderGiri, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5)
                                        .addComponent(lblGiriValore, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(20)
                        .addComponent(lblMeteo))
                .addGroup(javax.swing.GroupLayout.Alignment.CENTER,
                        layout.createSequentialGroup().addComponent(lblPiloti))
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(lblPos[0]).addComponent(lblPos[1]).addComponent(lblPos[2])
                                .addComponent(lblPos[3]).addComponent(lblPos[4]).addComponent(lblPos[5]))
                        .addGap(6)
                        .addGroup(layout.createParallelGroup()
                                .addComponent(comboPiloti[0]).addComponent(comboPiloti[1]).addComponent(comboPiloti[2])
                                .addComponent(comboPiloti[3]).addComponent(comboPiloti[4]).addComponent(comboPiloti[5]))
                        .addGap(6)
                        .addGroup(layout.createParallelGroup()
                                .addComponent(comboGomme[0], javax.swing.GroupLayout.PREFERRED_SIZE, 100,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(comboGomme[1], javax.swing.GroupLayout.PREFERRED_SIZE, 100,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(comboGomme[2], javax.swing.GroupLayout.PREFERRED_SIZE, 100,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(comboGomme[3], javax.swing.GroupLayout.PREFERRED_SIZE, 100,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(comboGomme[4], javax.swing.GroupLayout.PREFERRED_SIZE, 100,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(comboGomme[5], javax.swing.GroupLayout.PREFERRED_SIZE, 100,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(javax.swing.GroupLayout.Alignment.CENTER,
                        layout.createSequentialGroup().addComponent(btnGara))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(lblTitolo)
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblCircuito)
                                .addComponent(cmbCircuiti, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblGiri)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(sliderGiri, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblGiriValore)))
                        .addComponent(lblMeteo))
                .addGap(8)
                .addComponent(lblPiloti)
                .addGap(4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPos[0])
                        .addComponent(comboPiloti[0], javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboGomme[0], javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPos[1])
                        .addComponent(comboPiloti[1], javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboGomme[1], javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPos[2])
                        .addComponent(comboPiloti[2], javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboGomme[2], javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPos[3])
                        .addComponent(comboPiloti[3], javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboGomme[3], javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPos[4])
                        .addComponent(comboPiloti[4], javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboGomme[4], javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPos[5])
                        .addComponent(comboPiloti[5], javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboGomme[5], javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addComponent(btnGara)
                .addGap(10)
        );

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Genera meteo a caso e preseleziona le gomme consigliate. SOLE → Morbide,
     * NUVOLOSO → Dure, PIOGGIA → Intermedie.
     */
    private void generaMeteo() {
        meteo[] valori = meteo.values();
        meteoCorrente = valori[new Random().nextInt(valori.length)];
        String testo;
        int gommaIdx;
        switch (meteoCorrente) {
            case SOLE:
                testo = "☀️ Sole";
                gommaIdx = 0;
                break;
            case NUVOLOSO:
                testo = "☁️ Nuvoloso";
                gommaIdx = 1;
                break;
            case PIOGGIA:
                testo = "🌧 Pioggia";
                gommaIdx = 2;
                break;
            default:
                testo = "";
                gommaIdx = 0;
        }
        lblMeteo.setText("Meteo: " + testo);
        for (int i = 0; i < 6; i++) {
            if (comboGomme[i] != null) {
                comboGomme[i].setSelectedIndex(gommaIdx);
            }
        }
    }

    /**
     * Controlla duplicati, crea la gara e apre JfrmGara. Chiamato dal pulsante
     * GARA.
     */
    private void avviaGara() {
        Set<Integer> sel = new HashSet<>();
        for (int i = 0; i < 6; i++) {
            if (!sel.add(comboPiloti[i].getSelectedIndex())) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Hai selezionato lo stesso pilota piu' volte!\nScegli 6 piloti diversi.",
                        "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        String circuito = (String) cmbCircuiti.getSelectedItem();
        int giri = sliderGiri.getValue();
        int delayBase = Math.max(200, 2500 - giri * 25);
        Gara gara = new Gara(circuito, giri, meteoCorrente);
        for (int i = 0; i < 6; i++) {
            int idx = comboPiloti[i].getSelectedIndex();
            SceltaGomme gomme = parseGomme(comboGomme[i].getSelectedIndex());
            gara.aggiungiPilota(new Pilota(
                    PILOTI_F1[idx][0], PILOTI_F1[idx][1], gomme, giri, delayBase, i));
        }
        JfrmGara frmGara = new JfrmGara(gara);
        frmGara.setVisible(true);
        this.dispose();
    }

    private SceltaGomme parseGomme(int idx) {
        switch (idx) {
            case 1:
                return SceltaGomme.DURE;
            case 2:
                return SceltaGomme.INTERMEDIE;
            default:
                return SceltaGomme.MORBIDE;
        }
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JfrmSchermataIniziale.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new JfrmSchermataIniziale().setVisible(true));
    }
}
