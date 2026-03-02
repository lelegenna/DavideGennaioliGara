package gennaiolidavidegara;

/**
 * I tre stati in cui può trovarsi la macchina di un pilota durante la gara.
 *
 * Ogni oggetto Pilota ha un campo "stato" di questo tipo.
 * La finestra di gara (JfrmGara) lo legge ogni 200ms per sapere
 * cosa scrivere sulla progress bar e sull'etichetta del pilota.
 *
 * Flusso normale:
 *   IN_GARA → (arriva al giro del box) → BOX → IN_GARA → ... → fine gara
 *
 * Flusso con ritiro:
 *   IN_GARA → (sfortuna 1% per giro, o effetto Kimi) → Ritirato
 *   Una volta Ritirato il pilota smette di correre per sempre.
 */
public enum Statoauto {

    /** Macchina in pista, tutto ok, sta girando normalmente. */
    IN_GARA,

    /** Macchina ferma ai box per il cambio gomme. Dura 3x il tempo di un giro. */
    BOX,

    /**
     * Macchina fuori dalla gara.
     * Può succedere per:
     *   - Guasto casuale (1% di probabilità per ogni giro)
     *   - Kimi Antonelli si ritira (40% di chance nel primo terzo di gara)
     *   - Effetto Talibantonelli: Kimi porta via un pilota casuale con sé
     */
    Ritirato;
}
