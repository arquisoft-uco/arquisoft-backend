package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.scheduler;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.DetectarPlazosVencidosMapaRutaInteractor;
import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.DetectarPlazosVencidosMapaRutaCommand;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import com.arquisoft.shared.util.UtilFecha;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class DetectarPlazosVencidosMapaRutaScheduler {

    private final DetectarPlazosVencidosMapaRutaInteractor detectarPlazosVencidosMapaRutaInteractor;
    private final GestorTraza gestorTraza;
    private final AppLogger logger;
    private final ZoneId zona;

    public DetectarPlazosVencidosMapaRutaScheduler(
            DetectarPlazosVencidosMapaRutaInteractor detectarPlazosVencidosMapaRutaInteractor,
            GestorTraza gestorTraza,
            AppLogger logger,
            @Value("${plazo-mapa-ruta.zona-horaria:America/Bogota}") String zona) {
        this.detectarPlazosVencidosMapaRutaInteractor = detectarPlazosVencidosMapaRutaInteractor;
        this.gestorTraza = gestorTraza;
        this.logger = logger;
        this.zona = ZoneId.of(zona);
    }

    @Scheduled(
            cron = "${plazo-mapa-ruta.cron:0 5 0 * * *}",
            zone = "${plazo-mapa-ruta.zona-horaria:America/Bogota}")
    public void detectar() {
        try (var alcance = gestorTraza.abrir(SolicitudTraza.paraProgramado())) {
            try {
                var hoy = UtilFecha.generarFechaActual(zona);
                detectarPlazosVencidosMapaRutaInteractor.ejecutar(DetectarPlazosVencidosMapaRutaCommand.crear(hoy));
            } catch (RuntimeException e) {
                logger.error(MapaRutaKey.LOG_DETECCION_INESPERADA, e, e.getMessage());
            }
        }
    }
}
