package com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.entity.JuradoEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class JuradoJpaMapperTest {

    @Test
    void debeTraducirVacioANuloYConservarLaBaja_cuandoMapeaEnAmbosSentidos() {
        // Arrange
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");
        var eliminadoEn = Instant.parse("2026-09-25T10:00:00Z");
        var vigente = new JuradoEntity(UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez",
                "ana@uco.edu.co", ocurridoEn, UtilFecha.VACIO);
        var eliminado = new JuradoEntity(UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez",
                "ana@uco.edu.co", ocurridoEn, eliminadoEn);

        // Act
        var jpaVigente = JuradoJpaMapper.toJpaEntity(vigente);
        var jpaEliminado = JuradoJpaMapper.toJpaEntity(eliminado);

        // Assert
        assertThat(jpaVigente.getEliminadoEn()).isNull();
        assertThat(jpaVigente.getId()).isEqualTo(vigente.id());
        assertThat(jpaVigente.getIdentificador()).isEqualTo("20161020123");
        assertThat(jpaVigente.getNombre()).isEqualTo("Ana Perez");
        assertThat(jpaVigente.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(jpaVigente.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(JuradoJpaMapper.toEntity(jpaVigente)).isEqualTo(vigente);
        assertThat(jpaEliminado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(JuradoJpaMapper.toEntity(jpaEliminado)).isEqualTo(eliminado);
    }
}
