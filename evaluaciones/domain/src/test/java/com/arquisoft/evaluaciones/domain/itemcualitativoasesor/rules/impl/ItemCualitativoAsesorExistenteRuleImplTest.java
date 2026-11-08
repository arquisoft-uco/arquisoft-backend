package com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.ItemCualitativoAsesorNoEncontradoException;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.model.ExistenciaItemCualitativoAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemCualitativoAsesorExistenteRuleImplTest {

    private final ItemCualitativoAsesorExistenteRuleImpl rule = new ItemCualitativoAsesorExistenteRuleImpl();

    @Test
    void debeLanzarSinUuidEnElMensaje_cuandoItemNoExiste() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaItemCualitativoAsesor(item, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOfSatisfying(ItemCualitativoAsesorNoEncontradoException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ItemCualitativoAsesor.ITEM_NO_ENCONTRADO);
                    assertThat(exception.getError().getMessage()).doesNotContain(item.toString());
                    assertThat(exception.getMessage()).contains(item.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoItemExiste() {
        // Arrange
        var existencia = new ExistenciaItemCualitativoAsesor(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }
}
