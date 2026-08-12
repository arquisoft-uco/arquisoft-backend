package com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.EvaluacionDeRepresentanteExisteFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase.RegistrarEvaluacionFichaPerfilUseCase;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.RegistrarEvaluacionFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.application.representantecomite.command.finder.RepresentanteComiteExisteFinder;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
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
    private final CatalogoMensajes catalogo;

    @Override
    public UUID ejecutar(EvaluacionFichaPerfilDomain evaluacion) {
        boolean fichaExiste = fichaPerfilExisteFinder.obtener(evaluacion.getFichaPerfilId());
        boolean representanteExiste = representanteComiteExisteFinder.obtener(
                evaluacion.getRepresentanteComiteId());
        boolean evaluacionYaExiste = evaluacionDeRepresentanteExisteFinder.obtener(evaluacion);

        registrarEvaluacionFichaPerfilValidator.validar(
                evaluacion, fichaExiste, representanteExiste, evaluacionYaExiste);

        evaluacionFichaPerfilOutputPort.registrarEvaluacion(evaluacion);
        asignarEstadoInicialEvaluacion(evaluacion.getId());

        logger.info(
                catalogo.obtener(EvaluacionFichaPerfilKey.LOG_REGISTRADA),
                evaluacion.getId(),
                evaluacion.getRepresentanteComiteId(),
                evaluacion.getFichaPerfilId());

        return evaluacion.getId();
    }

    private void asignarEstadoInicialEvaluacion(UUID evaluacionFichaPerfil) {
        var estadoInicial = EstadoEvaluacionFichaDomain.crear(evaluacionFichaPerfil);
        estadoEvaluacionFichaOutputPort.registrarEstadoInicial(estadoInicial);
        logger.info(
                catalogo.obtener(EstadoEvaluacionFichaKey.LOG_CREADO_AUTOMATICO),
                estadoInicial.getId(),
                estadoInicial.getEvaluacionFichaPerfilId(),
                estadoInicial.getEstadoEvaluacion());
    }
}
