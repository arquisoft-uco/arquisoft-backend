package com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.ExistenciaJurados;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JuradosExistentesRuleImplTest {

    private final JuradosExistentesRuleImpl regla = new JuradosExistentesRuleImpl();

    @Test
    void debePermitirFlujo_cuandoTodosLosJuradosExisten() {
        // Arrange
        var juradoUno = UtilUUID.generarNuevoUUID();
        var juradoDos = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaJurados(
                Set.of(juradoUno, juradoDos), Set.of(juradoUno, juradoDos))))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcionNombrandoFaltantes_cuandoAlgunJuradoNoExiste() {
        // Arrange
        var existente = UtilUUID.generarNuevoUUID();
        var faltante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ExistenciaJurados(
                Set.of(existente, faltante), Set.of(existente))))
                .isInstanceOfSatisfying(JuradosNoEncontradosException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionJurado.JURADOS_NO_ENCONTRADOS);
                    assertThat(exception.getMessage())
                            .contains(faltante.toString())
                            .doesNotContain(existente.toString());
                });
    }
}
