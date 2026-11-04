package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContextoAsignacionEvaluacionDomainTest {

    @Test
    void debeDistinguirVacioDeReconstruido_cuandoSeConsultaEsVacio() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();

        // Act
        var contexto = ContextoAsignacionEvaluacionDomain.reconstruir(evaluacion, EstadoEvaluacion.EN_PROGRESO);

        // Assert
        assertThat(contexto.esVacio()).isFalse();
        assertThat(contexto.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(contexto.getEstado()).isEqualTo(EstadoEvaluacion.EN_PROGRESO);
        assertThat(ContextoAsignacionEvaluacionDomain.VACIO.esVacio()).isTrue();
    }
}
