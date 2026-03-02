package gennaiolidavidegara;

/**
 * I tre tipi di gomme che si possono scegliere per ogni pilota.
 *
 * La scelta della gomma modifica il tempo di ogni giro calcolato
 * dal metodo calcolaTempoGiro() dentro la classe Pilota.
 * Tempo base di un giro = 90 secondi simulati.
 *
 *   MORBIDE:    -1.5 secondi/giro  → più veloci, consigliate col sole
 *   INTERMEDIE: +0.5 secondi/giro  → per pista bagnata
 *   DURE:       +1.0 secondo/giro  → più lente ma resistenti, consigliate con nuvoloso
 *
 * Nella schermata iniziale le gomme vengono preselezionate in automatico
 * in base al meteo del giorno, ma l'utente può cambiarle liberamente.
 */
public enum SceltaGomme {
    DURE,        // gomme dure: più lente ma durano di più
    MORBIDE,     // gomme morbide: più veloci, consigliate col sole
    INTERMEDIE;  // gomme da bagnato, consigliate con la pioggia
}
