package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.RemoverObservacionItemAsesorCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverObservacionItemAsesorMapperTest {

    @Test
    void debeMapearAlObjetoDeAccion_cuandoCommandValido() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = RemoverObservacionItemAsesorCommand.crear(observacion, asesor);

        // Act
        var remocion = RemoverObservacionItemAsesorMapper.toDomain(command);

        // Assert
        assertThat(remocion.getObservacion()).isEqualTo(observacion);
        assertThat(remocion.getAsesor()).isEqualTo(asesor);
    }
}
