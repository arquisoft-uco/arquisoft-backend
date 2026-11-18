package com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.mapper;

import com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.entity.DocumentacionJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentacionJpaMapperTest {

    @Test
    void debeMapearLasSeisColumnasAlEntity_cuandoRecibeLaEntidadJpa() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var bibliotecario = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-11-15T10:00:00Z");
        var jpa = DocumentacionJpaEntity.builder()
                .id(id).url("https://minio.test/a.zip").fechaPublicacion(fecha)
                .entregableProyectoGradoId(entregable).bibliotecarioId(bibliotecario)
                .estadoDocumentacion("PENDIENTE").build();

        // Act
        var entity = DocumentacionJpaMapper.toEntity(jpa);

        // Assert
        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.url()).isEqualTo("https://minio.test/a.zip");
        assertThat(entity.fechaPublicacion()).isEqualTo(fecha);
        assertThat(entity.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(entity.bibliotecario()).isEqualTo(bibliotecario);
        assertThat(entity.estadoDocumentacion()).isEqualTo("PENDIENTE");
    }
}
