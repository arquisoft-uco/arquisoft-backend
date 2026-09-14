package com.arquisoft.fichas.application.asesorficha.command.secondaryport.mapper;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorFichaMapperTest {

    @Test
    void debeMapearEntityADomain_propagandoOcurridoEn() {
        // Arrange
        var ocurridoEn = Instant.now();
        var entity = new AsesorFichaEntity(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var domain = AsesorFichaMapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(entity.id());
        assertThat(domain.getIdentificador()).isEqualTo(entity.identificador());
        assertThat(domain.getNombre()).isEqualTo(entity.nombre());
        assertThat(domain.getEmail()).isEqualTo(entity.email());
        assertThat(domain.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeMapearDomainAEntity_enAsesorFichaMapper() {
        // Arrange
        var ocurridoEn = Instant.now();
        var asesorFicha = AsesorFichaDomain.crear(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var entity = AsesorFichaMapper.toEntity(asesorFicha);

        // Assert
        assertThat(entity.id()).isEqualTo(asesorFicha.getId());
        assertThat(entity.identificador()).isEqualTo(asesorFicha.getIdentificador());
        assertThat(entity.nombre()).isEqualTo(asesorFicha.getNombre());
        assertThat(entity.email()).isEqualTo(asesorFicha.getEmail());
        assertThat(entity.ocurridoEn()).isEqualTo(ocurridoEn);
    }
}
