package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoCompartibleException;
import com.arquisoft.biblioteca.domain.documentacion.model.EstadoPublicacionDocumentacion;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentacionCompartibleRuleImplTest {

    private final DocumentacionCompartibleRuleImpl rule = new DocumentacionCompartibleRuleImpl();

    @Test
    void debeNoLanzar_cuandoEstadoEsPublicado() {
        // Arrange
        var entrada = new EstadoPublicacionDocumentacion(UtilUUID.generarNuevoUUID(), EstadoDocumentacion.PUBLICADO);

        // Act & Assert
        assertThatCode(() -> rule.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoCompartible_cuandoEstadoNoEsPublicadoOEsVacio() {
        // Arrange
        var estados = new EstadoDocumentacion[] {
                EstadoDocumentacion.PENDIENTE, EstadoDocumentacion.NO_PUBLICADO, EstadoDocumentacion.VACIO};

        for (var estado : estados) {
            var entrada = new EstadoPublicacionDocumentacion(UtilUUID.generarNuevoUUID(), estado);

            // Act & Assert
            assertThatThrownBy(() -> rule.validar(entrada))
                    .isInstanceOf(DocumentacionNoCompartibleException.class)
                    .extracting("codigoError")
                    .isEqualTo(BibliotecaCodes.Documentacion.NO_COMPARTIBLE);
        }
    }
}
