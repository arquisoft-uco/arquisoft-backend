package com.arquisoft.evaluaciones.domain.asesor.rules.impl;

import com.arquisoft.evaluaciones.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.evaluaciones.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorExistenteRuleImplTest {

    private final AsesorExistenteRuleImpl regla = new AsesorExistenteRuleImpl();

    @Test
    void noDebeLanzar_cuandoElAsesorExiste() {
        // Arrange
        var existencia = new ExistenciaAsesor(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarAsesorNoEncontrado_cuandoNoExiste() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaAsesor(asesor, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(AsesorNoEncontradoException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(EvaluacionesCodes.Asesor.NO_ENCONTRADO);
                    assertThat(exception.getMessage()).contains(asesor.toString());
                });
    }
}
