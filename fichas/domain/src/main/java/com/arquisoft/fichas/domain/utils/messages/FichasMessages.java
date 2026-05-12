package com.arquisoft.fichas.domain.utils.messages;

/**
 * Punto de entrada de las constantes de mensajes del contexto {@code fichas}.
 *
 * <h2>Nomenclatura de archivos de mensajes</h2>
 *
 * <p>Dentro de {@code utils/messages/} de cada capa {@code domain} se sigue
 * la convención de dos niveles:</p>
 *
 * <ul>
 *   <li><strong>{@code {Contexto}Messages}</strong> (este archivo) — agrupa referencias
 *       a los archivos de mensajes de cada entidad del contexto. Sirve como índice
 *       y punto único de descubrimiento: basta abrir este archivo para saber qué
 *       entidades tienen constantes definidas y en qué clase buscarlas.</li>
 *   <li><strong>{@code {Entidad}Messages}</strong> (ej. {@link FichaMessages}) — contiene
 *       las constantes propias de una entidad: nombres de campo, códigos de error y
 *       cualquier valor de validación específico de esa entidad. Un archivo por entidad,
 *       organizado por secciones con separadores de comentario.</li>
 * </ul>
 *
 * <p>Las constantes numéricas genéricas (longitudes máximas, mínimas, etc.) no
 * residen aquí — pertenecen a {@code shared:validation} en
 * {@code ValidationLimits}, ya que no son propias de ningún contexto.</p>
 *
 * <h2>Entidades del contexto fichas</h2>
 * <ul>
 *   <li>{@link FichaMessages} — constantes de {@code FichaPerfil}</li>
 * </ul>
 */
public final class FichasMessages {

    private FichasMessages() {}
}
