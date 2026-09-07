package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.impl.RegistrarItemCuantitativoJuradoValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.CategoriaItemCuantitativoJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.NombreItemCuantitativoJuradoDuplicadoException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarItemCuantitativoJuradoValidatorTest {

    private final RegistrarItemCuantitativoJuradoValidatorImpl validator =
            new RegistrarItemCuantitativoJuradoValidatorImpl();

    @Test
    void debeAceptarItemValido() {
        assertThatCode(() -> validator.validar(itemValido(), true, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debePriorizarCategoriaInexistente() {
        assertThatThrownBy(() -> validator.validar(itemValido(), false, true))
                .isInstanceOf(CategoriaItemCuantitativoJuradoNoEncontradaException.class);
    }

    @Test
    void debeRechazarNombreDuplicado() {
        assertThatThrownBy(() -> validator.validar(itemValido(), true, true))
                .isInstanceOf(NombreItemCuantitativoJuradoDuplicadoException.class);
    }

    private static ItemCuantitativoJuradoDomain itemValido() {
        return ItemCuantitativoJuradoDomain.crear(
                "Calidad", "Descripción", UUID.randomUUID(), 100);
    }
}
