package com.arquisoft.evaluaciones.domain.evaluacionjurado;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContextoRegistroEvaluacionJuradoDomainTest {

    @Test
    void debeExponerTodosLosCampos_cuandoReconstruyeElContexto() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();

        // Act
        var contexto = ContextoRegistroEvaluacionJuradoDomain.reconstruir(
                id, evaluacion, EstadoEvaluacion.PENDIENTE, entregable, jurado);

        // Assert
        assertThat(contexto.getId()).isEqualTo(id);
        assertThat(contexto.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(contexto.getEstado()).isEqualTo(EstadoEvaluacion.PENDIENTE);
        assertThat(contexto.getEntregable()).isEqualTo(entregable);
        assertThat(contexto.getJurado()).isEqualTo(jurado);
        assertThat(contexto.esVacio()).isFalse();
    }

    @Test
    void debeUsarUuidPorDefectoEnElJurado_cuandoEsVacio() {
        // Act & Assert
        var vacio = ContextoRegistroEvaluacionJuradoDomain.VACIO;
        assertThat(vacio.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(vacio.getJurado())).isTrue();
    }
}
