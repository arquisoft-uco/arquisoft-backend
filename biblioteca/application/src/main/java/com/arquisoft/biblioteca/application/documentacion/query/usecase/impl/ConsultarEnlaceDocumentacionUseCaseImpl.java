package com.arquisoft.biblioteca.application.documentacion.query.usecase.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.EnlaceDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.finder.EstadoDeDocumentacionQueryFinder;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.DocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.EnlaceDocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.application.documentacion.query.usecase.ConsultarEnlaceDocumentacionUseCase;
import com.arquisoft.biblioteca.application.documentacion.query.validator.ConsultarEnlaceDocumentacionValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEnlaceDocumentacionUseCaseImpl implements ConsultarEnlaceDocumentacionUseCase {

    private final EstadoDeDocumentacionQueryFinder estadoDeDocumentacionQueryFinder;
    private final ConsultarEnlaceDocumentacionValidator consultarEnlaceDocumentacionValidator;
    private final DocumentacionQueryOutputPort documentacionQueryOutputPort;
    private final EnlaceDocumentacionQueryOutputPort enlaceDocumentacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public EnlaceDocumentacionReadModel ejecutar(EnlaceDocumentacionCriteria entrada) {
        logger.debug(DocumentacionKey.LOG_CONSULTANDO_ENLACE, entrada.documentacion());

        var estado = estadoDeDocumentacionQueryFinder.obtener(entrada.documentacion());

        logger.debug(DocumentacionKey.LOG_VERIFICACION_ENLACE, entrada.documentacion(), estado.getId());

        consultarEnlaceDocumentacionValidator.validar(entrada, estado);

        var documentacion = documentacionQueryOutputPort.consultarPorId(entrada.documentacion());
        var enlace = enlaceDocumentacionQueryOutputPort.generar(entrada.documentacion());

        logger.debug(DocumentacionKey.LOG_ENLACE_GENERADO, entrada.documentacion());
        return new EnlaceDocumentacionReadModel(
                enlace,
                documentacion.id(),
                documentacion.entregableProyectoGrado(),
                documentacion.bibliotecario(),
                documentacion.estadoDocumentacion());
    }
}
