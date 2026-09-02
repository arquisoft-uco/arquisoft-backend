package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EvaluacionFichaExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.mapper.EstadoEvaluacionFichaMapper;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.AsignarEstadoInicialEvaluacionUseCase;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.AsignarEstadoInicialEvaluacionValidator;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsignarEstadoInicialEvaluacionUseCaseImpl implements AsignarEstadoInicialEvaluacionUseCase {

    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;
    private final EvaluacionFichaExisteFinder evaluacionFichaExisteFinder;
    private final AsignarEstadoInicialEvaluacionValidator asignarEstadoInicialEvaluacionValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(EvaluacionFichaPerfilDomain evaluacion) {
        boolean evaluacionExiste = evaluacionFichaExisteFinder.obtener(evaluacion.getId());

        asignarEstadoInicialEvaluacionValidator.validar(evaluacion.getId(), evaluacionExiste);

        var estadoInicial = EstadoEvaluacionFichaDomain.crear(evaluacion.getId());
        estadoEvaluacionFichaOutputPort.registrarEstadoInicial(
                EstadoEvaluacionFichaMapper.toEntity(estadoInicial));

        logger.debug(
                Mensajes.obtener(EstadoEvaluacionFichaKey.LOG_CREADO_AUTOMATICO),
                estadoInicial.getId(),
                estadoInicial.getEvaluacionFichaPerfilId(),
                estadoInicial.getEstadoEvaluacion());
    }
}
