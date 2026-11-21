package com.arquisoft.biblioteca.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.biblioteca.domain.entregableproyectogrado.exception.ArchivoEntregableNoDisponibleException;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.model.UbicacionArchivoEntregable;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArchivoEntregableDisponibleRuleImplTest {

    private final ArchivoEntregableDisponibleRuleImpl rule = new ArchivoEntregableDisponibleRuleImpl();

    @Test
    void debePasar_cuandoLaClaveEstaPresente() {
        // Arrange
        var ubicacion = new UbicacionArchivoEntregable(UtilUUID.generarNuevoUUID(), "a/b.zip");

        // Act & Assert
        assertThatCode(() -> rule.validar(ubicacion)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void debeLanzarNoDisponible_cuandoFaltaLaClave(String clave) {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var ubicacion = new UbicacionArchivoEntregable(entregable, clave);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(ubicacion))
                .isInstanceOfSatisfying(ArchivoEntregableNoDisponibleException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(BibliotecaCodes.EntregableProyectoGrado.ARCHIVO_NO_DISPONIBLE);
                    assertThat(ex.getError().getMessage()).doesNotContain(entregable.toString());
                });
    }
}
