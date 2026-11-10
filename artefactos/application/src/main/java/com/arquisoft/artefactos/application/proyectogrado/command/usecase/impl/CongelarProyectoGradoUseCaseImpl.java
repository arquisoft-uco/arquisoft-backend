package com.arquisoft.artefactos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.result.CongelacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.result.mapper.CongelacionProyectoGradoResultMapper;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.CongelarProyectoGradoUseCase;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.CongelarProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.CongelacionProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CongelarProyectoGradoUseCaseImpl implements CongelarProyectoGradoUseCase {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final CongelarProyectoGradoValidator congelarProyectoGradoValidator;
    private final AppLogger logger;

    @Override
    public CongelacionProyectoGradoResult ejecutar(CongelacionProyectoGradoDomain congelacion) {
        var proyecto = proyectoGradoPorIdFinder.obtener(congelacion.getProyectoGrado());
        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_CONGELAR, !proyecto.esVacio(), proyecto.estaEntregado());

        congelarProyectoGradoValidator.validar(
                new ExistenciaProyectoGrado(congelacion.getProyectoGrado(), !proyecto.esVacio()));

        var yaCongelado = proyecto.estaEntregado()
                && !proyecto.getEntregadoEn().isBefore(congelacion.getEntregadoEn());
        if (yaCongelado) {
            return CongelacionProyectoGradoResultMapper.toResultYaCongelado(congelacion);
        }

        proyectoGradoOutputPort.marcarEntregado(congelacion.getProyectoGrado(), congelacion.getEntregadoEn());
        return CongelacionProyectoGradoResultMapper.toResultCongelado(congelacion);
    }
}
