package com.arquisoft.biblioteca.application.documentacion.query.usecase.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.DocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.application.documentacion.query.usecase.ConsultarDocumentacionesUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarDocumentacionesUseCaseImpl implements ConsultarDocumentacionesUseCase {

    private final DocumentacionQueryOutputPort documentacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<DocumentacionReadModel> ejecutar(DocumentacionCriteria entrada) {
        logger.debug(DocumentacionKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = documentacionQueryOutputPort.consultar(entrada);

        logger.debug(DocumentacionKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
