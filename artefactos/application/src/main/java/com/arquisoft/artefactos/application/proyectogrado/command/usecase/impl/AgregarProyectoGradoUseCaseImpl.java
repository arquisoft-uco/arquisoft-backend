package com.arquisoft.artefactos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.result.AgregacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.result.mapper.AgregacionProyectoGradoResultMapper;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.mapper.ProyectoGradoMapper;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.AgregarProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarProyectoGradoUseCaseImpl implements AgregarProyectoGradoUseCase {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;
    private final ProyectoGradoExisteFinder proyectoGradoExisteFinder;
    private final AppLogger logger;

    @Override
    public AgregacionProyectoGradoResult ejecutar(ProyectoGradoDomain proyecto) {
        var existe = proyectoGradoExisteFinder.obtener(proyecto.getId());
        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_AGREGAR, proyecto.getId(), existe);

        if (existe) {
            return AgregacionProyectoGradoResultMapper.toResultDuplicado(proyecto);
        }

        proyectoGradoOutputPort.guardar(ProyectoGradoMapper.toEntity(proyecto));
        return AgregacionProyectoGradoResultMapper.toResultAgregado(proyecto);
    }
}
