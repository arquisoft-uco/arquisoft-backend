package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.model.SincronizarProyectoEstudianteAccesoCommand;
import com.arquisoft.evaluaciones.domain.proyectoestudianteacceso.ProyectoEstudianteAccesoDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SincronizarProyectoEstudianteAccesoMapperTest {

    @Test
    void debeConstruirElDomain_conLosDatosDelCommand() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();
        SincronizarProyectoEstudianteAccesoCommand command =
                new SincronizarProyectoEstudianteAccesoCommand(proyecto, estudiante, false, ocurridoEn);

        // Act
        ProyectoEstudianteAccesoDomain domain = SincronizarProyectoEstudianteAccesoMapper.toDomain(command);

        // Assert
        assertThat(domain.getProyecto()).isEqualTo(proyecto);
        assertThat(domain.getEstudiante()).isEqualTo(estudiante);
        assertThat(domain.isActivo()).isFalse();
        assertThat(domain.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
