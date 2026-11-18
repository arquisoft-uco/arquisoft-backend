package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.CambiarCriterioEvaluacionCualitativaAsesorCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarCriterioEvaluacionCualitativaAsesorMapperTest {

    @Test
    void debeMapearADomain() {
        // Arrange
        var evaluacionCualitativa = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var command = CambiarCriterioEvaluacionCualitativaAsesorCommand.crear(
                evaluacionCualitativa, criterio.toString(), asesor);

        // Act
        var cambio = CambiarCriterioEvaluacionCualitativaAsesorMapper.toDomain(command);

        // Assert
        assertThat(cambio.getEvaluacionCualitativaAsesor()).isEqualTo(evaluacionCualitativa);
        assertThat(cambio.getAsesor()).isEqualTo(asesor);
        assertThat(cambio.getCriterio()).isEqualTo(criterio);
    }
}
