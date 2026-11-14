package com.arquisoft.entregables.application.proyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.artefacto.command.usecase.PurgarArtefactosProyectoGradoUseCase;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.entregables.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.result.mapper.FinalizacionProyectoGradoResultMapper;
import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.entregables.application.proyectogrado.command.usecase.FinalizarProyectoGradoUseCase;
import com.arquisoft.entregables.application.proyectogrado.command.validator.FinalizarProyectoGradoValidator;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.FinalizacionProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinalizarProyectoGradoUseCaseImpl implements FinalizarProyectoGradoUseCase {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final FinalizarProyectoGradoValidator finalizarProyectoGradoValidator;
    private final PurgarArtefactosProyectoGradoUseCase purgarArtefactosProyectoGradoUseCase;
    private final AppLogger logger;

    @Override
    public FinalizacionProyectoGradoResult ejecutar(FinalizacionProyectoGradoDomain entrada) {
        var proyecto = proyectoGradoPorIdFinder.obtener(entrada.getProyectoGrado());
        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_FINALIZAR, entrada.getProyectoGrado(), !proyecto.esVacio());

        finalizarProyectoGradoValidator.validar(
                new ExistenciaProyectoGrado(entrada.getProyectoGrado(), !proyecto.esVacio()));

        var yaFinalizado = proyecto.getEstadoProyecto() == EstadoProyecto.FINALIZADO;
        if (!yaFinalizado) {
            proyectoGradoOutputPort.actualizarEstado(entrada.getProyectoGrado(), entrada.getEstadoNuevo().getId());
        }

        var purgaArtefactos = purgarArtefactosProyectoGradoUseCase.ejecutar(entrada.getPurgaArtefactos());

        if (yaFinalizado) {
            return FinalizacionProyectoGradoResultMapper.toResultDuplicado(entrada, purgaArtefactos);
        }
        return FinalizacionProyectoGradoResultMapper.toResultFinalizado(entrada, purgaArtefactos);
    }
}
