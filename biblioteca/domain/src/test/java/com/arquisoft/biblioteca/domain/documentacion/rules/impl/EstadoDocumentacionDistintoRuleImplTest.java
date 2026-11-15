package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.EstadoDocumentacionSinCambioException;
import com.arquisoft.biblioteca.domain.documentacion.model.TransicionEstadoDocumentacion;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoDocumentacionDistintoRuleImplTest {

    private final EstadoDocumentacionDistintoRuleImpl rule = new EstadoDocumentacionDistintoRuleImpl();

    @Test
    void debePasar_cuandoElDestinoDifiereDelActual() {
        // Arrange
        var transicion = new TransicionEstadoDocumentacion(
                UtilUUID.generarNuevoUUID(), EstadoDocumentacion.PUBLICADO, EstadoDocumentacion.NO_PUBLICADO);

        // Act & Assert
        assertThatCode(() -> rule.validar(transicion)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSinCambio_cuandoElDestinoEsIgualAlActual() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var transicion = new TransicionEstadoDocumentacion(
                documentacion, EstadoDocumentacion.PUBLICADO, EstadoDocumentacion.PUBLICADO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(transicion))
                .isInstanceOfSatisfying(EstadoDocumentacionSinCambioException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(BibliotecaCodes.Documentacion.ESTADO_SIN_CAMBIO);
                    assertThat(ex.getError().getMessage()).doesNotContain(documentacion.toString());
                    assertThat(ex.getMessage()).contains(documentacion.toString());
                });
    }
}
