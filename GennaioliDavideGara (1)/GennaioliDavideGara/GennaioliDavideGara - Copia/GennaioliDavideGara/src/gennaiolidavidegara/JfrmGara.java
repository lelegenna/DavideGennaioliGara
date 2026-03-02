package gennaiolidavidegara;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JfrmGara extends javax.swing.JFrame implements GaraListener {

    private Gara gara;
    private List<Pilota> piloti;

    // Semaforo: 5 cerchi colorati
    private javax.swing.JLabel[] luci = new javax.swing.JLabel[5];
    private javax.swing.JPanel panelSemaforo;

    // Per ogni pilota: etichetta fissa a sinistra, barra al centro, info a destra
    private javax.swing.JLabel[] lblPos = new javax.swing.JLabel[6]; // "P1  Nome  🔴  [MORBIDE]"
    private javax.swing.JProgressBar[] bars = new javax.swing.JProgressBar[6]; // macchina
    private javax.swing.JLabel[] lblGiri = new javax.swing.JLabel[6]; // "14/30 | IN GARA"

    // Area eventi
    private javax.swing.JLabel lblEventi;
    private javax.swing.JTextArea atxEvento;
    private javax.swing.JScrollPane scrollEventi;
    private javax.swing.JButton btnAvvia;

    // Lista nell'ordine REALE di arrivo (chi taglia prima è classificato primo)
    private List<Pilota> ordineArrivo = new ArrayList<>();

    // Evita che l'effetto Kimi scatti due volte
    private AtomicBoolean kimiRitirato = new AtomicBoolean(false);

    // Diventa true quando la gara finisce
    private boolean garaTerminata = false;

    // Canzone di Verstappen: tenuta come campo per poterla fermare con OK
    private javax.sound.sampled.Clip clipAudio = null;

    //  COLORI UFFICIALI delle scuderie per la sagoma sulla progress bar
    private static Color getColorScuderia(String scuderia) {
        switch (scuderia) {
            case "Red Bull Racing":
                return new Color(30, 65, 255);
            case "McLaren":
                return new Color(255, 135, 0);
            case "Ferrari":
                return new Color(220, 0, 0);
            case "Mercedes":
                return new Color(0, 210, 190);
            case "Aston Martin":
                return new Color(0, 111, 98);
            case "Williams":
                return new Color(0, 90, 255);
            case "Alpine":
                return new Color(0, 144, 255);
            case "Haas":
                return new Color(160, 160, 160);
            case "RB":
                return new Color(100, 0, 200);
            case "Sauber":
                return new Color(180, 0, 40);
            default:
                return Color.WHITE;
        }
    }

    // Emoji per le etichette testo
    private static String getMacchina(String scuderia) {
        switch (scuderia) {
            case "Red Bull Racing":
                return "🔵";
            case "McLaren":
                return "🟠";
            case "Ferrari":
                return "🔴";
            case "Mercedes":
                return "⚫";
            case "Aston Martin":
                return "🟢";
            case "Williams":
                return "🔷";
            case "Alpine":
                return "🟦";
            case "Haas":
                return "⬜";
            case "RB":
                return "🟣";
            case "Sauber":
                return "🟡";
            default:
                return "🏎";
        }
    }

    //  PROGRESS BAR con solo la sagoma F1
    private static class F1ProgressBar extends javax.swing.JProgressBar {

        private Color coloreMacchina;

        public F1ProgressBar(int min, int max, Color colore) {
            super(min, max);
            this.coloreMacchina = colore;
            setStringPainted(false);
            setOpaque(false); // dice a Swing di non dipingere lo sfondo da solo
        }

        @Override
        protected void paintComponent(Graphics g) {

            // Disegniamo tutto noi: solo sfondo grigio scuro + macchina colorata
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Sfondo neutro scuro (al posto della barra verde/blu di Nimbus)
            g2.setColor(new Color(45, 45, 45));
            g2.fillRoundRect(0, 0, w, h, 6, 6);

            // Bordo sottile grigio
            g2.setColor(new Color(80, 80, 80));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 6, 6);

            // Posizione della macchina: 0% = sinistra, 100% = destra
            double pct = (getMaximum() > 0) ? (double) getValue() / getMaximum() : 0;
            int cx = (int) Math.min(Math.max(pct * (w - 30) + 15, 15), w - 15);
            int cy = h / 2;

            // Disegna SOLO la macchina colorata, niente altro
            disegnaMacchina(g2, cx, cy, coloreMacchina);

            g2.dispose();
        }

        /**
         * Disegna la monoposto F1 vista dall'alto centrata su (cx, cy). Parti:
         * scocca, muso, alettoni anteriori/posteriori, cockpit, 4 ruote.
         */
        private void disegnaMacchina(Graphics2D g2, int cx, int cy, Color col) {
            AffineTransform at = AffineTransform.getTranslateInstance(cx, cy);
            at.scale(0.55, 0.55);
            g2.transform(at);

            // Scocca principale (ovale allungato)
            g2.setColor(col);
            g2.fill(new RoundRectangle2D.Double(-22, -7, 44, 14, 8, 8));

            // Muso anteriore
            int[] musoX = {12, 22, 20, 10};
            int[] musoY = {-5, -3, 3, 5};
            g2.fillPolygon(musoX, musoY, 4);

            // Alettoni (più scuri per contrasto)
            g2.setColor(col.darker());
            g2.fill(new Rectangle2D.Double(16, -11, 8, 4)); // ant. sx
            g2.fill(new Rectangle2D.Double(16, 7, 8, 4)); // ant. dx
            g2.fill(new Rectangle2D.Double(-26, -11, 8, 4)); // post. sx
            g2.fill(new Rectangle2D.Double(-26, 7, 8, 4)); // post. dx

            // Cockpit (cabina pilota)
            g2.setColor(new Color(20, 20, 20, 200));
            g2.fill(new Ellipse2D.Double(-4, -4, 12, 8));

            // 4 ruote
            g2.setColor(new Color(30, 30, 30));
            g2.fill(new RoundRectangle2D.Double(10, -12, 8, 5, 2, 2)); // ant. sx
            g2.fill(new RoundRectangle2D.Double(10, 7, 8, 5, 2, 2)); // ant. dx
            g2.fill(new RoundRectangle2D.Double(-18, -12, 8, 5, 2, 2)); // post. sx
            g2.fill(new RoundRectangle2D.Double(-18, 7, 8, 5, 2, 2)); // post. dx

            // Contorno scocca
            g2.setColor(new Color(0, 0, 0, 80));
            g2.setStroke(new BasicStroke(0.8f));
            g2.draw(new RoundRectangle2D.Double(-22, -7, 44, 14, 8, 8));

            try {
                g2.setTransform(at.createInverse());
            } catch (Exception ignored) {
            }
        }
    }

    //  COSTRUTTORE
    public JfrmGara(Gara gara) {
        this.gara = gara;
        this.piloti = gara.getPiloti();
        gara.setListener(this);
        initComponents();
    }

    //  COSTRUZIONE GRAFICA
    private void initComponents() {
        setTitle("F1 Gara - " + gara.getNomeCircuito());
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // Semaforo
        panelSemaforo = new javax.swing.JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        for (int i = 0; i < 5; i++) {
            luci[i] = new javax.swing.JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color col = (Color) getClientProperty("colore");
                    if (col == null) {
                        col = new Color(60, 20, 20);
                    }
                    g2.setColor(col);
                    g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
                    g2.setColor(new Color(40, 40, 40));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
                }
            };
            luci[i].setPreferredSize(new Dimension(44, 44));
            luci[i].putClientProperty("colore", new Color(60, 20, 20));
            panelSemaforo.add(luci[i]);
        }
        panelSemaforo.setBackground(new Color(30, 30, 30));
        panelSemaforo.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(60, 60, 60), 2));

        javax.swing.JLabel lblGara = new javax.swing.JLabel(
                gara.getNomeCircuito() + "  |  " + gara.getGiri() + " giri",
                javax.swing.JLabel.CENTER);

        btnAvvia = new javax.swing.JButton("▶  AVVIA GARA");
        btnAvvia.setFont(btnAvvia.getFont().deriveFont(Font.BOLD, 14f));
        btnAvvia.addActionListener(e -> avviaConSemaforo());

        lblEventi = new javax.swing.JLabel("📋 Eventi");
        atxEvento = new javax.swing.JTextArea(10, 22);
        atxEvento.setEditable(false);
        atxEvento.setLineWrap(true);
        atxEvento.setWrapStyleWord(true);
        scrollEventi = new javax.swing.JScrollPane(atxEvento);

        // Crea le 3 colonne per ogni pilota
        for (int i = 0; i < 6; i++) {
            Pilota p = piloti.get(i);
            Color col = getColorScuderia(p.getScuderia());
            String car = getMacchina(p.getScuderia());

            // Colonna sinistra: nome fisso
            lblPos[i] = new javax.swing.JLabel(
                    "P" + (i + 1) + "  " + p.getNome() + "  " + car + "  [" + p.getGomme() + "]");

            // Colonna centrale: progress bar con solo la macchina
            bars[i] = new F1ProgressBar(0, gara.getGiri(), col);
            bars[i].setPreferredSize(new Dimension(300, 28));

            // Colonna destra: giri fatti + stato (aggiornata ogni 200ms)
            lblGiri[i] = new javax.swing.JLabel("0/" + gara.getGiri() + " giri  |  IN GARA");
            lblGiri[i].setPreferredSize(new Dimension(155, 20));
            lblGiri[i].setFont(new Font("Dialog", Font.PLAIN, 11));
        }

        // ---- Layout: col. sinistra = gara, col. destra = eventi ----
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // ORIZZONTALE
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(panelSemaforo)
                        .addComponent(lblGara)
                        // Ogni riga: [lblPos] [bar] [lblGiri]
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPos[0], javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(bars[0], javax.swing.GroupLayout.PREFERRED_SIZE, 300,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblGiri[0], javax.swing.GroupLayout.PREFERRED_SIZE, 155,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPos[1], javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(bars[1], javax.swing.GroupLayout.PREFERRED_SIZE, 300,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblGiri[1], javax.swing.GroupLayout.PREFERRED_SIZE, 155,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPos[2], javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(bars[2], javax.swing.GroupLayout.PREFERRED_SIZE, 300,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblGiri[2], javax.swing.GroupLayout.PREFERRED_SIZE, 155,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPos[3], javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(bars[3], javax.swing.GroupLayout.PREFERRED_SIZE, 300,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblGiri[3], javax.swing.GroupLayout.PREFERRED_SIZE, 155,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPos[4], javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(bars[4], javax.swing.GroupLayout.PREFERRED_SIZE, 300,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblGiri[4], javax.swing.GroupLayout.PREFERRED_SIZE, 155,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPos[5], javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(bars[5], javax.swing.GroupLayout.PREFERRED_SIZE, 300,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblGiri[5], javax.swing.GroupLayout.PREFERRED_SIZE, 155,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnAvvia))
                .addGap(12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblEventi)
                        .addComponent(scrollEventi, javax.swing.GroupLayout.PREFERRED_SIZE, 190,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        // VERTICALE
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(panelSemaforo,
                                javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4)
                        .addComponent(lblGara)
                        .addGap(8)
                        // Riga 1
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(lblPos[0])
                                .addComponent(bars[0], javax.swing.GroupLayout.PREFERRED_SIZE, 28,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblGiri[0]))
                        // Riga 2
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(lblPos[1])
                                .addComponent(bars[1], javax.swing.GroupLayout.PREFERRED_SIZE, 28,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblGiri[1]))
                        // Riga 3
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(lblPos[2])
                                .addComponent(bars[2], javax.swing.GroupLayout.PREFERRED_SIZE, 28,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblGiri[2]))
                        // Riga 4
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(lblPos[3])
                                .addComponent(bars[3], javax.swing.GroupLayout.PREFERRED_SIZE, 28,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblGiri[3]))
                        // Riga 5
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(lblPos[4])
                                .addComponent(bars[4], javax.swing.GroupLayout.PREFERRED_SIZE, 28,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblGiri[4]))
                        // Riga 6
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(lblPos[5])
                                .addComponent(bars[5], javax.swing.GroupLayout.PREFERRED_SIZE, 28,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblGiri[5]))
                        .addGap(8)
                        .addComponent(btnAvvia))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(lblEventi)
                        .addComponent(scrollEventi))
        );

        pack();
        setLocationRelativeTo(null);
    }

    //  SEMAFORO
    private void avviaConSemaforo() {
        btnAvvia.setEnabled(false);
        aggiungiEvento("🚦 Preparazione semaforo...");

        new Thread(() -> {
            try {
                // Accendi le 5 luci rosse una alla volta
                for (int i = 0; i < 5; i++) {
                    final int idx = i;
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        luci[idx].putClientProperty("colore", new Color(200, 0, 0));
                        luci[idx].repaint();
                    });
                    Thread.sleep(700);
                }
                Thread.sleep(1000 + (long) (Math.random() * 500)); // pausa casuale come in F1
                javax.swing.SwingUtilities.invokeLater(() -> {
                    for (javax.swing.JLabel l : luci) {
                        l.putClientProperty("colore", new Color(0, 180, 0));
                        l.repaint();
                    }
                    aggiungiEvento("🚦 PARTENZA! VIA!");
                });
                Thread.sleep(600);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    for (javax.swing.JLabel l : luci) {
                        l.putClientProperty("colore", new Color(60, 20, 20));
                        l.repaint();
                    }
                });
                gara.avviaGara();
                avviaMonitoraggio();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    //  MONITORAGGIO: aggiorna progress bar e label giri ogni 200ms
    private void avviaMonitoraggio() {
        new Thread(() -> {
            boolean tuttiFiniti;
            do {
                tuttiFiniti = true;
                for (int i = 0; i < piloti.size(); i++) {
                    Pilota p = piloti.get(i);
                    int giri = p.getGiriFatti();
                    Statoauto st = p.getStato();
                    final int idx = i;

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        // Aggiorna la progress bar (muove la macchina)
                        bars[idx].setValue(Math.min(giri, gara.getGiri()));
                        bars[idx].repaint();

                        // ---- AGGIORNA LA LABEL GIRI a destra della barra ----
                        // Formato: "14/30 giri  |  🔧 BOX"
                        // Cambia in base allo stato corrente del pilota
                        String statoTesto;
                        switch (st) {
                            case BOX:
                                statoTesto = "🔧 BOX";
                                break;
                            case Ritirato:
                                statoTesto = "❌ RITIRATO";
                                break;
                            default:
                                // IN_GARA: se ha finito tutti i giri mostra il traguardo
                                statoTesto = (giri >= gara.getGiri()) ? "🏁 ARRIVATO" : "IN GARA";
                        }
                        lblGiri[idx].setText(giri + "/" + gara.getGiri() + " giri  |  " + statoTesto);

                        // Aggiorna anche l'etichetta nome con lo stato visivo
                        String car = getMacchina(piloti.get(idx).getScuderia());
                        String statoLabel = "";
                        if (st == Statoauto.BOX) {
                            statoLabel = " [🔧]";
                        }
                        if (st == Statoauto.Ritirato) {
                            statoLabel = " [❌]";
                        }
                        lblPos[idx].setText("P" + (idx + 1) + "  "
                                + piloti.get(idx).getNome() + "  "
                                + car + "  [" + piloti.get(idx).getGomme() + "]"
                                + statoLabel);
                    });

                    if (st != Statoauto.Ritirato && giri < gara.getGiri()) {
                        tuttiFiniti = false;
                    }
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    break;
                }
            } while (!tuttiFiniti && !garaTerminata);

            try {
                for (Pilota p : piloti) {
                    p.aspettaThread();
                }
            } catch (InterruptedException ignored) {
            }

            javax.swing.SwingUtilities.invokeLater(this::mostraVincitore);
        }).start();
    }

    //  FINE GARA
    private void mostraVincitore() {
        if (garaTerminata) {
            return;
        }
        garaTerminata = true;

        // Chi ha tagliato il traguardo (ordine reale) + ritirati (per giri fatti)
        List<Pilota> classifica = new ArrayList<>(ordineArrivo);
        List<Pilota> ritirati = new ArrayList<>();
        for (Pilota p : piloti) {
            if (p.getStato() == Statoauto.Ritirato && !classifica.contains(p)) {
                ritirati.add(p);
            }
        }
        ritirati.sort((a, b) -> b.getGiriFatti() - a.getGiriFatti());
        classifica.addAll(ritirati);

        Pilota vincitore = classifica.isEmpty() ? piloti.get(0) : classifica.get(0);
        aggiungiEvento("🏁 --- FINE GARA ---");
        aggiungiEvento("🏆 Vincitore: " + vincitore.getNome());

        StringBuilder sb = new StringBuilder("🏁  CLASSIFICA FINALE\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        for (int i = 0; i < classifica.size(); i++) {
            Pilota p = classifica.get(i);
            String rit = (p.getStato() == Statoauto.Ritirato) ? " ❌ RIT" : "";
            String medaglia = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "   ";
            sb.append(String.format("%s P%d  %-22s  %s%s\n",
                    medaglia, i + 1, p.getNome(), p.getScuderia(), rit));
        }
        sb.append("\n🏆 VINCITORE: ").append(vincitore.getNome())
                .append("\n   ").append(vincitore.getScuderia())
                .append("\n⏱  Miglior giro: ")
                .append(String.format("%.2f", vincitore.getMiglioreGiro())).append("s");

        // Verstappen vince: avvia la canzone PRIMA del popup
        if (vincitore.getNome().equals("Max Verstappen")) {
            sb.append("\n\n🎵  PUMP IT UP! 🎵");

            new Thread(() -> {
                try {
                    java.io.File wavFile = new java.io.File("pump_it_up.wav");
                    if (!wavFile.exists()) {
                        java.net.URL url = getClass().getResource("/pump_it_up.wav");
                        if (url != null) {
                            wavFile = new java.io.File(url.toURI());
                        }
                    }
                    if (!wavFile.exists()) {
                        System.err.println("pump_it_up.wav non trovato! Mettilo nella cartella del .jar");
                        return;
                    }
                    javax.sound.sampled.AudioInputStream audio
                            = javax.sound.sampled.AudioSystem.getAudioInputStream(wavFile);
                    clipAudio = javax.sound.sampled.AudioSystem.getClip();
                    clipAudio.open(audio);
                    clipAudio.start(); // canzone parte prima del popup
                } catch (Exception ex) {
                    System.err.println("Errore audio: " + ex.getMessage());
                }
            }).start();

            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }
        }

        // Mostra il popup → si BLOCCA finché l'utente non preme OK
        String titolo = vincitore.getNome().equals("Max Verstappen")
                ? "🏆 Fine Gara! - MAX WINS! 🎵" : "🏁 Fine Gara!";
        javax.swing.JOptionPane.showMessageDialog(this, sb.toString(),
                titolo, javax.swing.JOptionPane.INFORMATION_MESSAGE);

        // Codice eseguito DOPO che l'utente ha premuto OK → ferma la musica
        if (clipAudio != null && clipAudio.isRunning()) {
            clipAudio.stop();
            clipAudio.close();
        }
    }

    private void aggiungiEvento(String msg) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            atxEvento.append(msg + "\n");
            atxEvento.setCaretPosition(atxEvento.getDocument().getLength());
        });
    }

    //  IMPLEMENTAZIONE DI GaraListener
    @Override
    public void onEvento(String messaggio) {
        aggiungiEvento(messaggio);
    }

    @Override
    public void onGiroCompletato(Pilota pilota, int giro) {
        if (giro == gara.getGiri()) {
            aggiungiEvento("🏁 " + pilota.getNome() + " taglio traguardo! Miglior giro: "
                    + String.format("%.2f", pilota.getMiglioreGiro()) + "s");
        }
    }

    /**
     * Chi arriva per primo al traguardo viene inserito per primo in
     * ordineArrivo. "synchronized" garantisce che due piloti che arrivano nello
     * stesso momento non ottengano la stessa posizione.
     */
    @Override
    public synchronized void onPilotaFinito(Pilota pilota) {
        if (!ordineArrivo.contains(pilota)) {
            ordineArrivo.add(pilota);
        }
        int posizione = ordineArrivo.indexOf(pilota) + 1;
        gara.registraArrivo(pilota);
        aggiungiEvento("🏆 " + pilota.getNome() + " arriva " + posizione + "°!");
    }

    /**
     * Kimi si ritira → sceglie una vittima casuale e mostra il popup
     * Talibantonelli. compareAndSet garantisce che succeda una sola volta.
     */
    public void onKimiRitirato(List<Pilota> tuttiiPiloti) {
        if (!kimiRitirato.compareAndSet(false, true)) {
            return;
        }
        List<Pilota> inGara = new ArrayList<>();
        for (Pilota p : tuttiiPiloti) {
            if (!p.getNome().equals("Kimi Antonelli") && p.getStato() != Statoauto.Ritirato) {
                inGara.add(p);
            }
        }
        if (!inGara.isEmpty()) {
            Pilota vittima = inGara.get(new java.util.Random().nextInt(inGara.size()));
            vittima.forzaRitiro();
            aggiungiEvento("💥 " + vittima.getNome() + " colpito da Kimi!");
            javax.swing.SwingUtilities.invokeLater(()
                    -> javax.swing.JOptionPane.showMessageDialog(this,
                            "TALIBANTONELLI COLPISCE ANCORA!\n\n"
                            + "Kimi Antonelli ha perso il controllo e ha portato\n"
                            + "via con sé " + vittima.getNome() + "!\n\n"
                            + "Nessuno è al sicuro in pista con lui! 😱",
                            "⚠️  INCIDENTE IN PISTA!", javax.swing.JOptionPane.WARNING_MESSAGE));
        }
    }

    public List<Pilota> getPiloti() {
        return piloti;
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
            java.util.logging.Logger.getLogger(JfrmGara.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
}
