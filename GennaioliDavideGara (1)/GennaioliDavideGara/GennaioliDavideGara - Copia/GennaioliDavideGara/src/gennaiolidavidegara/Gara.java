package gennaiolidavidegara;

import java.util.ArrayList;
import java.util.List;

/**
 * Contiene tutti i dati della gara e gestisce i piloti.
 *
 * Questa classe non ha grafica: è solo logica e dati.
 * Funziona come "contenitore centrale" che sa tutto sulla gara:
 * quale circuito, quanti giri, che meteo, e chi sta correndo.
 *
 * Come viene usata:
 *   1) JfrmSchermataIniziale crea un oggetto Gara con circuito+giri+meteo
 *   2) Aggiunge i 6 piloti con aggiungiPilota()
 *   3) Passa l'oggetto a JfrmGara
 *   4) JfrmGara chiama avviaGara() dopo il semaforo → partono i thread
 */
public class Gara {

    // Nome del circuito scelto dall'utente (es. "Monza - Italian GP")
    private String nomeCircuito;

    // Numero totale di giri da completare (scelto con lo slider, tra 5 e 80)
    private int giri;

    // Condizione meteo di oggi (SOLE, NUVOLOSO o PIOGGIA)
    private meteo meteoGara;

    // Lista dei 6 piloti che partecipano alla gara
    private List<Pilota> piloti;

    // Lista che registra i piloti nell'ordine in cui hanno tagliato il traguardo
    private List<Pilota> ordineArrivo;

    // Riferimento alla finestra di gara (JfrmGara) che vuole ricevere notifiche
    private GaraListener listener;

    /**
     * Crea una nuova gara con le impostazioni scelte dall'utente.
     *
     * @param nomeCircuito nome del circuito (es. "Monaco - Monaco GP")
     * @param giri         numero di giri da completare
     * @param meteoGara    condizione meteo per questa gara
     */
    public Gara(String nomeCircuito, int giri, meteo meteoGara) {
        this.nomeCircuito = nomeCircuito;
        this.giri         = giri;
        this.meteoGara    = meteoGara;
        this.piloti       = new ArrayList<>();
        this.ordineArrivo = new ArrayList<>();
    }

    /**
     * Imposta chi vuole ricevere le notifiche (JfrmGara).
     * Comunica il listener anche a tutti i piloti già aggiunti.
     *
     * @param listener la finestra JfrmGara
     */
    public void setListener(GaraListener listener) {
        this.listener = listener;
        // Aggiorna anche i piloti già aggiunti prima di questo metodo
        for (Pilota p : piloti) {
            p.setListener(listener);
        }
    }

    /**
     * Aggiunge un pilota alla lista dei partecipanti.
     * Se il listener è già pronto lo assegna subito al pilota.
     * Chiamato 6 volte da JfrmSchermataIniziale prima di aprire la gara.
     *
     * @param pilota il pilota da aggiungere
     */
    public void aggiungiPilota(Pilota pilota) {
        piloti.add(pilota);
        if (listener != null) {
            pilota.setListener(listener);
        }
    }

    /**
     * Avvia tutti i thread dei piloti contemporaneamente.
     * Viene chiamato da JfrmGara subito dopo il semaforo verde.
     * Da questo momento ogni pilota "corre" in parallelo.
     */
    public void avviaGara() {
        for (Pilota p : piloti) {
            p.avviaThread();
        }
    }

    /**
     * Registra l'ordine di arrivo quando un pilota taglia il traguardo.
     * "synchronized" = se due piloti arrivano nello stesso momento,
     * Java li fa entrare qui uno per volta → nessuna posizione duplicata.
     *
     * @param pilota il pilota che ha appena tagliato il traguardo
     */
    public synchronized void registraArrivo(Pilota pilota) {
        if (!ordineArrivo.contains(pilota)) {
            ordineArrivo.add(pilota);
        }
    }

    // --- Metodi per leggere i dati della gara dall'esterno ---

    /** Restituisce la lista di tutti i piloti. */
    public List<Pilota> getPiloti() { return piloti; }

    /** Restituisce il meteo della gara (SOLE, NUVOLOSO o PIOGGIA). */
    public meteo getMeteo() { return meteoGara; }

    /** Restituisce il nome del circuito. */
    public String getNomeCircuito() { return nomeCircuito; }

    /** Restituisce il numero totale di giri. */
    public int getGiri() { return giri; }
}
