package com.arquisoft.artefactos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.result.DescongelacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.result.mapper.DescongelacionProyectoGradoResultMapper;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.DescongelarProyectoGradoUseCase;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.DescongelarProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.DescongelacionProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DescongelarProyectoGradoUseCaseImpl implements DescongelarProyectoGradoUseCase {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final DescongelarProyectoGradoValidator descongelarProyectoGradoValidator;
    private final AppLogger logger;

    @Override
    public DescongelacionProyectoGradoResult ejecutar(DescongelacionProyectoGradoDomain descongelacion) {
        var proyecto = proyectoGradoPorIdFinder.obtener(descongelacion.getProyectoGrado());
        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_DESCONGELAR, !proyecto.esVacio(), proyecto.getEntregadoEn());

        descongelarProyectoGradoValidator.validar(
                new ExistenciaProyectoGrado(descongelacion.getProyectoGrado(), !proyecto.esVacio()));

        var congeladoPorEstaEntrega = proyecto.estaEntregado()
                && !proyecto.getEntregadoEn().isAfter(descongelacion.getCanceladoEn());
        if (!congeladoPorEstaEntrega) {
            return DescongelacionProyectoGradoResultMapper.toResultOmitido(descongelacion);
        }

        proyectoGradoOutputPort.limpiarEntregado(descongelacion.getProyectoGrado(), descongelacion.getCanceladoEn());
        return DescongelacionProyectoGradoResultMapper.toResultDescongelado(descongelacion);
    }
}
