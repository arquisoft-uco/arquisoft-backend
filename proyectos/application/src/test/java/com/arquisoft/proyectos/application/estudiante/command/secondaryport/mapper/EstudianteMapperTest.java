package com.arquisoft.proyectos.application.estudiante.command.secondaryport.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteMapperTest {

    @Test
    void debeMapearDomainAEntity_enEstudianteMapper() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var estudiante = EstudianteDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var entity = EstudianteMapper.toEntity(estudiante);

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
        var entity = new EstudianteEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, UtilFecha.VACIO);

        // Act
        var domain = EstudianteMapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getIdentificador()).isEqualTo("20161020123");
        assertThat(domain.getNombre()).isEqualTo("Ana Perez");
        assertThat(domain.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(domain.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
