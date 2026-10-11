package com.arquisoft.usuarios.application.asesorficha.command.secondaryport.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorFichaMapperTest {

    @Test
    void debeMapearDomainAEntity_enAsesorFichaMapper() {
        // Arrange
        var usuario = UUID.randomUUID();
        var asesorFicha = AsesorFichaDomain.crear(usuario);

        // Act
        var entity = AsesorFichaMapper.toEntity(asesorFicha);

        // Assert
        assertThat(entity.usuario()).isEqualTo(usuario);
    }

    @Test
    void debeMapearEntityADomain_cuandoSeLee() {
        // Arrange
        var usuario = UUID.randomUUID();
        var entity = new AsesorFichaEntity(usuario, UtilFecha.VACIO);

        // Act
        var domain = AsesorFichaMapper.toDomain(entity);

        // Assert
        assertThat(domain.getUsuario()).isEqualTo(usuario);
    }
}
