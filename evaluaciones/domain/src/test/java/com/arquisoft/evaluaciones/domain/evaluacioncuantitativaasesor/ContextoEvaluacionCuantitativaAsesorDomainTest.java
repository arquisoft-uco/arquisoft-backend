package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContextoEvaluacionCuantitativaAsesorDomainTest {

    @Test
    void debeReconstruirConTodosLosCampos_yDistinguirElCentinela() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var contexto = ContextoEvaluacionCuantitativaAsesorDomain.reconstruir(
                id, evaluacionAsesor, asesor, EstadoEvaluacion.FINALIZADA);

        // Assert
        assertThat(contexto.getId()).isEqualTo(id);
        assertThat(contexto.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(contexto.getAsesor()).isEqualTo(asesor);
        assertThat(contexto.getEstado()).isEqualTo(EstadoEvaluacion.FINALIZADA);
        assertThat(contexto.esVacio()).isFalse();
        assertThat(ContextoEvaluacionCuantitativaAsesorDomain.VACIO.esVacio()).isTrue();
        assertThat(ContextoEvaluacionCuantitativaAsesorDomain.VACIO.getAsesor())
                .isEqualTo(UtilUUID.obtenerUUIDPorDefecto());
    }
}
