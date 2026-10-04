package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioJpaMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoConvierteAJpaEntity() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = new UsuarioEntity(id, "usr001", "Ana Pérez", "ana@uco.edu.co", "573001112233", "ACTIVO", UtilFecha.VACIO);

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

    @Test
    void debeTraducirVacioANuloYViceversa_cuandoMapeaEliminadoEn() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-25T10:15:30Z");
        var vigente = new UsuarioEntity(UUID.randomUUID(), "usr001", "Ana Pérez", "ana@uco.edu.co",
                "573001112233", "ACTIVO", UtilFecha.VACIO);
        var eliminado = new UsuarioEntity(UUID.randomUUID(), "usr002", "Juan Pérez", "juan@uco.edu.co",
                "573004445566", "INACTIVO", eliminadoEn);

        // Act
        var jpaVigente = UsuarioJpaMapper.toJpaEntity(vigente);
        var jpaEliminado = UsuarioJpaMapper.toJpaEntity(eliminado);
        var deVuelta = UsuarioJpaMapper.toEntity(jpaVigente);

        // Assert
        assertThat(jpaVigente.getEliminadoEn()).isNull();
        assertThat(jpaEliminado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(deVuelta.eliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(UsuarioJpaMapper.toEntity(jpaEliminado).eliminadoEn()).isEqualTo(eliminadoEn);
    }
}
