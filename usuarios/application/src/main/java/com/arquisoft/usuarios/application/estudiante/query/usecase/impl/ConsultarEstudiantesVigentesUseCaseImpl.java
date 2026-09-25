package com.arquisoft.usuarios.application.estudiante.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarEstudiantesVigentesKey;
import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteVigenteCriteria;
import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteVigenteReadModel;
import com.arquisoft.usuarios.application.estudiante.query.secondaryport.EstudianteQueryOutputPort;
import com.arquisoft.usuarios.application.estudiante.query.usecase.ConsultarEstudiantesVigentesUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEstudiantesVigentesUseCaseImpl implements ConsultarEstudiantesVigentesUseCase {

    private final EstudianteQueryOutputPort estudianteQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<EstudianteVigenteReadModel> ejecutar(EstudianteVigenteCriteria entrada) {
        logger.debug(ConsultarEstudiantesVigentesKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = estudianteQueryOutputPort.consultarVigentes(entrada);

        logger.debug(ConsultarEstudiantesVigentesKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
