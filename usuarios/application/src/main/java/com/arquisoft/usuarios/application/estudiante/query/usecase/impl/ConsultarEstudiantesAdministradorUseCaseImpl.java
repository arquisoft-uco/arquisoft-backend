package com.arquisoft.usuarios.application.estudiante.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarEstudiantesAdministradorKey;
import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteCriteria;
import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteReadModel;
import com.arquisoft.usuarios.application.estudiante.query.secondaryport.EstudianteQueryOutputPort;
import com.arquisoft.usuarios.application.estudiante.query.usecase.ConsultarEstudiantesAdministradorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEstudiantesAdministradorUseCaseImpl implements ConsultarEstudiantesAdministradorUseCase {

    private final EstudianteQueryOutputPort estudianteQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<EstudianteReadModel> ejecutar(EstudianteCriteria entrada) {
        logger.debug(ConsultarEstudiantesAdministradorKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = estudianteQueryOutputPort.consultarTodos(entrada);

        logger.debug(ConsultarEstudiantesAdministradorKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
