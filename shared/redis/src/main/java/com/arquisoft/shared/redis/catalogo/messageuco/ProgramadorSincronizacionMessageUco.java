package com.arquisoft.shared.redis.catalogo.messageuco;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Repite la sincronización con MessageUcoLab mientras la aplicación está arriba.
 *
 * <p>La reserva en Redis hace que, con varias instancias, solo una consulte por ronda. No se libera
 * al terminar: caduca sola, para que la siguiente instancia no repita la misma ronda.
 */
public class ProgramadorSincronizacionMessageUco {

    private static final String CLAVE_RESERVA = "arquisoft:catalogo:messageuco:lock";
    private static final String VALOR_MARCA = "1";
    private static final String LOG_FALLO =
            "No se pudo sincronizar el catálogo desde MessageUcoLab, se conservan los textos actuales: {}";

    private final SincronizadorCatalogoMessageUco sincronizador;
    private final StringRedisTemplate plantilla;
    private final MessageUcoProperties propiedades;
    private final GestorTraza gestorTraza;
    private final AppLogger logger;

    public ProgramadorSincronizacionMessageUco(
            SincronizadorCatalogoMessageUco sincronizador,
            StringRedisTemplate plantilla,
            MessageUcoProperties propiedades,
            GestorTraza gestorTraza,
            AppLogger logger) {
        this.sincronizador = sincronizador;
        this.plantilla = plantilla;
        this.propiedades = propiedades;
        this.gestorTraza = gestorTraza;
        this.logger = logger;
    }

    /**
     * Ejecuta una ronda. El retardo inicial evita repetir la carga que acaba de hacer el arranque.
     *
     * <p>Captura toda excepción para que una caída de MessageUcoLab o de Redis no detenga la tarea.
     */
    @Scheduled(
            initialDelayString = "${arquisoft.catalogo.messageuco.intervalo:PT5M}",
            fixedDelayString = "${arquisoft.catalogo.messageuco.intervalo:PT5M}")
    public void ejecutar() {
        try (var alcance = gestorTraza.abrir(SolicitudTraza.paraProgramado())) {
            if (reservar()) {
                sincronizador.sincronizar();
            }
        } catch (RuntimeException e) {
            logger.warn(LOG_FALLO, e.getMessage());
        }
    }

    private boolean reservar() {
        return Boolean.TRUE.equals(
                plantilla.opsForValue().setIfAbsent(CLAVE_RESERVA, VALOR_MARCA, propiedades.lockLease()));
    }
}
