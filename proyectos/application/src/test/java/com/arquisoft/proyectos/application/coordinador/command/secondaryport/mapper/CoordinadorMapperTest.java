package com.arquisoft.proyectos.application.coordinador.command.secondaryport.mapper;

import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinadorMapperTest {

    @Test
    void debeMapearDomainAEntity_enCoordinadorMapper() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var coordinador = CoordinadorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var entity = CoordinadorMapper.toEntity(coordinador);

        // Assert
        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.identificador()).isEqualTo("20161020123");
        assertThat(entity.nombre()).isEqualTo("Ana Perez");
        assertThat(entity.email()).isEqualTo("ana@uco.edu.co");
        assertThat(entity.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeMapearEntityADomain_cuandoSeLee() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var entity = new CoordinadorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var domain = CoordinadorMapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getIdentificador()).isEqualTo("20161020123");
        assertThat(domain.getNombre()).isEqualTo("Ana Perez");
        assertThat(domain.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(domain.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
