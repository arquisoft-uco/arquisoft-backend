package com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteProyectoGradoMapperTest {

    @Test
    void debeConservarTodosLosCampos_alIrYVolverDeLaEntity() {
        // Arrange
        var entity = new EstudianteProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                Instant.parse("2026-10-02T10:00:00Z"), Instant.parse("2026-10-02T10:00:00Z"));
        var vigente = EstudianteProyectoGradoDomain.crear(
                entity.estudiante(), entity.proyectoGrado(), entity.ocurridoEn());

        // Act
        var domain = EstudianteProyectoGradoMapper.toDomain(entity);
        var devuelta = EstudianteProyectoGradoMapper.toEntity(domain);
        var entityVigente = EstudianteProyectoGradoMapper.toEntity(vigente);

        // Assert
        assertThat(domain.estaDestituido()).isTrue();
        assertThat(devuelta).isEqualTo(entity);
        assertThat(entityVigente.destituidoEn()).isEqualTo(UtilFecha.VACIO);
    }
}
