package com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.entity.EntregableProyectoAccesoEntity;
import com.arquisoft.evaluaciones.domain.entregableproyectoacceso.EntregableProyectoAccesoDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoAccesoMapperTest {

    @Test
    void debeConvertirDomainAEntity_yDeVueltaSinPerderDatos() {
        // Arrange
        UUID entregable = UUID.randomUUID();
        UUID proyecto = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();
        EntregableProyectoAccesoDomain domain =
                EntregableProyectoAccesoDomain.crear(entregable, proyecto, 5, ocurridoEn);

        // Act
        EntregableProyectoAccesoEntity entity = EntregableProyectoAccesoMapper.toEntity(domain);
        EntregableProyectoAccesoDomain reconstruido = EntregableProyectoAccesoMapper.toDomain(entity);

        // Assert
        assertThat(entity.entregable()).isEqualTo(entregable);
        assertThat(entity.proyecto()).isEqualTo(proyecto);
        assertThat(entity.versionEntregable()).isEqualTo(5);
        assertThat(entity.activo()).isTrue();
        assertThat(entity.ocurridoEn()).isEqualTo(ocurridoEn);

        assertThat(reconstruido.getEntregable()).isEqualTo(entregable);
        assertThat(reconstruido.getProyecto()).isEqualTo(proyecto);
        assertThat(reconstruido.getVersionEntregable()).isEqualTo(5);
        assertThat(reconstruido.isActivo()).isTrue();
        assertThat(reconstruido.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
