package com.arquisoft.usuarios.application.asesor.command.secondaryport.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorMapperTest {

    @Test
    void debeMapearDomainAEntity_enAsesorMapper() {
        // Arrange
        var usuario = UUID.randomUUID();
        var asesor = AsesorDomain.crear(usuario);

        // Act
        var entity = AsesorMapper.toEntity(asesor);

        // Assert
        assertThat(entity.usuario()).isEqualTo(usuario);
        assertThat(entity.eliminadoEn()).isEqualTo(UtilFecha.VACIO);
    }

    @Test
    void debeMapearEntityADomainConEliminadoEn_cuandoSeLee() {
        // Arrange
        var usuario = UUID.randomUUID();
        var eliminadoEn = Instant.parse("2026-09-23T10:00:00Z");

        // Act
        var asesor = AsesorMapper.toDomain(new AsesorEntity(usuario, eliminadoEn));

        // Assert
        assertThat(asesor.getUsuario()).isEqualTo(usuario);
        assertThat(asesor.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(asesor.estaEliminado()).isTrue();
    }
}
