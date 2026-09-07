package com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules;

import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.CategoriaItemCuantitativoJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.NombreItemCuantitativoJuradoDuplicadoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model.DisponibilidadNombreItemCuantitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model.ExistenciaCategoriaItemCuantitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.impl.CategoriaItemCuantitativoJuradoExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.impl.NombreItemCuantitativoJuradoPorCategoriaUnicoRuleImpl;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemCuantitativoJuradoRulesTest {

    private static final UUID CATEGORIA = UUID.randomUUID();

    @Test
    void debeAceptarCategoriaExistenteYNombreDisponible() {
        // Arrange
        var categoriaRule = new CategoriaItemCuantitativoJuradoExistenteRuleImpl();
        var nombreRule = new NombreItemCuantitativoJuradoPorCategoriaUnicoRuleImpl();

        // Act & Assert
        assertThatCode(() -> {
            categoriaRule.validar(new ExistenciaCategoriaItemCuantitativoJurado(CATEGORIA, true));
            nombreRule.validar(new DisponibilidadNombreItemCuantitativoJurado(
                    "Calidad", CATEGORIA, false));
        }).doesNotThrowAnyException();
    }

    @Test
    void debeRechazarCategoriaInexistente() {
        // Act & Assert
        assertThatThrownBy(() -> new CategoriaItemCuantitativoJuradoExistenteRuleImpl()
                .validar(new ExistenciaCategoriaItemCuantitativoJurado(CATEGORIA, false)))
                .isInstanceOfSatisfying(
                        CategoriaItemCuantitativoJuradoNoEncontradaException.class,
                        exception -> assertThat(exception.getCodigoError()).isEqualTo(
                                EvaluacionesCodes.ItemCuantitativoJurado.CATEGORIA_NO_ENCONTRADA));
    }

    @Test
    void debeRechazarNombreDuplicadoEnCategoria() {
        // Act & Assert
        assertThatThrownBy(() -> new NombreItemCuantitativoJuradoPorCategoriaUnicoRuleImpl()
                .validar(new DisponibilidadNombreItemCuantitativoJurado(
                        "Calidad", CATEGORIA, true)))
                .isInstanceOfSatisfying(
                        NombreItemCuantitativoJuradoDuplicadoException.class,
                        exception -> assertThat(exception.getCodigoError()).isEqualTo(
                                EvaluacionesCodes.ItemCuantitativoJurado.NOMBRE_CATEGORIA_DUPLICADO));
    }
}
