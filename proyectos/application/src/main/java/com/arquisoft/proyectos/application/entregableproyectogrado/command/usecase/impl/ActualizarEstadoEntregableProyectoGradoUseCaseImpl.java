package com.arquisoft.proyectos.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.finder.EntregableProyectoGradoPorIdFinder;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.result.ActualizacionEstadoEntregableProyectoGradoResult;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.result.mapper.ActualizacionEstadoEntregableProyectoGradoResultMapper;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.mapper.EntregableProyectoGradoMapper;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.usecase.ActualizarEstadoEntregableProyectoGradoUseCase;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EntregableProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActualizarEstadoEntregableProyectoGradoUseCaseImpl
        implements ActualizarEstadoEntregableProyectoGradoUseCase {

    private final EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;
    private final EntregableProyectoGradoPorIdFinder entregableProyectoGradoPorIdFinder;
    private final AppLogger logger;

    @Override
    public ActualizacionEstadoEntregableProyectoGradoResult ejecutar(EntregableProyectoGradoDomain entrada) {
        var vigente = entregableProyectoGradoPorIdFinder.obtener(entrada.getId());
        logger.debug(EntregableProyectoGradoKey.LOG_VERIFICACION_ACTUALIZAR, entrada.getId(), !vigente.esVacio());

        if (!vigente.esVacio() && !entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return ActualizacionEstadoEntregableProyectoGradoResultMapper.toResultDescartado(
                    entrada, vigente.getOcurridoEn());
        }

        entregableProyectoGradoOutputPort.guardar(EntregableProyectoGradoMapper.toEntity(entrada));
        return ActualizacionEstadoEntregableProyectoGradoResultMapper.toResultActualizado(entrada);
    }
}
