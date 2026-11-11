package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.AsesorDeEntregableProyectoGradoQueryFinder;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.criteria.EstadoEntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.readmodel.EstadoEntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.EstadoEntregableProyectoGradoQueryOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.usecase.ConsultarEstadosEntregableProyectoGradoUseCase;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.validator.ConsultarEstadosEntregableProyectoGradoValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EstadoEntregableProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosEntregableProyectoGradoUseCaseImpl
        implements ConsultarEstadosEntregableProyectoGradoUseCase {

    private final EstadoEntregableProyectoGradoQueryOutputPort estadoEntregableProyectoGradoQueryOutputPort;
    private final AsesorDeEntregableProyectoGradoQueryFinder asesorDeEntregableProyectoGradoQueryFinder;
    private final ConsultarEstadosEntregableProyectoGradoValidator consultarEstadosEntregableProyectoGradoValidator;
    private final AppLogger logger;

    @Override
    public List<EstadoEntregableProyectoGradoReadModel> ejecutar(EstadoEntregableProyectoGradoCriteria entrada) {
        logger.debug(EstadoEntregableProyectoGradoKey.LOG_CONSULTANDO_HISTORIAL, entrada.entregableProyectoGrado());

        var asesorAsignado = asesorDeEntregableProyectoGradoQueryFinder.obtener(entrada.entregableProyectoGrado());

        consultarEstadosEntregableProyectoGradoValidator.validar(
                entrada.entregableProyectoGrado(), asesorAsignado, entrada.asesor());

        var estados = estadoEntregableProyectoGradoQueryOutputPort
                .consultarPorEntregable(entrada.entregableProyectoGrado());

        logger.debug(EstadoEntregableProyectoGradoKey.LOG_HISTORIAL_CONSULTADO,
                entrada.entregableProyectoGrado(), estados.size());
        return estados;
    }
}
