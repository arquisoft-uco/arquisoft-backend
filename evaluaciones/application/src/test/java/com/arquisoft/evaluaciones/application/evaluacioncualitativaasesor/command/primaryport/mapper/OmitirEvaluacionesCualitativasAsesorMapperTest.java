package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.OmitirEvaluacionesCualitativasAsesorCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OmitirEvaluacionesCualitativasAsesorMapperTest {

    @Test
    void debeMapearADomain_cuandoCommandValido() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var command = new OmitirEvaluacionesCualitativasAsesorCommand(evaluacionAsesor, asesor, List.of(item));

        // Act
        var omision = OmitirEvaluacionesCualitativasAsesorMapper.toDomain(command);

        // Assert
        assertThat(omision.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(omision.getAsesor()).isEqualTo(asesor);
        assertThat(omision.getItems()).containsExactly(item);
    }
}
