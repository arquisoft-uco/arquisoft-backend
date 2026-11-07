package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.impl.RegistrarItemCuantitativoAsesorValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.CategoriaItemCuantitativoAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.NombreItemCuantitativoAsesorDuplicadoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarItemCuantitativoAsesorValidatorTest {

    private final RegistrarItemCuantitativoAsesorValidatorImpl validator =
            new RegistrarItemCuantitativoAsesorValidatorImpl();

    @Test
    void noDebeLanzar_cuandoCategoriaExisteYNombreEstaLibre() {
        // Arrange
        var item = itemValido();

        // Act & Assert
        assertThatCode(() -> validator.validar(item, true, false)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarCategoriaNoEncontrada_cuandoNoExiste() {
        // Arrange
        var item = itemValido();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(item, false, false))
                .isInstanceOf(CategoriaItemCuantitativoAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzarNombreDuplicado_cuandoYaExisteEnLaCategoria() {
        // Arrange
        var item = itemValido();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(item, true, true))
                .isInstanceOf(NombreItemCuantitativoAsesorDuplicadoException.class);
    }

    @Test
    void debePriorizarCategoria_cuandoFallanAmbasReglas() {
        // Arrange
        var item = itemValido();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(item, false, true))
                .isInstanceOf(CategoriaItemCuantitativoAsesorNoEncontradaException.class);
    }

    private static ItemCuantitativoAsesorDomain itemValido() {
        return ItemCuantitativoAsesorDomain.crear("Avance", "Porcentaje", UtilUUID.generarNuevoUUID(), 80);
    }
}
