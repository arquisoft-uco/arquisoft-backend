package com.arquisoft.proyectos.application.proyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.ProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarProyectosGradoAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarProyectosGradoAsesorUseCaseImpl implements ConsultarProyectosGradoAsesorUseCase {

    private final ProyectoGradoQueryOutputPort proyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<ProyectoGradoReadModel> ejecutar(ProyectoGradoAsesorCriteria entrada) {
        logger.debug(ProyectoGradoKey.LOG_CONSULTANDO_ASESOR,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = proyectoGradoQueryOutputPort.consultarPorAsesor(entrada);

        logger.debug(ProyectoGradoKey.LOG_CONSULTA_ASESOR_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
