package com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.AsignarEstadoInicialEvaluacionUseCase;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.AsignarEstadoReactivadoEvaluacionUseCase;
import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.EvaluacionExistenteDeRepresentanteFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.result.InicioEvaluacionFichaPerfilResult;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.result.mapper.InicioEvaluacionFichaPerfilResultMapper;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.mapper.EvaluacionFichaPerfilMapper;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase.RegistrarEvaluacionFichaPerfilUseCase;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.RegistrarEvaluacionFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.application.representantecomite.command.finder.RepresentanteComiteVigenteFinder;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionFichaPerfilUseCaseImpl implements RegistrarEvaluacionFichaPerfilUseCase {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;
    private final FichaPerfilExisteFinder fichaPerfilExisteFinder;
    private final RepresentanteComiteVigenteFinder representanteComiteVigenteFinder;
    private final EvaluacionExistenteDeRepresentanteFinder evaluacionExistenteDeRepresentanteFinder;
    private final EstadoActualFichaPerfilFinder estadoActualFichaPerfilFinder;
    private final RegistrarEvaluacionFichaPerfilValidator registrarEvaluacionFichaPerfilValidator;
    private final AsignarEstadoInicialEvaluacionUseCase asignarEstadoInicialEvaluacionUseCase;
    private final AsignarEstadoReactivadoEvaluacionUseCase asignarEstadoReactivadoEvaluacionUseCase;
    private final AppLogger logger;

    @Override
    public InicioEvaluacionFichaPerfilResult ejecutar(EvaluacionFichaPerfilDomain evaluacion) {
        logger.info(EvaluacionFichaPerfilKey.LOG_REGISTRANDO,
                evaluacion.getFichaPerfilId(), evaluacion.getRepresentanteComiteId());

        var fichaExiste = fichaPerfilExisteFinder.obtener(evaluacion.getFichaPerfilId());
        var representanteExiste = representanteComiteVigenteFinder.obtener(
                evaluacion.getRepresentanteComiteId());
        var existente = evaluacionExistenteDeRepresentanteFinder.obtener(evaluacion);
        var estadoFicha = estadoActualFichaPerfilFinder.obtener(evaluacion.getFichaPerfilId());

        logger.debug(EvaluacionFichaPerfilKey.LOG_VERIFICACION_REGISTRAR,
                fichaExiste, representanteExiste, !existente.esVacio(), estadoFicha.getEstadoFicha().getId());

        registrarEvaluacionFichaPerfilValidator.validar(
                evaluacion, fichaExiste, representanteExiste, existente, estadoFicha);

        if (existente.esVacio()) {
            return registrar(evaluacion);
        }

        var estadoReactivado = EstadoEvaluacionFichaDomain.crearConEstado(
                existente.evaluacion(), EstadoEvaluacion.EN_EVALUACION.getId());

        asignarEstadoReactivadoEvaluacionUseCase.ejecutar(estadoReactivado);

        logger.info(EvaluacionFichaPerfilKey.LOG_REACTIVADA,
                existente.evaluacion(), evaluacion.getRepresentanteComiteId(), evaluacion.getFichaPerfilId());

        return InicioEvaluacionFichaPerfilResultMapper.toResultReactivada(existente.evaluacion());
    }

    private InicioEvaluacionFichaPerfilResult registrar(EvaluacionFichaPerfilDomain evaluacion) {
        evaluacionFichaPerfilOutputPort.registrarEvaluacion(EvaluacionFichaPerfilMapper.toEntity(evaluacion));

        asignarEstadoInicialEvaluacionUseCase.ejecutar(evaluacion);

        logger.info(
                EvaluacionFichaPerfilKey.LOG_REGISTRADA,
                evaluacion.getId(),
                evaluacion.getRepresentanteComiteId(),
                evaluacion.getFichaPerfilId());

        return InicioEvaluacionFichaPerfilResultMapper.toResultRegistrada(evaluacion.getId());
    }
}
