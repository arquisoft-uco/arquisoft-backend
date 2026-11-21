package com.arquisoft.biblioteca.application.documentacion.query.validator.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DescargaDocumentacionCriteria;
import com.arquisoft.biblioteca.domain.documentacion.exception.ArchivoDocumentacionNoDisponibleException;
import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoDescargableException;
import com.arquisoft.biblioteca.domain.documentacion.model.ArchivoDocumentacion;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescargarDocumentacionValidatorImplTest {

    private final DescargarDocumentacionValidatorImpl validator = new DescargarDocumentacionValidatorImpl();

    private static ArchivoDocumentacion archivo(UUID documentacion, EstadoDocumentacion estado, boolean conUbicacion) {
        return new ArchivoDocumentacion(documentacion, estado,
                conUbicacion ? "p/a.zip" : UtilTexto.VACIO,
                "a.zip");
    }

    @Test
    void debeNoLanzar_cuandoPublicadaConUbicacionOBibliotecarioConPendiente() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var publicada = archivo(documentacion, EstadoDocumentacion.PUBLICADO, true);
        var pendiente = archivo(documentacion, EstadoDocumentacion.PENDIENTE, true);

        // Act & Assert
        assertThatCode(() -> validator.validar(new DescargaDocumentacionCriteria(documentacion, false), publicada))
                .doesNotThrowAnyException();
        assertThatCode(() -> validator.validar(new DescargaDocumentacionCriteria(documentacion, true), pendiente))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoDescargable_cuandoLaDocumentacionNoExisteONoEstaPermitida() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var criteria = new DescargaDocumentacionCriteria(documentacion, false);
        var pendiente = archivo(documentacion, EstadoDocumentacion.PENDIENTE, true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, ArchivoDocumentacion.VACIO))
                .isInstanceOf(DocumentacionNoDescargableException.class);
        assertThatThrownBy(() -> validator.validar(criteria, pendiente))
                .isInstanceOf(DocumentacionNoDescargableException.class);
    }

    @Test
    void debeLanzarNoDescargableAntesQueNoDisponible_cuandoPendienteSinUbicacionYNoBibliotecario() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var criteria = new DescargaDocumentacionCriteria(documentacion, false);
        var pendienteSinUbicacion = archivo(documentacion, EstadoDocumentacion.PENDIENTE, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, pendienteSinUbicacion))
                .isInstanceOf(DocumentacionNoDescargableException.class);
    }

    @Test
    void debeLanzarNoDisponible_cuandoEsDescargablePeroNoTieneUbicacion() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var criteria = new DescargaDocumentacionCriteria(documentacion, false);
        var publicadaSinUbicacion = archivo(documentacion, EstadoDocumentacion.PUBLICADO, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, publicadaSinUbicacion))
                .isInstanceOf(ArchivoDocumentacionNoDisponibleException.class);
    }
}
