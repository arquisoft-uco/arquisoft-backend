package com.arquisoft.shared.message;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Comprobaciones de aridad de una clave: cuántos marcadores lleva su patrón y si la vía por la que
 * se está resolviendo encaja con ellos.
 *
 * <p>Existe porque el catálogo tiene dos familias de patrón que no comparten sintaxis de marcador, y
 * cada una falla en silencio a su manera. Los mensajes al cliente llevan {@code %s} y los sustituye
 * {@link String#formatted}, que lanza si faltan argumentos pero ignora los que sobran; los patrones
 * de log llevan {@code {}} y los sustituye SLF4J, que no protesta en ninguno de los dos casos — se
 * come los sobrantes e imprime el {@code {}} literal cuando faltan, en un log de producción que
 * nadie mira hasta que hay un incidente.
 *
 * <p>Elegir mal la vía es el tercer fallo, y es el peor de los tres: {@code formatear} sobre un
 * patrón de log no sustituye nada porque ahí no hay ningún {@code %s}, y {@code obtener} sobre un
 * mensaje de error devuelve el patrón crudo, con los {@code %s} a la vista del usuario final.
 *
 * <p>Esta clase solo diagnostica: devuelve la descripción del problema y no lanza. Qué hacer con
 * ella es decisión de cada catálogo, y difiere a propósito — el de Redis lo registra y cae al
 * respaldo, porque suele ejecutarse construyendo la respuesta de un error que ya ocurrió y no puede
 * convertirlo en un 500; el de pruebas lanza, porque en un test el fallo tiene que ser ruidoso.
 */
public final class AridadClave {

    // Conversión de java.util.Formatter. El escape %% se cuenta aparte porque no consume argumento.
    private static final String ESCAPE_PORCENTAJE = "%%";
    private static final Pattern MARCADOR_FORMATO =
            Pattern.compile("%(?:%|[-#+ 0,(]*\\d*(?:\\.\\d+)?[a-zA-Z])");

    // Marcador de SLF4J. Se cuenta sin expresión regular para no escapar dos veces la barra del
    // escape \{}, que deja la llave literal y tampoco consume argumento.
    private static final String MARCADOR_SLF4J = "{}";
    private static final char ESCAPE_SLF4J = '\\';

    private static final String PROBLEMA_ARIDAD =
            "la clave declara %d argumento(s) y se recibieron %d";
    private static final String PROBLEMA_LOG_FORMATEADO =
            "es un patrón de log: sus marcadores {} los sustituye SLF4J, así que hay que resolverlo con obtener";
    private static final String PROBLEMA_PATRON_SIN_FORMATEAR =
            "la clave declara %d argumento(s) y obtener no sustituye ninguno: devolvería el patrón con los %%s crudos";

    private AridadClave() {}

    /**
     * Cuenta los marcadores del patrón, con la sintaxis que le corresponde a la categoría de la clave.
     *
     * @param clave  clave a la que pertenece el patrón
     * @param patron texto del catálogo
     * @return número de marcadores que consumen argumento
     */
    public static int marcadores(ClaveMensaje clave, String patron) {
        return esLog(clave)
                ? contarSlf4j(patron)
                : contarFormato(patron);
    }

    /**
     * Comprueba una llamada a {@code formatear}.
     *
     * @param clave      clave que se está resolviendo
     * @param argumentos número de argumentos recibidos
     * @return la descripción del problema, o vacío si la llamada es coherente
     */
    public static Optional<String> alFormatear(ClaveMensaje clave, int argumentos) {
        if (esLog(clave)) {
            return Optional.of(PROBLEMA_LOG_FORMATEADO);
        }
        if (argumentos != clave.parametros()) {
            return Optional.of(PROBLEMA_ARIDAD.formatted(clave.parametros(), argumentos));
        }
        return Optional.empty();
    }

    /**
     * Comprueba una llamada a {@code obtener}.
     *
     * <p>Una clave de log siempre es coherente por aquí, tenga los marcadores que tenga: esa es
     * justamente su vía, y quien sustituye es el logger.
     *
     * @param clave clave que se está resolviendo
     * @return la descripción del problema, o vacío si la llamada es coherente
     */
    public static Optional<String> alObtener(ClaveMensaje clave) {
        if (!esLog(clave) && clave.parametros() > 0) {
            return Optional.of(PROBLEMA_PATRON_SIN_FORMATEAR.formatted(clave.parametros()));
        }
        return Optional.empty();
    }

    private static boolean esLog(ClaveMensaje clave) {
        return CategoriaMensaje.LOG == CategoriaMensaje.desde(clave.clave());
    }

    private static int contarFormato(String patron) {
        Matcher coincidencias = MARCADOR_FORMATO.matcher(patron);
        int total = 0;

        while (coincidencias.find()) {
            if (!ESCAPE_PORCENTAJE.equals(coincidencias.group())) {
                total++;
            }
        }

        return total;
    }

    private static int contarSlf4j(String patron) {
        int total = 0;

        for (int i = patron.indexOf(MARCADOR_SLF4J); i >= 0; i = patron.indexOf(MARCADOR_SLF4J, i + 2)) {
            if (i == 0 || patron.charAt(i - 1) != ESCAPE_SLF4J) {
                total++;
            }
        }

        return total;
    }
}
