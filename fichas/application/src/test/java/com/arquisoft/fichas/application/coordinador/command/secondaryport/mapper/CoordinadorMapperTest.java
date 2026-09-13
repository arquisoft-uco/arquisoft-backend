package com.arquisoft.fichas.application.coordinador.command.secondaryport.mapper;

import com.arquisoft.fichas.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.fichas.domain.coordinador.CoordinadorDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinadorMapperTest {

    @Test
    void debeMapearEntityADomain_propagandoOcurridoEn() {
        // Arrange
        var ocurridoEn = Instant.now();
        var entity = new CoordinadorEntity(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var domain = CoordinadorMapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(entity.id());
        assertThat(domain.getIdentificador()).isEqualTo(entity.identificador());
        assertThat(domain.getNombre()).isEqualTo(entity.nombre());
        assertThat(domain.getEmail()).isEqualTo(entity.email());
        assertThat(domain.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeMapearDomainAEntity_enCoordinadorMapper() {
        // Arrange
        var ocurridoEn = Instant.now();
        var coordinador = CoordinadorDomain.crear(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var entity = CoordinadorMapper.toEntity(coordinador);

        // Assert
        assertThat(entity.id()).isEqualTo(coordinador.getId());
        assertThat(entity.identificador()).isEqualTo(coordinador.getIdentificador());
        assertThat(entity.nombre()).isEqualTo(coordinador.getNombre());
        assertThat(entity.email()).isEqualTo(coordinador.getEmail());
        assertThat(entity.ocurridoEn()).isEqualTo(ocurridoEn);
    }
}
