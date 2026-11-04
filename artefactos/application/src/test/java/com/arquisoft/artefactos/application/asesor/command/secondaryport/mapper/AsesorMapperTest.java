package com.arquisoft.artefactos.application.asesor.command.secondaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.artefactos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorMapperTest {

    @Test
    void debeMapearDomainAEntity_enAsesorMapper() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();
        var asesor = AsesorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var entity = AsesorMapper.toEntity(asesor);

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
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();
        var entity = new AsesorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, ocurridoEn);

        // Act
        var domain = AsesorMapper.toDomain(entity);
        var deVuelta = AsesorMapper.toEntity(domain);

        // Assert
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getIdentificador()).isEqualTo("20161020123");
        assertThat(domain.getNombre()).isEqualTo("Ana Perez");
        assertThat(domain.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(domain.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(domain.getEliminadoEn()).isEqualTo(ocurridoEn);
        assertThat(domain.estaEliminado()).isTrue();
        assertThat(deVuelta.eliminadoEn()).isEqualTo(ocurridoEn);
    }
}
