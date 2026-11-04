package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.PuntajesCuantitativosAsesorExcedenValorItemException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.PuntajesVsValoresMaximosAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PuntajesCuantitativosAsesorNoExcedenValorItemRuleImplTest {

    private final PuntajesCuantitativosAsesorNoExcedenValorItemRuleImpl regla =
            new PuntajesCuantitativosAsesorNoExcedenValorItemRuleImpl();

    @Test
    void debePermitirFlujo_cuandoPuntajeIgualAlMaximo() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var entrada = new PuntajesVsValoresMaximosAsesor(Map.of(item, 50), Map.of(item, 50));

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoAlgunPuntajeExcedeElMaximo() {
        // Arrange
        var correcto = UtilUUID.generarNuevoUUID();
        var excedido = UtilUUID.generarNuevoUUID();
        var entrada = new PuntajesVsValoresMaximosAsesor(
                Map.of(correcto, 10, excedido, 51), Map.of(correcto, 50, excedido, 50));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(PuntajesCuantitativosAsesorExcedenValorItemException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJES_EXCEDEN_VALOR_ITEM);
                    assertThat(exception.getMessage()).contains(excedido.toString())
                            .doesNotContain(correcto.toString());
                });
    }
}
