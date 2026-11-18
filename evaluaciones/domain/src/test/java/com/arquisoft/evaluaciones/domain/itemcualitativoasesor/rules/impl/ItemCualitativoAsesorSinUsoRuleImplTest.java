package com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.ItemCualitativoAsesorEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.model.UsoItemCualitativoAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemCualitativoAsesorSinUsoRuleImplTest {

    private final ItemCualitativoAsesorSinUsoRuleImpl rule = new ItemCualitativoAsesorSinUsoRuleImpl();

    @Test
    void debeLanzarSinUuidEnElMensaje_cuandoItemEstaEnUso() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var uso = new UsoItemCualitativoAsesor(item, true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(uso))
                .isInstanceOfSatisfying(ItemCualitativoAsesorEnUsoException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ItemCualitativoAsesor.ITEM_EN_USO);
                    assertThat(exception.getError().getMessage()).doesNotContain(item.toString());
                    assertThat(exception.getMessage()).contains(item.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoItemNoEstaEnUso() {
        // Arrange
        var uso = new UsoItemCualitativoAsesor(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> rule.validar(uso)).doesNotThrowAnyException();
    }
}
