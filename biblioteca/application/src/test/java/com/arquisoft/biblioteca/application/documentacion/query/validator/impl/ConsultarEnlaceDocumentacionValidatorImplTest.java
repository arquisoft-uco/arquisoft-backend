package com.arquisoft.biblioteca.application.documentacion.query.validator.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.EnlaceDocumentacionCriteria;
import com.arquisoft.biblioteca.domain.documentacion.exception.ArchivoDocumentacionNoDisponibleException;
import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoCompartibleException;
import com.arquisoft.biblioteca.domain.documentacion.model.ArchivoDocumentacion;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEnlaceDocumentacionValidatorImplTest {

    private final ConsultarEnlaceDocumentacionValidatorImpl validator =
            new ConsultarEnlaceDocumentacionValidatorImpl();

    private static ArchivoDocumentacion archivo(UUID documentacion, EstadoDocumentacion estado, boolean conUbicacion) {
        return new ArchivoDocumentacion(documentacion, estado,
                conUbicacion ? "p/a.zip" : UtilTexto.VACIO,
                "a.zip");
    }

    @Test
    void debeNoLanzar_cuandoPublicadaConUbicacion() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var criteria = new EnlaceDocumentacionCriteria(documentacion);
        var publicada = archivo(documentacion, EstadoDocumentacion.PUBLICADO, true);

        // Act & Assert
        assertThatCode(() -> validator.validar(criteria, publicada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoCompartible_cuandoNoExisteONoEstaPublicada() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var criteria = new EnlaceDocumentacionCriteria(documentacion);
        var pendiente = archivo(documentacion, EstadoDocumentacion.PENDIENTE, true);
        var noPublicada = archivo(documentacion, EstadoDocumentacion.NO_PUBLICADO, true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, ArchivoDocumentacion.VACIO))
                .isInstanceOf(DocumentacionNoCompartibleException.class);
        assertThatThrownBy(() -> validator.validar(criteria, pendiente))
                .isInstanceOf(DocumentacionNoCompartibleException.class);
        assertThatThrownBy(() -> validator.validar(criteria, noPublicada))
                .isInstanceOf(DocumentacionNoCompartibleException.class);
    }

    @Test
    void debeLanzarNoCompartibleAntesQueNoDisponible_cuandoPendienteSinUbicacion() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var criteria = new EnlaceDocumentacionCriteria(documentacion);
        var pendienteSinUbicacion = archivo(documentacion, EstadoDocumentacion.PENDIENTE, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, pendienteSinUbicacion))
                .isInstanceOf(DocumentacionNoCompartibleException.class);
    }

    @Test
    void debeLanzarNoDisponible_cuandoPublicadaSinUbicacion() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var criteria = new EnlaceDocumentacionCriteria(documentacion);
        var publicadaSinUbicacion = archivo(documentacion, EstadoDocumentacion.PUBLICADO, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, publicadaSinUbicacion))
                .isInstanceOf(ArchivoDocumentacionNoDisponibleException.class);
    }
}
