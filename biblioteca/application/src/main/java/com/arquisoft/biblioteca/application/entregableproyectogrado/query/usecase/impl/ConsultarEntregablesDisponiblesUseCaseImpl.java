package com.arquisoft.biblioteca.application.entregableproyectogrado.query.usecase.impl;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.criteria.EntregableDisponibleCriteria;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel.EntregableDisponibleReadModel;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.secondaryport.EntregableDisponibleQueryOutputPort;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.usecase.ConsultarEntregablesDisponiblesUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EntregableProyectoGradoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEntregablesDisponiblesUseCaseImpl implements ConsultarEntregablesDisponiblesUseCase {

    private final EntregableDisponibleQueryOutputPort entregableDisponibleQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<EntregableDisponibleReadModel> ejecutar(EntregableDisponibleCriteria entrada) {
        logger.debug(EntregableProyectoGradoKey.LOG_CONSULTANDO_DISPONIBLES,
                entrada.getPagina(), entrada.getTamanio(), entrada.tieneFiltros());

        var resultado = entregableDisponibleQueryOutputPort.consultar(entrada);

        logger.debug(EntregableProyectoGradoKey.LOG_CONSULTA_DISPONIBLES_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
