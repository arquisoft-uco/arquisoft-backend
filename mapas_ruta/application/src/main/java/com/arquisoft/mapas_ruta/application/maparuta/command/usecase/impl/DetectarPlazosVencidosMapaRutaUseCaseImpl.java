package com.arquisoft.mapas_ruta.application.maparuta.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapasRutaConPlazoVencidoFinder;
import com.arquisoft.mapas_ruta.application.maparuta.command.usecase.DetectarPlazosVencidosMapaRutaUseCase;
import com.arquisoft.mapas_ruta.domain.maparuta.DeteccionPlazosVencidosMapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.event.MapaRutaPlazoVencidoEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DetectarPlazosVencidosMapaRutaUseCaseImpl implements DetectarPlazosVencidosMapaRutaUseCase {

    private final MapasRutaConPlazoVencidoFinder mapasRutaConPlazoVencidoFinder;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(DeteccionPlazosVencidosMapaRutaDomain entrada) {
        logger.info(MapaRutaKey.LOG_DETECTANDO_PLAZOS, entrada.getHoy());

        var vencidos = mapasRutaConPlazoVencidoFinder.obtener(entrada.getHoy());
        logger.debug(MapaRutaKey.LOG_VERIFICACION_PLAZOS, vencidos.size());

        vencidos.forEach(mapaRuta -> eventPublisher.publish(new MapaRutaPlazoVencidoEvent(
                mapaRuta.getId(), mapaRuta.getProyectoGrado(), mapaRuta.getFechaFin())));

        logger.info(MapaRutaKey.LOG_PLAZOS_DETECTADOS, entrada.getHoy(), vencidos.size());
    }
}
