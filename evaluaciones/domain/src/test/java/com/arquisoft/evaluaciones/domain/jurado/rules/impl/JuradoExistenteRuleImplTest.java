package com.arquisoft.evaluaciones.domain.jurado.rules.impl;

import com.arquisoft.evaluaciones.domain.jurado.exception.JuradoNoEncontradoException;
import com.arquisoft.evaluaciones.domain.jurado.model.ExistenciaJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JuradoExistenteRuleImplTest {

    private final JuradoExistenteRuleImpl regla = new JuradoExistenteRuleImpl();

    @Test
    void noDebeLanzar_cuandoElJuradoExiste() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaJurado(UtilUUID.generarNuevoUUID(), true)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarJuradoNoEncontrado_cuandoNoExiste() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ExistenciaJurado(jurado, false)))
                .isInstanceOfSatisfying(JuradoNoEncontradoException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(EvaluacionesCodes.Jurado.NO_ENCONTRADO);
                    assertThat(exception.getMessage()).contains(jurado.toString());
                });
    }
}
