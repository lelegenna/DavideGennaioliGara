package gennaiolidavidegara;

import java.util.Random;

public class Pilota implements Runnable {

    private String nome;
    private String scuderia;
    private SceltaGomme gomme;
    private Statoauto stato;
    private int giriFatti;
    private int totalGiri;
    private double tempoTotale;  // somma dei tempi di tutti i giri (secondi simulati)
    private double miglioreGiro; // tempo del giro più veloce

    private Thread thread;           // il thread Java che fa correre questo pilota
    private GaraListener listener;   // chi vuole ricevere notifiche (JfrmGara)

    private int delayBase;     // millisecondi di pausa per ogni giro simulato
    private int indiceGriglia; // posizione in griglia: 0=P1, 5=P6 → serve per il pit stop

    /**
     * Se diventa true, il pilota si ritira al prossimo giro.
     *
     */
    private volatile boolean forzatoRitiro = false;

    /**
     * Riferimento diretto a JfrmGara, usato solo quando Kimi si ritira per
     * notificare la GUI del secondo ritiro a catena.
     */
    private JfrmGara frmGara;

    /**
     * Crea un pilota con tutti i suoi dati di gara.
     *
     */
    public Pilota(String nome, String scuderia, SceltaGomme gomme,
            int totalGiri, int delayBase, int indiceGriglia) {
        this.nome = nome;
        this.scuderia = scuderia;
        this.gomme = gomme;
        this.totalGiri = totalGiri;
        this.delayBase = delayBase;
        this.indiceGriglia = indiceGriglia;
        this.stato = Statoauto.IN_GARA; // parte in pista
        this.giriFatti = 0;
        this.tempoTotale = 0;
        this.miglioreGiro = Double.MAX_VALUE; // valore altissimo: scende al primo giro
    }

    /**
     * Imposta il listener e salva il riferimento a JfrmGara se è lui il
     * listener. JfrmGara è l'unico che implementa GaraListener in questo
     * progetto.
     *
     * @param listener la finestra JfrmGara
     */
    public void setListener(GaraListener listener) {
        this.listener = listener;
        if (listener instanceof JfrmGara) {
            this.frmGara = (JfrmGara) listener;
        }
    }

    /**
     * Forza il ritiro di questo pilota al prossimo giro. Chiamato da JfrmGara
     * quando Kimi sceglie la sua vittima casuale.
     */
    public void forzaRitiro() {
        forzatoRitiro = true;
    }

    /**
     * Crea il thread e lo avvia. setDaemon(true) = quando si chiude la
     * finestra, il thread si ferma da solo.
     */
    public void avviaThread() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Aspetta che questo thread abbia finito. Usato da JfrmGara per aspettare
     * tutti i piloti prima di mostrare la classifica finale.
     */
    public void aspettaThread() throws InterruptedException {
        if (thread != null) {
            thread.join();
        }
    }

    /**
     * Metodo principale del thread: la corsa vera e propria.
     *
     */
    @Override
    public void run() {
        Random rand = new Random();

        // Caso speciale: Kimi Talibantonelli (40% di probabilità di fare casino)
        if (nome.equals("Kimi Antonelli") && rand.nextDouble() < 0.40) {
            int giroRitiro = 1 + rand.nextInt(Math.max(1, totalGiri / 3));
            corriGiri(rand, giroRitiro ); // corre fino al giro prima del ritiro
            if (stato != Statoauto.Ritirato) {
                stato = Statoauto.Ritirato;
                if (listener != null) {
                    listener.onEvento("💥 Talibantonelli RITIRATO al giro " + giroRitiro + "!");
                }
                // Avvisa JfrmGara: deve far ritirare anche un altro pilota
                if (frmGara != null) {
                    frmGara.onKimiRitirato(frmGara.getPiloti());
                }
            }
            return; // il thread di Kimi finisce qui
        }

        // Caso normale: corre tutti i giri
        corriGiri(rand, totalGiri);

        // Se ha finito senza ritirarsi, notifica l'arrivo al traguardo
        if (stato != Statoauto.Ritirato && listener != null) {
            listener.onPilotaFinito(this);
        }
    }

