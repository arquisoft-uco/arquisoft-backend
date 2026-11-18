package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.CategoriaItemCuantitativoAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.model.ExistenciaCategoriaItemCuantitativoAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoriaItemCuantitativoAsesorExistenteRuleImplTest {

    private final CategoriaItemCuantitativoAsesorExistenteRuleImpl rule =
            new CategoriaItemCuantitativoAsesorExistenteRuleImpl();

    @Test
    void debeLanzarSinUuidEnElMensaje_cuandoCategoriaNoExiste() {
        // Arrange
        var categoria = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaCategoriaItemCuantitativoAsesor(categoria, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOfSatisfying(CategoriaItemCuantitativoAsesorNoEncontradaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ItemCuantitativoAsesor.CATEGORIA_NO_ENCONTRADA);
                    assertThat(exception.getError().getMessage()).doesNotContain(categoria.toString());
                    assertThat(exception.getMessage()).contains(categoria.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoCategoriaExiste() {
        // Arrange
        var existencia = new ExistenciaCategoriaItemCuantitativoAsesor(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }
}
