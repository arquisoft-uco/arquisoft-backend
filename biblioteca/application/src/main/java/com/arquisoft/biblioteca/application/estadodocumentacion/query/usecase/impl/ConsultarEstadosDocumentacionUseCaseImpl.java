package com.arquisoft.biblioteca.application.estadodocumentacion.query.usecase.impl;

import com.arquisoft.biblioteca.application.estadodocumentacion.query.readmodel.EstadoDocumentacionReadModel;
import com.arquisoft.biblioteca.application.estadodocumentacion.query.secondaryport.EstadoDocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.application.estadodocumentacion.query.usecase.ConsultarEstadosDocumentacionUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EstadoDocumentacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosDocumentacionUseCaseImpl implements ConsultarEstadosDocumentacionUseCase {

    private final EstadoDocumentacionQueryOutputPort estadoDocumentacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoDocumentacionReadModel> ejecutar() {
        var resultado = estadoDocumentacionQueryOutputPort.findAll();

        logger.debug(EstadoDocumentacionKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
