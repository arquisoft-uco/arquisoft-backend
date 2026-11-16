package com.arquisoft.biblioteca.application.proyectogrado.command.usecase.impl;

import com.arquisoft.biblioteca.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.mapper.ProyectoGradoMapper;
import com.arquisoft.biblioteca.application.proyectogrado.command.usecase.AgregarProyectoGradoUseCase;
import com.arquisoft.biblioteca.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarProyectoGradoUseCaseImpl implements AgregarProyectoGradoUseCase {

    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final ProyectoGradoOutputPort proyectoGradoOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(ProyectoGradoDomain proyecto) {
        var existente = proyectoGradoPorIdFinder.obtener(proyecto.getId());
        var existia = !existente.esVacio();
        var aplicado = !existia || proyecto.getOcurridoEn().isAfter(existente.getOcurridoEn());
        logger.debug(ProyectoGradoKey.LOG_APLICADO, proyecto.getId(), existia, aplicado);

        if (!aplicado) {
            return;
        }

        if (existia) {
            existente.actualizar(proyecto.getTitulo(), proyecto.getOcurridoEn());
            proyectoGradoOutputPort.guardar(ProyectoGradoMapper.toEntity(existente));
            return;
        }

        proyectoGradoOutputPort.guardar(ProyectoGradoMapper.toEntity(proyecto));
    }
}
