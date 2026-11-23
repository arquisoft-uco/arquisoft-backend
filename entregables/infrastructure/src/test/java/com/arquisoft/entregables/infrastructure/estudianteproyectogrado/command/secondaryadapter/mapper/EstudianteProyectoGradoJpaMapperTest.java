package com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteProyectoGradoJpaMapperTest {

    @Test
    void debeTraducirVacioANuloYConservarLaBaja_cuandoMapeaEnAmbosSentidos() {
        // Arrange
        var ocurridoEn = Instant.parse("2026-10-20T10:00:00Z");
        var eliminadoEn = Instant.parse("2026-10-21T10:00:00Z");
        var vigente = new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "Ana", "ana@uco.edu.co", ocurridoEn, UtilFecha.VACIO);
        var eliminado = new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "Luis", "luis@uco.edu.co", ocurridoEn, eliminadoEn);

        // Act
        var jpaVigente = EstudianteProyectoGradoJpaMapper.toJpaEntity(vigente);
        var jpaEliminado = EstudianteProyectoGradoJpaMapper.toJpaEntity(eliminado);

        // Assert
        assertThat(jpaVigente.getEliminadoEn()).isNull();
        assertThat(jpaVigente.getId()).isEqualTo(vigente.id());
        assertThat(jpaVigente.getEstudianteId()).isEqualTo(vigente.estudiante());
        assertThat(jpaVigente.getProyectoGradoId()).isEqualTo(vigente.proyectoGrado());
        assertThat(jpaVigente.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(jpaVigente.getNombre()).isEqualTo("Ana");
        assertThat(jpaVigente.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(EstudianteProyectoGradoJpaMapper.toEntity(jpaVigente)).isEqualTo(vigente);
        assertThat(jpaEliminado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(EstudianteProyectoGradoJpaMapper.toEntity(jpaEliminado)).isEqualTo(eliminado);
    }

    @Test
    void debeLeerElContactoNuloComoTextoVacio_cuandoLaFilaEsAnteriorALaMigracion() {
        // Arrange
        var jpa = EstudianteProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estudianteId(UtilUUID.generarNuevoUUID())
                .proyectoGradoId(UtilUUID.generarNuevoUUID())
                .ocurridoEn(Instant.parse("2026-10-20T10:00:00Z"))
                .build();

        // Act
        var entity = EstudianteProyectoGradoJpaMapper.toEntity(jpa);

        // Assert
        assertThat(entity.nombre()).isEqualTo(UtilTexto.VACIO);
        assertThat(entity.email()).isEqualTo(UtilTexto.VACIO);
        assertThat(entity.eliminadoEn()).isEqualTo(UtilFecha.VACIO);
    }
}
