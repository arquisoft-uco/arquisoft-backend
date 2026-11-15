package com.arquisoft.biblioteca.application.documentacion.query.usecase.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DescargaDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.finder.ArchivoDocumentacionQueryFinder;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDescargaDocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.EnlaceDescargaDocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.application.documentacion.query.usecase.DescargarDocumentacionUseCase;
import com.arquisoft.biblioteca.application.documentacion.query.validator.DescargarDocumentacionValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DescargarDocumentacionUseCaseImpl implements DescargarDocumentacionUseCase {

    private final ArchivoDocumentacionQueryFinder archivoDocumentacionQueryFinder;
    private final DescargarDocumentacionValidator descargarDocumentacionValidator;
    private final EnlaceDescargaDocumentacionQueryOutputPort enlaceDescargaDocumentacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public EnlaceDescargaDocumentacionReadModel ejecutar(DescargaDocumentacionCriteria entrada) {
        logger.debug(DocumentacionKey.LOG_DESCARGANDO, entrada.documentacion(), entrada.comoBibliotecario());

        var archivo = archivoDocumentacionQueryFinder.obtener(entrada.documentacion());

        logger.debug(DocumentacionKey.LOG_VERIFICACION_DESCARGA, !archivo.esVacio(), archivo.estado().getId());

        descargarDocumentacionValidator.validar(entrada, archivo);

        var enlace = enlaceDescargaDocumentacionQueryOutputPort.generar(
                archivo.documentacion(), archivo.bucket(), archivo.claveObjeto(), archivo.nombreArchivo());

        logger.debug(DocumentacionKey.LOG_DESCARGA_GENERADA, entrada.documentacion(), enlace.expiraEn());
        return enlace;
    }
}
