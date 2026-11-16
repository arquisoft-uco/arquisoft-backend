package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionYaRegistradaException;
import com.arquisoft.biblioteca.domain.documentacion.model.UnicidadDocumentacionEntregable;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentacionDelEntregableUnicaRuleImplTest {

    private final DocumentacionDelEntregableUnicaRuleImpl rule = new DocumentacionDelEntregableUnicaRuleImpl();

    @Test
    void debeLanzarYaRegistrada_cuandoElEntregableYaTieneDocumentacion() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> rule.validar(new UnicidadDocumentacionEntregable(entregable, false)))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> rule.validar(new UnicidadDocumentacionEntregable(entregable, true)))
                .isInstanceOfSatisfying(DocumentacionYaRegistradaException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(BibliotecaCodes.Documentacion.YA_REGISTRADA);
                    assertThat(ex.getError().getMessage()).doesNotContain(entregable.toString());
                    assertThat(ex.getMessage()).contains(entregable.toString());
                });
    }
}
