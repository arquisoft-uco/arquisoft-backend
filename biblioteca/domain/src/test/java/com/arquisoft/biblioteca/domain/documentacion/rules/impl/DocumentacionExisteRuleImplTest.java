package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoEncontradaException;
import com.arquisoft.biblioteca.domain.documentacion.model.ExistenciaDocumentacion;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentacionExisteRuleImplTest {

    private final DocumentacionExisteRuleImpl rule = new DocumentacionExisteRuleImpl();

    @Test
    void debePasar_cuandoLaDocumentacionExiste() {
        // Arrange
        var existencia = new ExistenciaDocumentacion(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoLaDocumentacionNoExiste() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaDocumentacion(documentacion, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOfSatisfying(DocumentacionNoEncontradaException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(BibliotecaCodes.Documentacion.NO_ENCONTRADA);
                    assertThat(ex.getError().getMessage()).doesNotContain(documentacion.toString());
                    assertThat(ex.getMessage()).contains(documentacion.toString());
                });
    }
}
