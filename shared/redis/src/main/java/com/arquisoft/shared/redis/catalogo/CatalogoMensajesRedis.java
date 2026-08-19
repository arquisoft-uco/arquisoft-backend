package com.arquisoft.shared.redis.catalogo;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.ClavesCatalogo;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Catálogo de mensajes respaldado por Redis, con caché en memoria y degradación sin caída.
 *
 * <p>Usa {@link StringRedisTemplate} y no {@code RedisClient}: ese último serializa con
 * {@code GenericJacksonJsonRedisSerializer} con <em>default typing</em>, que escribe el tipo dentro
 * del JSON y no sabe deserializar las cadenas planas que deja en Redis el script de carga
 * ({@code SET clave "texto"}). Es la trampa más fácil de pisar de toda la migración.
 *
 * <p>Tres niveles de resolución:
 * <ol>
 *   <li>Redis. Si responde, refresca la caché y devuelve el texto.</li>
 *   <li>Caché en memoria, cuando Redis falla. Tras un arranque exitoso está <em>completa</em>, así
 *       que devuelve el texto real y no uno genérico.</li>
 *   <li>Respaldo compilado. Inalcanzable en la práctica tras un arranque exitoso: el arranque
 *       aborta si falta una clave, y las claves son constantes de compilación.</li>
 * </ol>
 *
 * <p>Una vez la aplicación está arriba, ningún método propaga la excepción de Redis ni devuelve la
 * clave cruda a un consumidor de API. El fail-fast es exclusivo del arranque.
 */
public class CatalogoMensajesRedis implements CatalogoMensajes {

    // Los textos de log de esta clase no salen del catálogo: es el propio catálogo el que está
    // fallando cuando se registran, así que resolverlos por él sería circular.
    private static final String LOG_DEGRADADO =
            "Catálogo de mensajes degradado: Redis no responde, se sirve desde la caché en memoria. Clave: {}";
    private static final String LOG_SIN_TEXTO =
            "Clave del catálogo sin texto ni en Redis ni en caché, se devuelve el respaldo. Clave: {}";
    private static final String LOG_FORMATO_INVALIDO =
            "El patrón del catálogo no admite los argumentos recibidos, se devuelve el respaldo. Clave: {}, argumentos: {}";

    private static final int TAMANIO_LOTE = 500;

    // Conversión de java.util.Formatter. El escape %% se cuenta aparte porque no consume argumento.
    private static final String ESCAPE_PORCENTAJE = "%%";
    private static final Pattern MARCADOR_FORMATO =
            Pattern.compile("%(?:%|[-#+ 0,(]*\\d*(?:\\.\\d+)?[a-zA-Z])");

    private final StringRedisTemplate plantilla;
    private final CatalogoMensajes respaldo;
    private final AppLogger logger;

    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final AtomicBoolean degradado = new AtomicBoolean(false);

    public CatalogoMensajesRedis(StringRedisTemplate plantilla, CatalogoMensajes respaldo, AppLogger logger) {
        this.plantilla = plantilla;
        this.respaldo = respaldo;
        this.logger = logger;
    }

    @Override
    public String obtener(ClaveMensaje clave) {
        String texto = resolver(clave);
        return texto != null ? texto : respaldo.obtener(clave);
    }

    @Override
    public String formatear(ClaveMensaje clave, Object... args) {
        String patron = resolver(clave);
        if (patron == null) {
            return respaldo.formatear(clave, args);
        }

        try {
            return patron.formatted(args);
        } catch (IllegalFormatException e) {
            // No se relanza a propósito: formatear se ejecuta dentro de GlobalAppExceptionHandler,
            // construyendo la respuesta de un error ya ocurrido. Lanzar aquí convertiría un 422
            // legible en un 500 sin cuerpo y el cliente perdería el errorCode. Tampoco se devuelve
            // el patrón crudo, que expondría los marcadores de formato al usuario.
            logger.error(LOG_FORMATO_INVALIDO, e, clave.clave(), args.length);
            return respaldo.formatear(clave, args);
        }
    }

    @Override
    public boolean contiene(ClaveMensaje clave) {
        return resolver(clave) != null;
    }

    /**
     * Recarga el catálogo completo desde Redis.
     *
     * <p>La usan el arranque y el monitor de reconexión. No lanza si faltan claves: devuelve el
     * resultado para que cada uno decida — el arranque aborta, el monitor se queda degradado.
     *
     * @return el resultado de la carga
     */
    public ResultadoCarga recargar() {
        List<ClaveMensaje> declaradas = ClavesCatalogo.TODAS;
        List<String> faltantes = new ArrayList<>();
        List<String> desajustes = new ArrayList<>();
        Map<String, String> cargadas = new ConcurrentHashMap<>();

        for (int inicio = 0; inicio < declaradas.size(); inicio += TAMANIO_LOTE) {
            List<ClaveMensaje> lote = declaradas.subList(inicio, Math.min(inicio + TAMANIO_LOTE, declaradas.size()));
            List<String> nombres = lote.stream().map(ClaveMensaje::clave).toList();
            List<String> textos = plantilla.opsForValue().multiGet(nombres);

            for (int i = 0; i < nombres.size(); i++) {
                String texto = textos == null ? null : textos.get(i);
                if (texto == null) {
                    faltantes.add(nombres.get(i));
                    continue;
                }

                cargadas.put(nombres.get(i), texto);
                if (contarMarcadores(texto) != lote.get(i).parametros()) {
                    desajustes.add(nombres.get(i));
                }
            }
        }

        cache.putAll(cargadas);
        return new ResultadoCarga(cargadas.size(), declaradas.size(),
                List.copyOf(faltantes), List.copyOf(desajustes));
    }

    // La aridad se comprueba también en el build, contra los .properties. Repetirla aquí no es
    // redundante: lo que se despliega es lo que Redis tenga, y entre el build y el arranque hay una
    // carga manual por medio que puede haber quedado a medias o apuntando a otra instancia.
    private static int contarMarcadores(String patron) {
        Matcher coincidencias = MARCADOR_FORMATO.matcher(patron);
        int total = 0;

        while (coincidencias.find()) {
            if (!ESCAPE_PORCENTAJE.equals(coincidencias.group())) {
                total++;
            }
        }

        return total;
    }

    /**
     * Indica si el catálogo está sirviendo desde la caché por no poder hablar con Redis.
     *
     * @return {@code true} si está degradado
     */
    public boolean estaDegradado() {
        return degradado.get();
    }

    /**
     * Marca el catálogo como sano. Lo llama el monitor, y solo tras una recarga completa.
     */
    public void marcarSano() {
        degradado.set(false);
    }

    /**
     * Número de claves en la caché en memoria.
     *
     * @return el tamaño de la caché
     */
    public int clavesEnCache() {
        return cache.size();
    }

    /**
     * Comprueba la conexión con Redis sin alterar el estado del catálogo.
     *
     * @return {@code true} si Redis responde
     */
    public boolean hayConexion() {
        try {
            plantilla.hasKey(ClavesCatalogo.TODAS.get(0).clave());
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private String resolver(ClaveMensaje clave) {
        String nombre = clave.clave();

        try {
            String texto = plantilla.opsForValue().get(nombre);
            if (texto != null) {
                cache.put(nombre, texto);
                return texto;
            }
        } catch (RuntimeException e) {
            if (degradado.compareAndSet(false, true)) {
                logger.warn(LOG_DEGRADADO, nombre);
            }
        }

        String enCache = cache.get(nombre);
        if (enCache == null) {
            logger.error(LOG_SIN_TEXTO, nombre);
        }
        return enCache;
    }
}
