package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.mapper;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.EnviarRevisionAsesorCommand;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnviarRevisionAsesorMapperTest {

    @Test
    void debeConstruirObjetoDeAccionConAvance_cuandoSeMapeaElCommand() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = EnviarRevisionAsesorCommand.crear(revision, asesor);

        // Act
        var envio = EnviarRevisionAsesorMapper.toDomain(command);

        // Assert
        assertThat(envio.getRevisionAsesor()).isEqualTo(revision);
        assertThat(envio.getAsesor()).isEqualTo(asesor);
        assertThat(envio.getObservaciones().getRevisionAsesor()).isEqualTo(revision);
        assertThat(envio.getObservaciones().getEstadoOrigen()).isEqualTo(EstadoObservacion.NUEVA);
        assertThat(envio.getObservaciones().getEstadoDestino()).isEqualTo(EstadoObservacion.EN_PROGRESO);
    }
}
