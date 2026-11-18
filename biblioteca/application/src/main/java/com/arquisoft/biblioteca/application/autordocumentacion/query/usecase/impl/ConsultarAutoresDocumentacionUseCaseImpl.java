package com.arquisoft.biblioteca.application.autordocumentacion.query.usecase.impl;

import com.arquisoft.biblioteca.application.autordocumentacion.query.criteria.AutorDocumentacionCriteria;
import com.arquisoft.biblioteca.application.autordocumentacion.query.readmodel.AutorDocumentacionReadModel;
import com.arquisoft.biblioteca.application.autordocumentacion.query.secondaryport.AutorDocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.application.autordocumentacion.query.usecase.ConsultarAutoresDocumentacionUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.AutorDocumentacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarAutoresDocumentacionUseCaseImpl implements ConsultarAutoresDocumentacionUseCase {

    private final AutorDocumentacionQueryOutputPort autorDocumentacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<AutorDocumentacionReadModel> ejecutar(AutorDocumentacionCriteria entrada) {
        logger.debug(AutorDocumentacionKey.LOG_CONSULTANDO, entrada.documentacion());

        var autores = autorDocumentacionQueryOutputPort.consultarPorDocumentacion(entrada.documentacion());

        logger.debug(AutorDocumentacionKey.LOG_CONSULTA_COMPLETADA, autores.size());
        return autores;
    }
}
