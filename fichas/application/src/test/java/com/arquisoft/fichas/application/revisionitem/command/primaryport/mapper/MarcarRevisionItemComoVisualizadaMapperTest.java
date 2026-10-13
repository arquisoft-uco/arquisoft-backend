package com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.MarcarRevisionItemComoVisualizadaCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarcarRevisionItemComoVisualizadaMapperTest {

    @Test
    void debeConservarLosIds_cuandoConvierteElCommandADomain() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var command = MarcarRevisionItemComoVisualizadaCommand.crear(revisionItem, estudiante);

        // Act
        var visualizacion = MarcarRevisionItemComoVisualizadaMapper.toDomain(command);

        // Assert
        assertThat(visualizacion.getRevisionItem()).isEqualTo(revisionItem);
        assertThat(visualizacion.getEstudiante()).isEqualTo(estudiante);
    }
}
