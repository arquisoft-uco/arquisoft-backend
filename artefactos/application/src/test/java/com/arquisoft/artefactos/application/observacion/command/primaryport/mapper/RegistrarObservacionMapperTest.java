package com.arquisoft.artefactos.application.observacion.command.primaryport.mapper;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.RegistrarObservacionCommand;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarObservacionMapperTest {

    @Test
    void debeArmarElRegistro_cuandoSeConvierteElCommand() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = RegistrarObservacionCommand.crear(revision, "Detalle", asesor);

        // Act
        var registro = RegistrarObservacionMapper.toDomain(command);

        // Assert
        assertThat(registro.getAsesor()).isEqualTo(asesor);
        assertThat(registro.getObservacion().getRevisionAsesor()).isEqualTo(revision);
        assertThat(registro.getObservacion().getDescripcion()).isEqualTo("Detalle");
        assertThat(registro.getObservacion().getEstado()).isEqualTo(EstadoObservacion.NUEVA);
        assertThat(registro.getObservacion().getId()).isNotNull();
    }
}
