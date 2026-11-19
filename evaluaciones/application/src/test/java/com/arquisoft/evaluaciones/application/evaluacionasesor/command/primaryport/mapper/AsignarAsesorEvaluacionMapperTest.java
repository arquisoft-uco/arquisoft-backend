package com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.model.AsignarAsesorEvaluacionCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsignarAsesorEvaluacionMapperTest {

    @Test
    void debeMapearADomain_cuandoCommandValido() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = AsignarAsesorEvaluacionCommand.crear(evaluacion, asesor.toString());

        // Act
        var domain = AsignarAsesorEvaluacionMapper.toDomain(command);

        // Assert
        assertThat(domain.getId()).isNotNull();
        assertThat(domain.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(domain.getAsesor()).isEqualTo(asesor);
    }
}
