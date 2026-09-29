package com.arquisoft.mapas_ruta.application.maparuta.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapaRutaDeProyectoExisteFinder;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.MapaRutaOutputPort;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.mapper.MapaRutaMapper;
import com.arquisoft.mapas_ruta.application.maparuta.command.usecase.AgregarMapaRutaUseCase;
import com.arquisoft.mapas_ruta.application.maparuta.command.validator.AgregarMapaRutaValidator;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.mapas_ruta.domain.maparuta.AgregacionMapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.event.MapaRutaAgregadoEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarMapaRutaUseCaseImpl implements AgregarMapaRutaUseCase {

    private final MapaRutaOutputPort mapaRutaOutputPort;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final MapaRutaDeProyectoExisteFinder mapaRutaDeProyectoExisteFinder;
    private final AgregarMapaRutaValidator agregarMapaRutaValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AgregacionMapaRutaDomain entrada) {
        var mapaRuta = entrada.getMapaRuta();
        logger.info(MapaRutaKey.LOG_AGREGANDO, mapaRuta.getProyectoGrado(), entrada.getCoordinador());

        var proyectoGrado = proyectoGradoPorIdFinder.obtener(mapaRuta.getProyectoGrado());
        var proyectoGradoExiste = !proyectoGrado.esVacio();
        var mapaRutaExiste = proyectoGradoExiste
                && mapaRutaDeProyectoExisteFinder.obtener(mapaRuta.getProyectoGrado());

        logger.debug(MapaRutaKey.LOG_VERIFICACION_AGREGAR, proyectoGradoExiste, mapaRutaExiste);
        agregarMapaRutaValidator.validar(entrada, proyectoGrado, mapaRutaExiste);

        mapaRutaOutputPort.registrar(MapaRutaMapper.toEntity(mapaRuta));
        eventPublisher.publish(new MapaRutaAgregadoEvent(
                mapaRuta.getId(),
                mapaRuta.getProyectoGrado(),
                mapaRuta.getFechaInicio(),
                mapaRuta.getFechaFin()));

        logger.info(MapaRutaKey.LOG_AGREGADO, mapaRuta.getId(), mapaRuta.getProyectoGrado());
        return mapaRuta.getId();
    }
}
