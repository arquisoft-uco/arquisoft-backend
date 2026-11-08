package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.ItemCuantitativoAsesorEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.model.UsoItemCuantitativoAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemCuantitativoAsesorSinUsoRuleImplTest {

    private final ItemCuantitativoAsesorSinUsoRuleImpl rule = new ItemCuantitativoAsesorSinUsoRuleImpl();

    @Test
    void debeLanzarSinUuidEnElMensaje_cuandoItemEstaEnUso() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var uso = new UsoItemCuantitativoAsesor(item, true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(uso))
                .isInstanceOfSatisfying(ItemCuantitativoAsesorEnUsoException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_EN_USO);
                    assertThat(exception.getError().getMessage()).doesNotContain(item.toString());
                    assertThat(exception.getMessage()).contains(item.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoItemNoEstaEnUso() {
        // Arrange
        var uso = new UsoItemCuantitativoAsesor(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> rule.validar(uso)).doesNotThrowAnyException();
    }
}
