package com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase.RegistrarEvaluacionFichaPerfilUseCase;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.RegistrarEvaluacionFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionFichaPerfilUseCaseImpl implements RegistrarEvaluacionFichaPerfilUseCase {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;
    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;
    private final RegistrarEvaluacionFichaPerfilValidator registrarEvaluacionFichaPerfilValidator;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public UUID ejecutar(RegistrarEvaluacionFichaPerfilCommand entrada) {
        var evaluacion = EvaluacionFichaPerfilDomain.crear(
                entrada.representanteComite(),
                entrada.fichaPerfil());

        registrarEvaluacionFichaPerfilValidator.validar(evaluacion);

        evaluacionFichaPerfilOutputPort.guardar(evaluacion);
        asignarEstadoInicialEvaluacion(evaluacion.getId());

        logger.info(
                catalog.obtener(FichasKeys.EvaluacionFichaPerfil.LOG_REGISTRADA),
                evaluacion.getId(),
                evaluacion.getRepresentanteComiteId(),
                evaluacion.getFichaPerfilId());

        return evaluacion.getId();
    }

    private void asignarEstadoInicialEvaluacion(UUID evaluacionFichaPerfil) {
        var estadoInicial = EstadoEvaluacionFichaDomain.crear(evaluacionFichaPerfil);
        estadoEvaluacionFichaOutputPort.guardar(estadoInicial);
        logger.info(
                catalog.obtener(FichasKeys.EstadoEvaluacionFicha.LOG_CREADO_AUTOMATICO),
                estadoInicial.getId(),
                estadoInicial.getEvaluacionFichaPerfilId(),
                estadoInicial.getEstadoEvaluacion());
    }
}
