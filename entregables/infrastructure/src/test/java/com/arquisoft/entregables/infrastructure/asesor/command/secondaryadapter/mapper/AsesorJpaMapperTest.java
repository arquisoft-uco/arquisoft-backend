package com.arquisoft.entregables.infrastructure.asesor.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.entregables.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorJpaMapperTest {

    @Test
    void debeMapearJpaEntityYEntity_enAmbosSentidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();
        var entity = new AsesorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, UtilFecha.VACIO);

        // Act
        var jpaEntity = AsesorJpaMapper.toJpaEntity(entity);
        var entityMapeada = AsesorJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(jpaEntity.getId()).isEqualTo(id);
        assertThat(jpaEntity.getIdentificador()).isEqualTo("20161020123");
        assertThat(jpaEntity.getNombre()).isEqualTo("Ana Perez");
        assertThat(jpaEntity.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(jpaEntity.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(entityMapeada).isEqualTo(entity);
    }

    @Test
    void debeTraducirVacioANuloYViceversa_cuandoMapeaEliminadoEn() {
        // Arrange
        var ocurridoEn = Instant.parse("2026-09-23T10:00:00Z");
        var vigente = new AsesorEntity(UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co",
                ocurridoEn, UtilFecha.VACIO);
        var eliminado = new AsesorEntity(UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co",
                ocurridoEn, ocurridoEn);

        // Act
        var jpaVigente = AsesorJpaMapper.toJpaEntity(vigente);
        var jpaEliminado = AsesorJpaMapper.toJpaEntity(eliminado);
        var leidoEliminado = AsesorJpaMapper.toEntity(jpaEliminado);

        // Assert
        assertThat(jpaVigente.getEliminadoEn()).isNull();
        assertThat(jpaEliminado.getEliminadoEn()).isEqualTo(ocurridoEn);
        assertThat(leidoEliminado.eliminadoEn()).isEqualTo(ocurridoEn);
    }
}
