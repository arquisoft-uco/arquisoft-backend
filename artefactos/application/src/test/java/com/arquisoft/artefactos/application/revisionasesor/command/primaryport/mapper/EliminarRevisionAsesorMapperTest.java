package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.mapper;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.EliminarRevisionAsesorCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarRevisionAsesorMapperTest {

    @Test
    void debeMapearAlObjetoDeAccion_cuandoElCommandEsValido() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = EliminarRevisionAsesorCommand.crear(revision, asesor);

        // Act
        var eliminacion = EliminarRevisionAsesorMapper.toDomain(command);

        // Assert
        assertThat(eliminacion.getRevisionAsesor()).isEqualTo(revision);
        assertThat(eliminacion.getAsesor()).isEqualTo(asesor);
    }
}
