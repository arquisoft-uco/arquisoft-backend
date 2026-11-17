package com.arquisoft.biblioteca.application.documentacion.command.usecase.impl;

import com.arquisoft.biblioteca.application.documentacion.command.finder.DocumentacionPorIdFinder;
import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.DocumentacionOutputPort;
import com.arquisoft.biblioteca.application.documentacion.command.usecase.CambiarEstadoDocumentacionUseCase;
import com.arquisoft.biblioteca.application.documentacion.command.validator.CambiarEstadoDocumentacionValidator;
import com.arquisoft.biblioteca.domain.documentacion.CambioEstadoDocumentacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarEstadoDocumentacionUseCaseImpl implements CambiarEstadoDocumentacionUseCase {

    private final DocumentacionPorIdFinder documentacionPorIdFinder;
    private final CambiarEstadoDocumentacionValidator cambiarEstadoDocumentacionValidator;
    private final DocumentacionOutputPort documentacionOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioEstadoDocumentacionDomain cambio) {
        logger.info(DocumentacionKey.LOG_CAMBIANDO_ESTADO,
                cambio.getDocumentacion(), cambio.getEstado().getId());

        var documentacion = documentacionPorIdFinder.obtener(cambio.getDocumentacion());
        logger.debug(DocumentacionKey.LOG_VERIFICACION_CAMBIAR_ESTADO,
                !documentacion.esVacio(), documentacion.getEstadoDocumentacion().getId());

        cambiarEstadoDocumentacionValidator.validar(cambio, documentacion);

        documentacionOutputPort.actualizarEstado(cambio.getDocumentacion(), cambio.getEstado().getId());

        logger.info(DocumentacionKey.LOG_ESTADO_CAMBIADO,
                cambio.getDocumentacion(), documentacion.getEstadoDocumentacion().getId(),
                cambio.getEstado().getId());
    }
}
