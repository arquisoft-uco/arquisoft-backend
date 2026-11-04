package com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.mapper;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.model.AgregarAsesorProyectoGradoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarAsesorProyectoGradoMapperTest {

    @Test
    void debeArmarVinculoYTraspasoConElMismoProyectoYAsesor() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-10-02T10:00:00Z");
        var command = new AgregarAsesorProyectoGradoCommand(asesor, proyecto, ocurridoEn);

        // Act
        var agregacion = AgregarAsesorProyectoGradoMapper.toDomain(command);

        // Assert
        assertThat(agregacion.getVinculo().getAsesor()).isEqualTo(asesor);
        assertThat(agregacion.getVinculo().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(agregacion.getVinculo().getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(agregacion.getTraspaso().getAsesor()).isEqualTo(asesor);
        assertThat(agregacion.getTraspaso().getProyectoGrado()).isEqualTo(proyecto);
    }
}
