package com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.EvaluacionDeRepresentanteExisteFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase.RegistrarEvaluacionFichaPerfilUseCase;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.RegistrarEvaluacionFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.application.representantecomite.command.finder.RepresentanteComiteExisteFinder;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.mapper.EstadoEvaluacionFichaMapper;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.mapper.EvaluacionFichaPerfilMapper;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionFichaPerfilUseCaseImpl implements RegistrarEvaluacionFichaPerfilUseCase {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;
    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;
    private final FichaPerfilExisteFinder fichaPerfilExisteFinder;
    private final RepresentanteComiteExisteFinder representanteComiteExisteFinder;
    private final EvaluacionDeRepresentanteExisteFinder evaluacionDeRepresentanteExisteFinder;
    private final RegistrarEvaluacionFichaPerfilValidator registrarEvaluacionFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(EvaluacionFichaPerfilDomain evaluacion) {
        logger.info(Mensajes.obtener(EvaluacionFichaPerfilKey.LOG_REGISTRANDO),
                evaluacion.getFichaPerfilId(), evaluacion.getRepresentanteComiteId());

        boolean fichaExiste = fichaPerfilExisteFinder.obtener(evaluacion.getFichaPerfilId());
        boolean representanteExiste = representanteComiteExisteFinder.obtener(
                evaluacion.getRepresentanteComiteId());
        boolean evaluacionYaExiste = evaluacionDeRepresentanteExisteFinder.obtener(evaluacion);

        logger.debug(Mensajes.obtener(EvaluacionFichaPerfilKey.LOG_VERIFICACION_REGISTRAR),
                fichaExiste, representanteExiste, evaluacionYaExiste);

        registrarEvaluacionFichaPerfilValidator.validar(
                evaluacion, fichaExiste, representanteExiste, evaluacionYaExiste);

        evaluacionFichaPerfilOutputPort.registrarEvaluacion(EvaluacionFichaPerfilMapper.toEntity(evaluacion));
        asignarEstadoInicialEvaluacion(evaluacion.getId());

        logger.info(
                Mensajes.obtener(EvaluacionFichaPerfilKey.LOG_REGISTRADA),
                evaluacion.getId(),
                evaluacion.getRepresentanteComiteId(),
                evaluacion.getFichaPerfilId());

        return evaluacion.getId();
    }

    private void asignarEstadoInicialEvaluacion(UUID evaluacionFichaPerfil) {
        var estadoInicial = EstadoEvaluacionFichaDomain.crear(evaluacionFichaPerfil);
        estadoEvaluacionFichaOutputPort.registrarEstadoInicial(
                EstadoEvaluacionFichaMapper.toEntity(estadoInicial));
        logger.debug(
                Mensajes.obtener(EstadoEvaluacionFichaKey.LOG_CREADO_AUTOMATICO),
                estadoInicial.getId(),
                estadoInicial.getEvaluacionFichaPerfilId(),
                estadoInicial.getEstadoEvaluacion());
    }
}
