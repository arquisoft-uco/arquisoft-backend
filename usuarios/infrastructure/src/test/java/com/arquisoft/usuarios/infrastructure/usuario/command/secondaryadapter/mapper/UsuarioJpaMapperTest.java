package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.mapper;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioJpaMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoConvierteAJpaEntity() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = new UsuarioEntity(id, "usr001", "Ana Pérez", "ana@uco.edu.co", "573001112233", "ACTIVO");

        // Act
        var jpaEntity = UsuarioJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpaEntity.getId()).isEqualTo(id);
        assertThat(jpaEntity.getIdentificador()).isEqualTo("usr001");
        assertThat(jpaEntity.getNombre()).isEqualTo("Ana Pérez");
        assertThat(jpaEntity.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(jpaEntity.getContacto()).isEqualTo("573001112233");
        assertThat(jpaEntity.getEstadoId()).isEqualTo("ACTIVO");
    }
}
