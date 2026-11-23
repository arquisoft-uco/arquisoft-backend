package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.ArchivoDocumentacionNoDisponibleException;
import com.arquisoft.biblioteca.domain.documentacion.model.ArchivoDocumentacion;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArchivoDocumentacionDisponibleRuleImplTest {

    private final ArchivoDocumentacionDisponibleRuleImpl rule = new ArchivoDocumentacionDisponibleRuleImpl();

    private static ArchivoDocumentacion archivo(UUID documentacion, String clave) {
        return new ArchivoDocumentacion(documentacion, EstadoDocumentacion.PUBLICADO, clave, "paquete.zip");
    }

    @Test
    void debeNoLanzar_cuandoLaClaveEstaInformada() {
        // Arrange
        var archivo = archivo(UtilUUID.generarNuevoUUID(), "paquetes/a.zip");

        // Act & Assert
        assertThatCode(() -> rule.validar(archivo)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void debeLanzarNoDisponible_cuandoLaClaveNoEstaInformada(String clave) {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(archivo(documentacion, clave)))
                .isInstanceOfSatisfying(ArchivoDocumentacionNoDisponibleException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(BibliotecaCodes.Documentacion.ARCHIVO_NO_DISPONIBLE);
                    assertThat(ex.getError().getMessage()).doesNotContain(documentacion.toString());
                    assertThat(ex.getMessage()).contains(documentacion.toString());
                });
    }
}
