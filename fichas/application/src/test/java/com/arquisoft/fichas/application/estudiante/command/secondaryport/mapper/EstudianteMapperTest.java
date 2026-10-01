package com.arquisoft.fichas.application.estudiante.command.secondaryport.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteMapperTest {

    @Test
    void debeMaperarADominio_cuandoEntityEsValida() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var entity = new EstudianteEntity(
                id, "20161020123", "Juan Pérez", "juan.perez@example.com", ocurridoEn, UtilFecha.VACIO);

        // Act
        var aggregate = EstudianteMapper.toDomain(entity);

        // Assert
        assertThat(aggregate.getId()).isEqualTo(id);
        assertThat(aggregate.getIdentificador()).isEqualTo("20161020123");
        assertThat(aggregate.getNombre()).isEqualTo("Juan Pérez");
        assertThat(aggregate.getEmail()).isEqualTo("juan.perez@example.com");
        assertThat(aggregate.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeMaperarAEntity_cuandoAggregateEsValido() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var aggregate = EstudianteDomain.reconstruir(
                id,
                "20161020123",
                "Juan Pérez",
                "juan.perez@example.com",
                ocurridoEn
        , UtilFecha.VACIO);

        // Act
        var entity = EstudianteMapper.toEntity(aggregate);

        // Assert
        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.identificador()).isEqualTo("20161020123");
        assertThat(entity.nombre()).isEqualTo("Juan Pérez");
        assertThat(entity.email()).isEqualTo("juan.perez@example.com");
        assertThat(entity.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debePreservarId_cuandoMapeaIdaYVuelta() {
        // Arrange
        var idOriginal = UUID.randomUUID();
        var entityOriginal = new EstudianteEntity(
                idOriginal, "20161020123", "Juan Pérez", "juan.perez@example.com", Instant.now(), UtilFecha.VACIO);

        // Act
        var aggregate = EstudianteMapper.toDomain(entityOriginal);
        var entityMapeada = EstudianteMapper.toEntity(aggregate);

        // Assert
        assertThat(entityMapeada.id()).isEqualTo(idOriginal);
        assertThat(entityMapeada.identificador()).isEqualTo(entityOriginal.identificador());
        assertThat(entityMapeada.nombre()).isEqualTo(entityOriginal.nombre());
        assertThat(entityMapeada.email()).isEqualTo(entityOriginal.email());
        assertThat(entityMapeada.ocurridoEn()).isEqualTo(entityOriginal.ocurridoEn());
    }
}