    /**
     * Esegue i giri da 1 a maxGiri, gestendo pit stop e ritiri.
     *
     * Pit stop scaglionato: giroBox = (38% + indiceGriglia × 2.5%) del totale
     * giri ± 2 giri casuali Risultato: P1 va ai box ~38%, P6 va ai box ~52%
     * della gara → impossibile che tutti vadano ai box nello stesso giro
     
     */
    private void corriGiri(Random rand, int maxGiri) {
        // Calcola il giro del pit stop per questo pilota
        int offsetGiri = (int) (totalGiri * (0.38 + indiceGriglia * 0.025));
        int giroBox = offsetGiri + rand.nextInt(5) - 2; // ±2 giri di casualità
        giroBox = Math.max(3, Math.min(giroBox, totalGiri - 3)); // non ai box giro 1 o ultimo

        for (int giro = 1; giro <= maxGiri; giro++) {

            // Se è già ritirato (da un giro precedente), smetti
            if (stato == Statoauto.Ritirato) {
                break;
            }

            // Ritiro forzato da JfrmGara (vittima di Kimi)
            if (forzatoRitiro) {
                stato = Statoauto.Ritirato;
                if (listener != null) {
                    listener.onEvento("💥 " + nome + " costretto al ritiro!");
                }
                break;
            }

            // Ritiro casuale: 1% di probabilità per ogni giro (guasto, incidente)
            if (rand.nextDouble() < 0.01) {
                stato = Statoauto.Ritirato;
                if (listener != null) {
                    listener.onEvento("💥 " + nome + " RITIRATO al giro " + giro + "!");
                }
                break;
            }

            // Pit stop al giro calcolato
            if (giro == giroBox) {
                stato = Statoauto.BOX;
                if (listener != null) {
                    listener.onEvento("🔧 " + nome + " ai BOX (giro " + giro + ")");
                }
                try {
                    Thread.sleep(delayBase * 3);
                } // il pit stop dura 3 giri
                catch (InterruptedException e) {
                    return;
                }
                stato = Statoauto.IN_GARA; // torna in pista
            }

            // Calcola e registra il tempo di questo giro
            double tempo = calcolaTempoGiro(rand);
            tempoTotale += tempo;
            if (tempo < miglioreGiro) {
                miglioreGiro = tempo;
            }
            giriFatti++;

            if (listener != null) {
                listener.onGiroCompletato(this, giro);
            }

            // Pausa che simula il tempo per percorrere il giro
            try {
                Thread.sleep(delayBase);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * Calcola il tempo simulato di un giro in secondi. Base: 90 secondi ±
     * variazione casuale ± bonus/malus gomme.
     *
     * MORBIDE: -1.5 secondi (più veloci) INTERMEDIE: +0.5 secondi DURE: +1.0
     * secondo (più lente) Casualità: ±1.5 secondi 
     *
     */
    private double calcolaTempoGiro(Random rand) {
        double base = 90.0;
        switch (gomme) {
            case MORBIDE:
                base -= 1.5;
                break;
            case INTERMEDIE:
                base += 0.5;
                break;
            case DURE:
                base += 1.0;
                break;
        }
        base += (rand.nextDouble() - 0.5) * 3.0; // casualità tra -1.5 e +1.5
        return Math.round(base * 100.0) / 100.0;  // arrotonda a 2 decimali
    }


    public String getNome() {
        return nome;
    }

    public String getScuderia() {
        return scuderia;
    }

    public SceltaGomme getGomme() {
        return gomme;
    }

    public Statoauto getStato() {
        return stato;
    }

    public int getGiriFatti() {
        return giriFatti;
    }

    public int getTotalGiri() {
        return totalGiri;
    }

    public double getTempoTotale() {
        return tempoTotale;
    }

    /**
     * Restituisce il miglior tempo sul giro. 
     */
    public double getMiglioreGiro() {
        return miglioreGiro == Double.MAX_VALUE ? 0 : miglioreGiro;
    }

    /**
     * Restituisce "Nome (Scuderia)" - usato nei messaggi dell'area eventi.
     */
    @Override
    public String toString() {
        return nome + " (" + scuderia + ")";
    }
}
