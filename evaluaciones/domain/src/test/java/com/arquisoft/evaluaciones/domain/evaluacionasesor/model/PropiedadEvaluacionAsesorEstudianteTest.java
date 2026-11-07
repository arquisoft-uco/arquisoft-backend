package com.arquisoft.evaluaciones.domain.evaluacionasesor.model;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropiedadEvaluacionAsesorEstudianteTest {

    @Test
    void debeNormalizarListaNula_cuandoEsNula() {
        // Act
        var propiedad = new PropiedadEvaluacionAsesorEstudiante(
                UtilUUID.generarNuevoUUID(), null, UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(propiedad.estudiantesVinculados()).isNotNull().isEmpty();
    }
}
