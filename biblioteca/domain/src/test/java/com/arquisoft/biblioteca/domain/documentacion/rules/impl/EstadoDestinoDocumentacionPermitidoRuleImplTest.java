package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.EstadoDestinoDocumentacionNoPermitidoException;
import com.arquisoft.biblioteca.domain.documentacion.model.TransicionEstadoDocumentacion;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoDestinoDocumentacionPermitidoRuleImplTest {

    private final EstadoDestinoDocumentacionPermitidoRuleImpl rule =
            new EstadoDestinoDocumentacionPermitidoRuleImpl();

    @ParameterizedTest
    @EnumSource(value = EstadoDocumentacion.class, names = {"PUBLICADO", "NO_PUBLICADO"})
    void debePasar_cuandoElDestinoEsPublicadoONoPublicado(EstadoDocumentacion destino) {
        // Arrange
        var transicion = new TransicionEstadoDocumentacion(
                UtilUUID.generarNuevoUUID(), EstadoDocumentacion.PENDIENTE, destino);

        // Act & Assert
        assertThatCode(() -> rule.validar(transicion)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoDocumentacion.class, names = {"PENDIENTE", "VACIO"})
    void debeLanzarDestinoNoPermitido_cuandoElDestinoNoEsPublicadoNiNoPublicado(EstadoDocumentacion destino) {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var transicion = new TransicionEstadoDocumentacion(documentacion, EstadoDocumentacion.PUBLICADO, destino);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(transicion))
                .isInstanceOfSatisfying(EstadoDestinoDocumentacionNoPermitidoException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(BibliotecaCodes.Documentacion.ESTADO_DESTINO_NO_PERMITIDO);
                    assertThat(ex.getError().getMessage()).doesNotContain(documentacion.toString());
                    assertThat(ex.getMessage()).contains(documentacion.toString());
                });
    }
}
