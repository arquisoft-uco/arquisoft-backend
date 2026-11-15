package com.arquisoft.biblioteca.application.documentacion.command.usecase.impl;

import com.arquisoft.biblioteca.application.documentacion.command.finder.DocumentacionPorIdFinder;
import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.DocumentacionOutputPort;
import com.arquisoft.biblioteca.application.documentacion.command.validator.CambiarEstadoDocumentacionValidator;
import com.arquisoft.biblioteca.domain.documentacion.CambioEstadoDocumentacionDomain;
import com.arquisoft.biblioteca.domain.documentacion.DocumentacionDomain;
import com.arquisoft.biblioteca.domain.documentacion.exception.EstadoDocumentacionSinCambioException;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoDocumentacionUseCaseImplTest {

    @Mock
    private DocumentacionPorIdFinder documentacionPorIdFinder;
    @Mock
    private CambiarEstadoDocumentacionValidator cambiarEstadoDocumentacionValidator;
    @Mock
    private DocumentacionOutputPort documentacionOutputPort;
    @Mock
    private AppLogger logger;

    private CambiarEstadoDocumentacionUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CambiarEstadoDocumentacionUseCaseImpl(
                documentacionPorIdFinder, cambiarEstadoDocumentacionValidator, documentacionOutputPort, logger);
    }

    private DocumentacionDomain guardada(UUID id, EstadoDocumentacion estado) {
        return DocumentacionDomain.reconstruir(id, "https://minio.test/a.zip",
                Instant.parse("2026-11-15T10:00:00Z"), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), estado);
    }

    @Test
    void debeActualizarElEstado_enOrdenFinderValidatorPuerto() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoDocumentacionDomain.crear(id, "PUBLICADO");
        var documentacion = guardada(id, EstadoDocumentacion.PENDIENTE);
        when(documentacionPorIdFinder.obtener(id)).thenReturn(documentacion);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(documentacionPorIdFinder, cambiarEstadoDocumentacionValidator,
                documentacionOutputPort, logger);
        orden.verify(documentacionPorIdFinder, times(1)).obtener(id);
        orden.verify(cambiarEstadoDocumentacionValidator).validar(cambio, documentacion);
        orden.verify(documentacionOutputPort).actualizarEstado(id, "PUBLICADO");
        orden.verify(logger).info(DocumentacionKey.LOG_ESTADO_CAMBIADO, id, "PENDIENTE", "PUBLICADO");
    }

    @Test
    void noDebeActualizarNiCerrarElLog_cuandoElValidatorRechaza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoDocumentacionDomain.crear(id, "PUBLICADO");
        var documentacion = guardada(id, EstadoDocumentacion.PUBLICADO);
        when(documentacionPorIdFinder.obtener(id)).thenReturn(documentacion);
        doThrow(new EstadoDocumentacionSinCambioException(id, EstadoDocumentacion.PUBLICADO))
                .when(cambiarEstadoDocumentacionValidator).validar(cambio, documentacion);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(EstadoDocumentacionSinCambioException.class);
        verify(documentacionOutputPort, never()).actualizarEstado(any(), any());
        verify(logger, never()).info(eq(DocumentacionKey.LOG_ESTADO_CAMBIADO), any(), any(), any());
    }
}
