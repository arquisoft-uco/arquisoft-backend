package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.CambiarPuntajeEvaluacionCuantitativaAsesorCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarPuntajeEvaluacionCuantitativaAsesorMapperTest {

    @Test
    void debeMapearADomain_cuandoCommandValido() {
        // Arrange
        var evaluacionCuantitativaAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = new CambiarPuntajeEvaluacionCuantitativaAsesorCommand(evaluacionCuantitativaAsesor, 200, asesor);

        // Act
        var cambio = CambiarPuntajeEvaluacionCuantitativaAsesorMapper.toDomain(command);

        // Assert
        assertThat(cambio.getEvaluacionCuantitativaAsesor()).isEqualTo(evaluacionCuantitativaAsesor);
        assertThat(cambio.getAsesor()).isEqualTo(asesor);
        assertThat(cambio.getNuevoPuntaje()).isEqualTo(200);
    }
}
