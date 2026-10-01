package com.arquisoft.solicitudes.application.usuario.command.result.mapper;

import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgregacionUsuarioResultMapperTest {

    private UsuarioDomain usuario(UUID id, Instant ocurridoEn) {
        return UsuarioDomain.crear(id, "EST-9", "Nombre Completo", "n@uco.edu.co", ocurridoEn);
    }

    @Test
    void debeMapearAgregada_cuandoToResultAgregadaEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();

        // Act
        var resultado = AgregacionUsuarioResultMapper.toResultAgregada(usuario(id, Instant.now()));

        // Assert
        assertThat(resultado.usuario()).isEqualTo(id);
    }

    @Test
    void debeMapearDuplicada_cuandoToResultDuplicadaEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();

        // Act
        var resultado = AgregacionUsuarioResultMapper.toResultDuplicada(usuario(id, Instant.now()));

        // Assert
        assertThat(resultado.usuario()).isEqualTo(id);
    }

    @Test
    void debeMapearDescartada_cuandoToResultDescartadaEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now();

        // Act
        var resultado = AgregacionUsuarioResultMapper.toResultDescartada(usuario(id, Instant.now()), vigente);

        // Assert
        assertThat(resultado.usuario()).isEqualTo(id);
        assertThat(resultado.ocurridoEnVigente()).isEqualTo(vigente);
    }
}
