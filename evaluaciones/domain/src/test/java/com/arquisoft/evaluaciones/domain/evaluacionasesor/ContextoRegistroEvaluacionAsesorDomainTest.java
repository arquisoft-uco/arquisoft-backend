package com.arquisoft.evaluaciones.domain.evaluacionasesor;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContextoRegistroEvaluacionAsesorDomainTest {

    @Test
    void debeReconstruirConTodosLosCampos_cuandoSeInformanLosDatos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var contexto = ContextoRegistroEvaluacionAsesorDomain.reconstruir(
                id, evaluacion, EstadoEvaluacion.PENDIENTE, entregable, asesor);

        // Assert
        assertThat(contexto.getId()).isEqualTo(id);
        assertThat(contexto.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(contexto.getEstado()).isEqualTo(EstadoEvaluacion.PENDIENTE);
        assertThat(contexto.getEntregable()).isEqualTo(entregable);
        assertThat(contexto.getAsesor()).isEqualTo(asesor);
        assertThat(contexto.esVacio()).isFalse();
    }

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Act & Assert
        assertThat(ContextoRegistroEvaluacionAsesorDomain.VACIO.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(ContextoRegistroEvaluacionAsesorDomain.VACIO.getAsesor())).isTrue();
    }
}
