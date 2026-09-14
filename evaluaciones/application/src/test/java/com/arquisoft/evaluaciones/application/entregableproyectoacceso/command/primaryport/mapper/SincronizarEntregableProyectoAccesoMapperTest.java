package com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.model.SincronizarEntregableProyectoAccesoCommand;
import com.arquisoft.evaluaciones.domain.entregableproyectoacceso.EntregableProyectoAccesoDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SincronizarEntregableProyectoAccesoMapperTest {

    @Test
    void debeConstruirElDomain_conLosDatosDelCommand() {
        // Arrange
        UUID entregable = UUID.randomUUID();
        UUID proyecto = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();
        SincronizarEntregableProyectoAccesoCommand command =
                new SincronizarEntregableProyectoAccesoCommand(entregable, proyecto, 3, ocurridoEn);

        // Act
        EntregableProyectoAccesoDomain domain = SincronizarEntregableProyectoAccesoMapper.toDomain(command);

        // Assert
        assertThat(domain.getEntregable()).isEqualTo(entregable);
        assertThat(domain.getProyecto()).isEqualTo(proyecto);
        assertThat(domain.getVersionEntregable()).isEqualTo(3);
        assertThat(domain.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(domain.isActivo()).isTrue();
    }
}
