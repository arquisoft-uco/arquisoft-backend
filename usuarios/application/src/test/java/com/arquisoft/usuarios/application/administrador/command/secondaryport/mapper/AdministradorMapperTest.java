package com.arquisoft.usuarios.application.administrador.command.secondaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AdministradorMapperTest {

    @Test
    void debeConservarLosCampos_cuandoSeMapeaDeDomainAEntityYDeVuelta() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var administrador = AdministradorDomain.reconstruir(usuario, eliminadoEn);

        // Act
        var entity = AdministradorMapper.toEntity(administrador);
        var domain = AdministradorMapper.toDomain(entity);

        // Assert
        assertThat(entity.usuario()).isEqualTo(usuario);
        assertThat(entity.eliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(domain.getUsuario()).isEqualTo(usuario);
        assertThat(domain.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(domain.estaEliminado()).isTrue();
    }
}
