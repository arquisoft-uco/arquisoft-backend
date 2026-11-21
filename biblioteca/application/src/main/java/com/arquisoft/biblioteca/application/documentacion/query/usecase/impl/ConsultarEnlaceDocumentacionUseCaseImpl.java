package com.arquisoft.biblioteca.application.documentacion.query.usecase.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.EnlaceDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.finder.ArchivoDocumentacionQueryFinder;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDescargaDocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.EnlaceDescargaDocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.application.documentacion.query.usecase.ConsultarEnlaceDocumentacionUseCase;
import com.arquisoft.biblioteca.application.documentacion.query.validator.ConsultarEnlaceDocumentacionValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEnlaceDocumentacionUseCaseImpl implements ConsultarEnlaceDocumentacionUseCase {

    private final ArchivoDocumentacionQueryFinder archivoDocumentacionQueryFinder;
    private final ConsultarEnlaceDocumentacionValidator consultarEnlaceDocumentacionValidator;
    private final EnlaceDescargaDocumentacionQueryOutputPort enlaceDescargaDocumentacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public EnlaceDescargaDocumentacionReadModel ejecutar(EnlaceDocumentacionCriteria entrada) {
        logger.debug(DocumentacionKey.LOG_CONSULTANDO_ENLACE, entrada.documentacion());

        var archivo = archivoDocumentacionQueryFinder.obtener(entrada.documentacion());

        logger.debug(DocumentacionKey.LOG_VERIFICACION_ENLACE, !archivo.esVacio(), archivo.estado().getId());

        consultarEnlaceDocumentacionValidator.validar(entrada, archivo);

        var enlace = enlaceDescargaDocumentacionQueryOutputPort.generar(
                archivo.documentacion(), archivo.claveObjeto(), archivo.nombreArchivo());

        logger.debug(DocumentacionKey.LOG_ENLACE_GENERADO, entrada.documentacion(), enlace.expiraEn());
        return enlace;
    }
}
