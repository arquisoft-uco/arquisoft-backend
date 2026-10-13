package com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.entity.AdministradorEntity;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AdministradorMapperTest {

    @Test
    void debeMapearDomainAEntity_enAdministradorMapper() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();
        var administrador = AdministradorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var entity = AdministradorMapper.toEntity(administrador);

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
        var entity = new AdministradorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, ocurridoEn);

        // Act
        var domain = AdministradorMapper.toDomain(entity);
        var deVuelta = AdministradorMapper.toEntity(domain);

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
