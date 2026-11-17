package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccesoEvaluacionCuantitativaAsesorDomainTest {

    @Test
    void debeReconstruir_conTodosLosCampos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var acceso = AccesoEvaluacionCuantitativaAsesorDomain.reconstruir(evaluacionAsesor, asesor);

        // Assert
        assertThat(acceso.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(acceso.getAsesor()).isEqualTo(asesor);
        assertThat(acceso.esVacio()).isFalse();
    }

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Act
        var acceso = AccesoEvaluacionCuantitativaAsesorDomain.VACIO;

        // Assert
        assertThat(acceso.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(acceso.getEvaluacionAsesor())).isTrue();
        assertThat(UtilUUID.esPorDefecto(acceso.getAsesor())).isTrue();
    }
}
