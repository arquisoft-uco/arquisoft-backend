package com.arquisoft.shared.redis.catalogo.messageuco;

import co.edu.uco.messageucolab.client.api.MessageReader;
import co.edu.uco.messageucolab.client.api.model.MessageCollection;
import co.edu.uco.messageucolab.client.api.model.MessageDetails;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.AridadClave;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.ClavesCatalogo;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Copia en Redis los textos de MessageUcoLab (ADR-017).
 *
 * <p>Hay dos entradas: {@link #sincronizar()}, que trae todos los mensajes (al arrancar y ante un
 * cambio masivo), y {@link #actualizar(String)}, que trae uno solo cuando llega su aviso de cambio.
 * Ambas aplican las mismas reglas.
 *
 * <p>{@link MessageReader} es reactivo y aquí se bloquea a propósito: esto corre en el hilo del
 * arranque y en el del listener de Pulsar, nunca en un hilo de Reactor.
 *
 * <p>Redis sigue siendo lo único que lee la aplicación: esta clase solo cambia quién lo alimenta,
 * así que la latencia, el fail-fast y la degradación de ADR-013 no se tocan.
 *
 * <p>Recorre las claves declaradas y no los mensajes recibidos porque esta instancia de Redis
 * comparte espacio con el rate limit y los tokens invalidados: un título arbitrario en MessageUcoLab
 * no debe poder escribir una clave ajena al catálogo.
 *
 * <p>Nunca borra ni pone caducidad. Una clave faltante impide el siguiente arranque, y que un
 * mensaje no llegue puede significar solo que lo desactivaron: conservar el último texto válido es
 * lo que permite arrancar aunque MessageUcoLab esté caído.
 */
public class SincronizadorCatalogoMessageUco {

    // Se ejecuta antes de que exista el catálogo, así que sus textos son literales.
    private static final String LOG_ACTUALIZADO =
            "Catálogo sincronizado desde MessageUcoLab: {} claves actualizadas";
    private static final String LOG_SIN_CAMBIOS = "Catálogo sincronizado desde MessageUcoLab: sin cambios";
    private static final String LOG_AUSENTES =
            "MessageUcoLab no trajo {} clave(s) del catálogo, se conserva su texto en Redis: {}";
    private static final String LOG_RECHAZADAS =
            "MessageUcoLab trajo {} texto(s) vacíos o con aridad inválida, no se escriben: {}";
    private static final String LOG_IGNORADOS =
            "MessageUcoLab trajo {} mensaje(s) cuyo título no es una clave del catálogo, se ignoran";
    private static final String LOG_FALLO_ARRANQUE =
            "No se pudo sincronizar el catálogo desde MessageUcoLab al arrancar, se validará lo que tenga Redis: {}";
    private static final String LOG_CLAVE_ACTUALIZADA = "Clave del catálogo actualizada desde MessageUcoLab: {} ({})";
    private static final String LOG_CLAVE_SIN_CAMBIOS = "Clave del catálogo sin cambios en MessageUcoLab: {} ({})";
    private static final String LOG_CLAVE_IGNORADA =
            "El mensaje {} de MessageUcoLab tiene el título '{}', que no es una clave del catálogo; se ignora";
    private static final String LOG_CLAVE_RECHAZADA =
            "El mensaje {} de MessageUcoLab trae un texto vacío o con aridad inválida para {}; se conserva el de Redis";

    private static final int TAMANIO_LOTE = 500;
    private static final int MAXIMO_A_NOMBRAR = 20;

    private static final Map<String, ClaveMensaje> DECLARADAS = indexarDeclaradas();

    private final MessageReader lector;
    private final StringRedisTemplate plantilla;
    private final AppLogger logger;

    // Cuándo empezó la última sincronización completa que terminó bien. Todo lo publicado en
    // MessageUcoLab antes de ese instante ya está en Redis: la escucha de avisos lo usa para no
    // volver a aplicar avisos viejos al suscribirse.
    private volatile Instant inicioUltimaCompleta;

    public SincronizadorCatalogoMessageUco(
            MessageReader lector, StringRedisTemplate plantilla, AppLogger logger) {
        this.lector = lector;
        this.plantilla = plantilla;
        this.logger = logger;
    }

    /**
     * Sincroniza antes de validar el catálogo. Un fallo no aborta aquí: quien decide si se puede
     * arrancar es el fail-fast, según lo que Redis tenga.
     */
    public void sincronizarAlArrancar() {
        try {
            sincronizar();
        } catch (RuntimeException e) {
            logger.warn(LOG_FALLO_ARRANQUE, e.getMessage());
        }
    }

    /**
     * Trae los mensajes y escribe en Redis solo los que cambiaron.
     *
     * @return el resultado de la sincronización
     */
    public ResultadoSincronizacion sincronizar() {
        // Se toma antes de consultar: un cambio publicado durante la consulta queda después de este
        // instante y su aviso se aplica, en lugar de perderse entre la consulta y la suscripción.
        Instant comienzo = Instant.now();
        Map<String, String> recibidos = indexar(lector.getAll().block());

        List<ClaveMensaje> declaradas = ClavesCatalogo.TODAS;
        Map<String, String> cambios = new HashMap<>();
        List<String> ausentes = new ArrayList<>();
        List<String> rechazadas = new ArrayList<>();
        int reconocidos = 0;

        for (int inicio = 0; inicio < declaradas.size(); inicio += TAMANIO_LOTE) {
            List<ClaveMensaje> lote = declaradas.subList(inicio, Math.min(inicio + TAMANIO_LOTE, declaradas.size()));
            List<String> actuales = plantilla.opsForValue().multiGet(lote.stream().map(ClaveMensaje::clave).toList());

            for (int i = 0; i < lote.size(); i++) {
                ClaveMensaje clave = lote.get(i);
                String texto = recibidos.get(clave.clave());
                if (UtilObjeto.esNulo(texto)) {
                    ausentes.add(clave.clave());
                    continue;
                }

                reconocidos++;
                if (!esValido(clave, texto)) {
                    rechazadas.add(clave.clave());
                } else if (UtilObjeto.esNulo(actuales) || !texto.equals(actuales.get(i))) {
                    cambios.put(clave.clave(), texto);
                }
            }
        }

        if (!cambios.isEmpty()) {
            plantilla.opsForValue().multiSet(cambios);
        }

        var resultado = new ResultadoSincronizacion(
                cambios.size(), List.copyOf(ausentes), List.copyOf(rechazadas), recibidos.size() - reconocidos);
        registrar(resultado);
        inicioUltimaCompleta = comienzo;
        return resultado;
    }

    /**
     * Cuándo empezó la última sincronización completa que terminó bien.
     *
     * @return el instante, o vacío si ninguna terminó bien (por ejemplo, MessageUcoLab caído al arrancar)
     */
    public Optional<Instant> inicioUltimaSincronizacionCompleta() {
        return Optional.ofNullable(inicioUltimaCompleta);
    }

    /**
     * Trae un solo mensaje y, si su título es una clave declarada, escribe su texto en Redis cuando
     * cambió. Lo usa el aviso de creación o modificación de ese mensaje.
     *
     * <p>Los errores de MessageUcoLab se propagan: quien escucha el aviso decide si reintentarlo.
     *
     * @param codigo código del mensaje en MessageUcoLab ({@code MSG_...})
     * @return qué pasó con la clave
     */
    public ResultadoActualizacion actualizar(String codigo) {
        MessageDetails mensaje = lector.getDetails(codigo).block();
        if (UtilObjeto.esNulo(mensaje)) {
            return ResultadoActualizacion.NO_ENCONTRADO;
        }

        ClaveMensaje clave = DECLARADAS.get(mensaje.title());
        if (UtilObjeto.esNulo(clave)) {
            logger.warn(LOG_CLAVE_IGNORADA, codigo, mensaje.title());
            return ResultadoActualizacion.NO_ES_CLAVE;
        }

        String texto = mensaje.content();
        if (!esValido(clave, texto)) {
            logger.error(LOG_CLAVE_RECHAZADA, codigo, clave.clave());
            return ResultadoActualizacion.RECHAZADA;
        }

        if (texto.equals(plantilla.opsForValue().get(clave.clave()))) {
            logger.debug(LOG_CLAVE_SIN_CAMBIOS, clave.clave(), codigo);
            return ResultadoActualizacion.SIN_CAMBIOS;
        }

        plantilla.opsForValue().set(clave.clave(), texto);
        logger.info(LOG_CLAVE_ACTUALIZADA, clave.clave(), codigo);
        return ResultadoActualizacion.ACTUALIZADA;
    }

    private static boolean esValido(ClaveMensaje clave, String texto) {
        return !UtilTexto.esVacioONulo(texto) && AridadClave.marcadores(clave, texto) == clave.parametros();
    }

    private static Map<String, ClaveMensaje> indexarDeclaradas() {
        Map<String, ClaveMensaje> declaradas = new HashMap<>();
        for (ClaveMensaje clave : ClavesCatalogo.TODAS) {
            declaradas.put(clave.clave(), clave);
        }
        return Map.copyOf(declaradas);
    }

    private Map<String, String> indexar(MessageCollection coleccion) {
        Map<String, String> recibidos = new HashMap<>();
        if (UtilObjeto.esNulo(coleccion)) {
            return recibidos;
        }

        for (MessageDetails mensaje : UtilColeccion.aplicarPorDefecto(coleccion.messages())) {
            if (UtilObjeto.noEsNulo(mensaje.title()) && UtilObjeto.noEsNulo(mensaje.content())) {
                recibidos.put(mensaje.title(), mensaje.content());
            }
        }
        return recibidos;
    }

    private void registrar(ResultadoSincronizacion resultado) {
        if (resultado.actualizadas() > 0) {
            logger.info(LOG_ACTUALIZADO, resultado.actualizadas());
        } else {
            logger.debug(LOG_SIN_CAMBIOS);
        }
        if (!UtilColeccion.esVaciaONula(resultado.ausentes())) {
            logger.warn(LOG_AUSENTES, resultado.ausentes().size(), recortar(resultado.ausentes()));
        }
        if (!UtilColeccion.esVaciaONula(resultado.rechazadas())) {
            logger.error(LOG_RECHAZADAS, resultado.rechazadas().size(), recortar(resultado.rechazadas()));
        }
        if (resultado.ignorados() > 0) {
            logger.warn(LOG_IGNORADOS, resultado.ignorados());
        }
    }

    private List<String> recortar(List<String> claves) {
        return claves.subList(0, Math.min(MAXIMO_A_NOMBRAR, claves.size()));
    }
}
