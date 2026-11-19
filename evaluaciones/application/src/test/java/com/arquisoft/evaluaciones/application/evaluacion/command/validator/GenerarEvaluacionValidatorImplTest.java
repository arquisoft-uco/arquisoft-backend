package com.arquisoft.evaluaciones.application.evaluacion.command.validator;

import com.arquisoft.evaluaciones.application.evaluacion.command.validator.impl.GenerarEvaluacionValidatorImpl;
import com.arquisoft.evaluaciones.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.evaluaciones.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarEvaluacionValidatorImplTest {

    private final GenerarEvaluacionValidatorImpl validator = new GenerarEvaluacionValidatorImpl();

    @Test
    void debePasar_cuandoElAsesorEstaVigente() {
        // Arrange
        var existencia = new ExistenciaAsesor(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> validator.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoAsesorNoVigente() {
        // Arrange
        var existencia = new ExistenciaAsesor(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia))
                .isInstanceOfSatisfying(AsesorNoEncontradoException.class, ex ->
                        assertThat(ex.getCodigoError()).isEqualTo(EvaluacionesCodes.Asesor.NO_ENCONTRADO));
    }
}
