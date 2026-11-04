package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.ItemsCuantitativosAsesorNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaItemsCuantitativosAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemsCuantitativosAsesorExistentesRuleImplTest {

    private final ItemsCuantitativosAsesorExistentesRuleImpl regla = new ItemsCuantitativosAsesorExistentesRuleImpl();

    @Test
    void debePermitirFlujo_cuandoTodosLosItemsExisten() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaItemsCuantitativosAsesor(Set.of(item), Map.of(item, 100));

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoFaltanItems() {
        // Arrange
        var existente = UtilUUID.generarNuevoUUID();
        var faltante = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaItemsCuantitativosAsesor(Set.of(existente, faltante), Map.of(existente, 100));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ItemsCuantitativosAsesorNoEncontradosException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCuantitativaAsesor.ITEMS_NO_ENCONTRADOS);
                    assertThat(exception.getMessage()).contains(faltante.toString())
                            .doesNotContain(existente.toString());
                });
    }
}
