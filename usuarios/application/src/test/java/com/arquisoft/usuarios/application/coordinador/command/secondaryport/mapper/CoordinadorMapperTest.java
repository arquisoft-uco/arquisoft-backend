package com.arquisoft.usuarios.application.coordinador.command.secondaryport.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinadorMapperTest {

    @Test
    void debeMapearDomainAEntity_enCoordinadorMapper() {
        // Arrange
        var usuario = UUID.randomUUID();
        var coordinador = CoordinadorDomain.crear(usuario);

        // Act
        var entity = CoordinadorMapper.toEntity(coordinador);

        // Assert
        assertThat(entity.usuario()).isEqualTo(usuario);
        assertThat(entity.eliminadoEn()).isEqualTo(UtilFecha.VACIO);
    }

    @Test
    void debeMapearEntityADomainConEliminadoEn_cuandoSeLee() {
        // Arrange
        var usuario = UUID.randomUUID();
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        var coordinador = CoordinadorMapper.toDomain(new CoordinadorEntity(usuario, eliminadoEn));

        // Assert
        assertThat(coordinador.getUsuario()).isEqualTo(usuario);
        assertThat(coordinador.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(coordinador.estaEliminado()).isTrue();
    }
}
