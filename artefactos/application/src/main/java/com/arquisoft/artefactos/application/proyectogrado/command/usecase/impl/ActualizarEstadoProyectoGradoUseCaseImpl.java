package com.arquisoft.artefactos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.result.mapper.ActualizacionEstadoProyectoGradoResultMapper;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.ActualizarEstadoProyectoGradoUseCase;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.ActualizarEstadoProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.ActualizacionEstadoProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActualizarEstadoProyectoGradoUseCaseImpl implements ActualizarEstadoProyectoGradoUseCase {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final ActualizarEstadoProyectoGradoValidator actualizarEstadoProyectoGradoValidator;
    private final AppLogger logger;

    @Override
    public ActualizacionEstadoProyectoGradoResult ejecutar(ActualizacionEstadoProyectoGradoDomain actualizacion) {
        var proyecto = proyectoGradoPorIdFinder.obtener(actualizacion.getProyectoGrado());
        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_ACTUALIZAR_ESTADO,
                !proyecto.esVacio(), proyecto.getOcurridoEn());

        actualizarEstadoProyectoGradoValidator.validar(
                new ExistenciaProyectoGrado(actualizacion.getProyectoGrado(), !proyecto.esVacio()));

        if (!actualizacion.getOcurridoEn().isAfter(proyecto.getOcurridoEn())) {
            return ActualizacionEstadoProyectoGradoResultMapper.toResultDescartado(
                    actualizacion, proyecto.getOcurridoEn());
        }

        proyectoGradoOutputPort.actualizarEstado(
                actualizacion.getProyectoGrado(),
                actualizacion.getEstadoProyecto().getId(),
                actualizacion.getOcurridoEn());
        return ActualizacionEstadoProyectoGradoResultMapper.toResultActualizado(actualizacion);
    }
}
