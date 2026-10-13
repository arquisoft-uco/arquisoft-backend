package com.arquisoft.repositorio_artefactos.application.administrador.command.result.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AgregacionAdministradorResultMapperTest {

    private final AdministradorDomain administrador = AdministradorDomain.crear(
            UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());

    @Test
    void debeMapearAgregada_enAgregacionAdministradorResultMapper() {
        // Act
        var resultado = AgregacionAdministradorResultMapper.toResultAgregada(administrador);

        // Assert
        assertThat(resultado.administrador()).isEqualTo(administrador.getId());
    }

    @Test
    void debeMapearDuplicada_enAgregacionAdministradorResultMapper() {
        // Act
        var resultado = AgregacionAdministradorResultMapper.toResultDuplicada(administrador);

        // Assert
        assertThat(resultado.administrador()).isEqualTo(administrador.getId());
    }

    @Test
    void debeMapearDescartada_enAgregacionAdministradorResultMapper() {
        // Arrange
        var vigente = Instant.now();

        // Act
        var resultado = AgregacionAdministradorResultMapper.toResultDescartada(administrador, vigente);

        // Assert
        assertThat(resultado.administrador()).isEqualTo(administrador.getId());
        assertThat(resultado.ocurridoEnVigente()).isEqualTo(vigente);
    }
}
