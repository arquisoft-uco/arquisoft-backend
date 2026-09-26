package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class UsuarioCommandOutputAdapterPersistenciaTest {

    @Autowired
    private UsuarioCommandRepository usuarioCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UsuarioCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioCommandOutputAdapter(usuarioCommandRepository, mock(AppLogger.class));
    }

    private UUID sembrarUsuario(String estado, Instant eliminadoEn) {
        var usuario = UsuarioJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("20161020123")
                .nombre("Ana Perez")
                .email("ana@uco.edu.co")
                .contacto("573001112233")
                .estadoId(estado)
                .eliminadoEn(eliminadoEn)
                .build();
        entityManager.persistAndFlush(usuario);
        return usuario.getId();
    }

    @Test
    void debeEscribirEliminadoEnSinTocarElEstado_cuandoSeEliminaLogicamente() {
        // Arrange
        var id = sembrarUsuario("ACTIVO", null);
        var eliminadoEn = Instant.parse("2026-09-26T08:00:00Z");

        // Act
        adapter.eliminarLogica(id, eliminadoEn);

        // Assert
        var guardado = entityManager.find(UsuarioJpaEntity.class, id);
        assertThat(guardado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(guardado.getEstadoId()).isEqualTo("ACTIVO");
    }

    @Test
    void debeEscribirElEstadoSinTocarEliminadoEn_cuandoSeCambiaElEstado() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-25T10:15:30Z");
        var id = sembrarUsuario("ACTIVO", eliminadoEn);

        // Act
        adapter.cambiarEstado(id, "INACTIVO");

        // Assert
        var guardado = entityManager.find(UsuarioJpaEntity.class, id);
        assertThat(guardado.getEstadoId()).isEqualTo("INACTIVO");
        assertThat(guardado.getEliminadoEn()).isEqualTo(eliminadoEn);
    }

    @Test
    void debeTraducirColumnaNulaAVacio_cuandoElUsuarioEstaVigente() {
        // Arrange
        var id = sembrarUsuario("ACTIVO", null);

        // Act
        var usuario = adapter.obtenerPorId(id);

        // Assert
        assertThat(usuario).map(UsuarioEntity::eliminadoEn).contains(UtilFecha.VACIO);
    }
}
