package com.arquisoft.repositorio_artefactos.infrastructure.administrador.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.entity.AdministradorEntity;
import com.arquisoft.shared.util.UtilFecha;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AdministradorJpaMapperTest {

    @Test
    void debeMapearJpaEntityYEntity_enAmbosSentidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();
        var entity = new AdministradorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn,
                UtilFecha.VACIO);

        // Act
        var jpaEntity = AdministradorJpaMapper.toJpaEntity(entity);
        var entityMapeada = AdministradorJpaMapper.toEntity(jpaEntity);

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
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var vigente = new AdministradorEntity(UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co",
                eliminadoEn, UtilFecha.VACIO);
        var eliminado = new AdministradorEntity(UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co",
                eliminadoEn, eliminadoEn);

        // Act
        var jpaVigente = AdministradorJpaMapper.toJpaEntity(vigente);
        var jpaEliminado = AdministradorJpaMapper.toJpaEntity(eliminado);
        var leidoEliminado = AdministradorJpaMapper.toEntity(jpaEliminado);

        // Assert
        assertThat(jpaVigente.getEliminadoEn()).isNull();
        assertThat(jpaEliminado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(leidoEliminado.eliminadoEn()).isEqualTo(eliminadoEn);
    }
}
