package gennaiolidavidegara;


public interface GaraListener {

    /**
     * Chiamato ogni volta che succede qualcosa degno di nota.
     * Esempi: "🔧 Verstappen ai BOX", "💥 Kimi RITIRATO", "🚦 VIA!"
     *
     * @param messaggio il testo dell'evento da mostrare nell'area eventi
     */
    void onEvento(String messaggio);

    /**
     * Chiamato dal pilota ogni volta che completa un giro.
     * Usato solo per rilevare l'ultimo giro e mostrare il miglior tempo.
   
     */
    void onGiroCompletato(Pilota pilota, int giro);

    /**
     * Chiamato quando il pilota taglia il traguardo (ha finito tutti i giri).
     * In JfrmGara è "synchronized" per gestire il caso in cui due piloti
     * arrivino quasi nello stesso momento senza sbagliarsi nelle posizioni.
     *
     
     */
    void onPilotaFinito(Pilota pilota);
}
