package com.arquisoft.biblioteca.application.documentacion.query.validator.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.EnlaceDocumentacionCriteria;
import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoCompartibleException;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEnlaceDocumentacionValidatorImplTest {

    private final ConsultarEnlaceDocumentacionValidatorImpl validator =
            new ConsultarEnlaceDocumentacionValidatorImpl();

    @Test
    void debeNoLanzar_cuandoLaDocumentacionEstaPublicada() {
        // Arrange
        var criteria = new EnlaceDocumentacionCriteria(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> validator.validar(criteria, EstadoDocumentacion.PUBLICADO))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoCompartible_cuandoNoEstaPublicadaOEsVacio() {
        // Arrange
        var criteria = new EnlaceDocumentacionCriteria(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, EstadoDocumentacion.NO_PUBLICADO))
                .isInstanceOf(DocumentacionNoCompartibleException.class);
        assertThatThrownBy(() -> validator.validar(criteria, EstadoDocumentacion.VACIO))
                .isInstanceOf(DocumentacionNoCompartibleException.class);
    }
}
