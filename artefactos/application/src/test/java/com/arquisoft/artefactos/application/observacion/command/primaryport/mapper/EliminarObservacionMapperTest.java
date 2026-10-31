package com.arquisoft.artefactos.application.observacion.command.primaryport.mapper;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.EliminarObservacionCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarObservacionMapperTest {

    @Test
    void debeMapearAEliminacionDomain_cuandoCommandValido() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = EliminarObservacionCommand.crear(observacion, asesor);

        // Act
        var eliminacion = EliminarObservacionMapper.toDomain(command);

        // Assert
        assertThat(eliminacion.getObservacion()).isEqualTo(observacion);
        assertThat(eliminacion.getAsesor()).isEqualTo(asesor);
    }
}
