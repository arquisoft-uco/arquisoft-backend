package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.ModificarObservacionItemAsesorCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarObservacionItemAsesorMapperTest {

    @Test
    void debeMapearAlObjetoDeAccion() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = ModificarObservacionItemAsesorCommand.crear(observacion, asesor, "Sustento");

        // Act
        var modificacion = ModificarObservacionItemAsesorMapper.toDomain(command);

        // Assert
        assertThat(modificacion.getObservacion()).isEqualTo(observacion);
        assertThat(modificacion.getAsesor()).isEqualTo(asesor);
        assertThat(modificacion.getDescripcion()).isEqualTo("Sustento");
    }
}
