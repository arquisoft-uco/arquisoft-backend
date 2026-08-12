package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EstadoEnEvaluacionExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EvaluacionFichaExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.RepresentantePropietarioEvaluacionFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.AgregarEstadoEvaluacionFichaUseCase;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.AgregarEstadoEvaluacionFichaValidator;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarEstadoEvaluacionFichaUseCaseImpl implements AgregarEstadoEvaluacionFichaUseCase {

    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;
    private final EvaluacionFichaExisteFinder evaluacionFichaExisteFinder;
    private final RepresentantePropietarioEvaluacionFinder representantePropietarioEvaluacionFinder;
    private final EstadoEnEvaluacionExisteFinder estadoEnEvaluacionExisteFinder;
    private final AgregarEstadoEvaluacionFichaValidator agregarEstadoEvaluacionFichaValidator;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public UUID ejecutar(AgregacionEstadoEvaluacionFichaDomain entrada) {
        boolean evaluacionExiste = evaluacionFichaExisteFinder.obtener(entrada.getEvaluacionFichaPerfil());
        boolean esPropietario = representantePropietarioEvaluacionFinder.obtener(entrada);
        boolean estadoYaExiste = estadoEnEvaluacionExisteFinder.obtener(entrada);

        agregarEstadoEvaluacionFichaValidator.validar(
                entrada, evaluacionExiste, esPropietario, estadoYaExiste);

        var estadoEvaluacion = EstadoEvaluacionFichaDomain.crearConEstado(
                entrada.getEvaluacionFichaPerfil(),
                entrada.getEstadoEvaluacion(),
                obtenerUltimoEstado(entrada.getEvaluacionFichaPerfil()));

        estadoEvaluacionFichaOutputPort.agregarEstado(estadoEvaluacion);

        logger.info(
                catalogo.obtener(EstadoEvaluacionFichaKey.LOG_AGREGADO),
                estadoEvaluacion.getId(),
                estadoEvaluacion.getEvaluacionFichaPerfilId(),
                estadoEvaluacion.getEstadoEvaluacion());

        return estadoEvaluacion.getId();
    }

    private EstadoEvaluacion obtenerUltimoEstado(UUID evaluacionFichaPerfil) {
        return estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionFichaPerfil)
                .orElse(null);
    }
}
