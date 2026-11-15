package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.EvaluacionCuantitativaAsesorInexistenteException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaEvaluacionCuantitativaAsesorPorId;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionCuantitativaAsesorExistenteRuleImplTest {

    private final EvaluacionCuantitativaAsesorExistenteRuleImpl regla =
            new EvaluacionCuantitativaAsesorExistenteRuleImpl();

    @Test
    void debePasar_cuandoExiste() {
        // Arrange
        var entrada = new ExistenciaEvaluacionCuantitativaAsesorPorId(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaEvaluacionCuantitativaAsesorPorId(id, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EvaluacionCuantitativaAsesorInexistenteException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCuantitativaAsesor.NO_ENCONTRADA);
                    assertThat(exception.getError().getMessage()).doesNotContain(id.toString());
                });
    }
}
