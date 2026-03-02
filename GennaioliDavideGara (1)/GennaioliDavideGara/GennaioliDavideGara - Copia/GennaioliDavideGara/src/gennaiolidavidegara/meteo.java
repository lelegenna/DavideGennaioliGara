package gennaiolidavidegara;

/**
 * Elenco fisso delle condizioni meteo possibili per la gara.
 *
 * In Java un "enum" è una lista di valori costanti che non cambiano mai.
 * Invece di usare numeri (0=sole, 1=nuvoloso...) usiamo parole chiare,
 * così il codice si capisce subito.
 *
 * Il meteo viene scelto a caso in JfrmSchermataIniziale e poi passato
 * alla gara. Cambia anche le gomme consigliate:
 *   SOLE     → Morbide (più veloci sull'asciutto)
 *   NUVOLOSO → Dure    (più resistenti con temperatura bassa)
 *   PIOGGIA  → Intermedie (per la pista bagnata)
 */
public enum meteo {
    SOLE,     // cielo sereno
    NUVOLOSO, // cielo coperto
    PIOGGIA;  // pioggia → intermedie obbligatorie in F1 reale
}
