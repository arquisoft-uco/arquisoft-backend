package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.ItemCuantitativoAsesorNoEncontradoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.model.ExistenciaItemCuantitativoAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemCuantitativoAsesorExistenteRuleImplTest {

    private final ItemCuantitativoAsesorExistenteRuleImpl rule = new ItemCuantitativoAsesorExistenteRuleImpl();

    @Test
    void debeLanzarSinUuidEnElMensaje_cuandoItemNoExiste() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaItemCuantitativoAsesor(item, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOfSatisfying(ItemCuantitativoAsesorNoEncontradoException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_NO_ENCONTRADO);
                    assertThat(exception.getError().getMessage()).doesNotContain(item.toString());
                    assertThat(exception.getMessage()).contains(item.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoItemExiste() {
        // Arrange
        var existencia = new ExistenciaItemCuantitativoAsesor(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }
}
