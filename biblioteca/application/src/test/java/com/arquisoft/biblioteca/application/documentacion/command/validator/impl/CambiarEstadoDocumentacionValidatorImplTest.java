package com.arquisoft.biblioteca.application.documentacion.command.validator.impl;

import com.arquisoft.biblioteca.domain.documentacion.CambioEstadoDocumentacionDomain;
import com.arquisoft.biblioteca.domain.documentacion.DocumentacionDomain;
import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoEncontradaException;
import com.arquisoft.biblioteca.domain.documentacion.exception.EstadoDestinoDocumentacionNoPermitidoException;
import com.arquisoft.biblioteca.domain.documentacion.exception.EstadoDocumentacionSinCambioException;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoDocumentacionValidatorImplTest {

    private final CambiarEstadoDocumentacionValidatorImpl validator = new CambiarEstadoDocumentacionValidatorImpl();

    private DocumentacionDomain guardada(UUID id, EstadoDocumentacion estado) {
        return DocumentacionDomain.reconstruir(id, "https://minio.test/a.zip",
                Instant.parse("2026-11-15T10:00:00Z"), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), estado);
    }

    @ParameterizedTest
    @CsvSource({
            "PENDIENTE, PUBLICADO",
            "PENDIENTE, NO_PUBLICADO",
            "PUBLICADO, NO_PUBLICADO",
            "NO_PUBLICADO, PUBLICADO"
    })
    void debePasar_enLasCuatroTransicionesPermitidas(EstadoDocumentacion actual, EstadoDocumentacion destino) {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoDocumentacionDomain.crear(id, destino.getId());

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, guardada(id, actual))).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoLaDocumentacionEsVacia() {
        // Arrange
        var cambio = CambioEstadoDocumentacionDomain.crear(UtilUUID.generarNuevoUUID(), "PUBLICADO");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, DocumentacionDomain.VACIO))
                .isInstanceOf(DocumentacionNoEncontradaException.class);
    }

    @Test
    void debeLanzarDestinoNoPermitido_cuandoElDestinoEsPendiente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoDocumentacionDomain.crear(id, "PENDIENTE");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, guardada(id, EstadoDocumentacion.PUBLICADO)))
                .isInstanceOf(EstadoDestinoDocumentacionNoPermitidoException.class);
    }

    @Test
    void debeLanzarSinCambio_cuandoElDestinoEsElEstadoActual() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoDocumentacionDomain.crear(id, "NO_PUBLICADO");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, guardada(id, EstadoDocumentacion.NO_PUBLICADO)))
                .isInstanceOf(EstadoDocumentacionSinCambioException.class);
    }

    @Test
    void debePriorizarDestinoNoPermitidoSobreSinCambio_cuandoPendienteAPendiente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoDocumentacionDomain.crear(id, "PENDIENTE");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, guardada(id, EstadoDocumentacion.PENDIENTE)))
                .isInstanceOf(EstadoDestinoDocumentacionNoPermitidoException.class);
    }

    @Test
    void debePriorizarNoEncontradaSobreDestino_cuandoLaDocumentacionNoExisteYElDestinoEsPendiente() {
        // Arrange
        var cambio = CambioEstadoDocumentacionDomain.crear(UtilUUID.generarNuevoUUID(), "PENDIENTE");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, DocumentacionDomain.VACIO))
                .isInstanceOf(DocumentacionNoEncontradaException.class);
    }
}
