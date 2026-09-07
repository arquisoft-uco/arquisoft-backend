package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.ProyectoEstudianteAccesoEntity;
import com.arquisoft.evaluaciones.domain.proyectoestudianteacceso.ProyectoEstudianteAccesoDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoEstudianteAccesoMapperTest {

    @Test
    void debeConvertirDomainAEntity_yDeVueltaSinPerderDatos() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();
        ProyectoEstudianteAccesoDomain domain =
                ProyectoEstudianteAccesoDomain.crear(proyecto, estudiante, true, ocurridoEn);

        // Act
        ProyectoEstudianteAccesoEntity entity = ProyectoEstudianteAccesoMapper.toEntity(domain);
        ProyectoEstudianteAccesoDomain reconstruido = ProyectoEstudianteAccesoMapper.toDomain(entity);

        // Assert
        assertThat(entity.proyecto()).isEqualTo(proyecto);
        assertThat(entity.estudiante()).isEqualTo(estudiante);
        assertThat(entity.activo()).isTrue();
        assertThat(entity.ocurridoEn()).isEqualTo(ocurridoEn);

        assertThat(reconstruido.getProyecto()).isEqualTo(proyecto);
        assertThat(reconstruido.getEstudiante()).isEqualTo(estudiante);
        assertThat(reconstruido.isActivo()).isTrue();
        assertThat(reconstruido.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
