package com.arquisoft.entregables.application.entregableproyectogrado.query.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoEstudianteCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.EntregableProyectoGradoQueryOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.query.usecase.ConsultarEntregablesProyectoGradoEstudianteUseCase;
import com.arquisoft.entregables.application.entregableproyectogrado.query.validator.ConsultarEntregablesProyectoGradoEstudianteValidator;
import com.arquisoft.entregables.application.proyectogrado.query.finder.EstudiantePerteneceProyectoGradoQueryFinder;
import com.arquisoft.entregables.application.proyectogrado.query.finder.model.EstudianteProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEntregablesProyectoGradoEstudianteUseCaseImpl
        implements ConsultarEntregablesProyectoGradoEstudianteUseCase {

    private final EstudiantePerteneceProyectoGradoQueryFinder estudiantePerteneceProyectoGradoQueryFinder;
    private final ConsultarEntregablesProyectoGradoEstudianteValidator consultarEntregablesProyectoGradoEstudianteValidator;
    private final EntregableProyectoGradoQueryOutputPort entregableProyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<EntregableProyectoGradoReadModel> ejecutar(
            EntregableProyectoGradoEstudianteCriteria entrada) {
        var consulta = entrada.consulta();
        logger.debug(EntregableProyectoGradoKey.LOG_CONSULTANDO_ESTUDIANTE,
                entrada.proyectoGrado(), consulta.getPagina(), consulta.getTamanio(),
                consulta.tieneFiltros(), consulta.tieneOrden());

        var pertenece = estudiantePerteneceProyectoGradoQueryFinder.obtener(
                new EstudianteProyectoGrado(entrada.estudiante(), entrada.proyectoGrado()));

        consultarEntregablesProyectoGradoEstudianteValidator.validar(
                entrada.estudiante(), entrada.proyectoGrado(), pertenece);

        var resultado = entregableProyectoGradoQueryOutputPort.consultar(consulta);

        logger.debug(EntregableProyectoGradoKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), consulta.getPagina(), consulta.getTamanio());
        return resultado;
    }
}
