package com.arquisoft.mapas_ruta.application.maparuta.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapaRutaPorIdFinder;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.MapaRutaOutputPort;
import com.arquisoft.mapas_ruta.application.maparuta.command.usecase.CambiarFechaFinMapaRutaUseCase;
import com.arquisoft.mapas_ruta.application.maparuta.command.validator.CambiarFechaFinMapaRutaValidator;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.mapas_ruta.domain.maparuta.CambioFechaFinMapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.event.MapaRutaFechaFinCambiadaEvent;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarFechaFinMapaRutaUseCaseImpl implements CambiarFechaFinMapaRutaUseCase {

    private final MapaRutaOutputPort mapaRutaOutputPort;
    private final MapaRutaPorIdFinder mapaRutaPorIdFinder;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final CambiarFechaFinMapaRutaValidator cambiarFechaFinMapaRutaValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioFechaFinMapaRutaDomain entrada) {
        logger.info(MapaRutaKey.LOG_CAMBIANDO, entrada.getMapaRuta(), entrada.getCoordinador());

        var mapaRuta = mapaRutaPorIdFinder.obtener(entrada.getMapaRuta());
        var mapaRutaExiste = !mapaRuta.esVacio();
        var proyectoGrado = mapaRutaExiste
                ? proyectoGradoPorIdFinder.obtener(mapaRuta.getProyectoGrado())
                : ProyectoGradoDomain.VACIO;

        logger.debug(MapaRutaKey.LOG_VERIFICACION_CAMBIAR, mapaRutaExiste, !proyectoGrado.esVacio());
        cambiarFechaFinMapaRutaValidator.validar(entrada, mapaRuta, proyectoGrado);

        mapaRutaOutputPort.actualizarFechaFin(entrada.getMapaRuta(), entrada.getFechaFin());
        eventPublisher.publish(new MapaRutaFechaFinCambiadaEvent(
                mapaRuta.getId(),
                mapaRuta.getProyectoGrado(),
                mapaRuta.getFechaInicio(),
                mapaRuta.getFechaFin(),
                entrada.getFechaFin()));

        logger.info(MapaRutaKey.LOG_CAMBIADO, entrada.getMapaRuta(), entrada.getFechaFin());
    }
}
