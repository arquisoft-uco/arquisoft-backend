package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.PuntajesCuantitativosJuradoExcedenValorItemException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.PuntajesVsValoresMaximosJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PuntajesCuantitativosJuradoNoExcedenValorItemRuleImplTest {

    private final PuntajesCuantitativosJuradoNoExcedenValorItemRuleImpl regla =
            new PuntajesCuantitativosJuradoNoExcedenValorItemRuleImpl();

    @Test
    void noDebeLanzar_cuandoPuntajeIgualAlMaximoDelItem() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var entrada = new PuntajesVsValoresMaximosJurado(Map.of(item, 100), Map.of(item, 100));

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConExcedidos_cuandoAlgunPuntajeSuperaElMaximo() {
        // Arrange
        var valido = UtilUUID.generarNuevoUUID();
        var excedido = UtilUUID.generarNuevoUUID();
        var entrada = new PuntajesVsValoresMaximosJurado(
                Map.of(valido, 50, excedido, 101), Map.of(valido, 100, excedido, 100));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(PuntajesCuantitativosJuradoExcedenValorItemException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJES_EXCEDEN_VALOR_ITEM);
                    assertThat(exception.getMessage()).contains(excedido.toString()).doesNotContain(valido.toString());
                });
    }
}
