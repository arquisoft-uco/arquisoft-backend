package com.arquisoft.usuarios.infrastructure.bibliotecario.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarBibliotecarioKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.entity.BibliotecarioEntity;
import com.arquisoft.usuarios.infrastructure.bibliotecario.command.secondaryadapter.entity.BibliotecarioJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class BibliotecarioCommandOutputAdapterTest {

    @Autowired
    private BibliotecarioCommandRepository bibliotecarioCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private BibliotecarioCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BibliotecarioCommandOutputAdapter(bibliotecarioCommandRepository, logger);
    }

    @Test
    void debeGuardarVigenteYLeerloDeVuelta_cuandoSePersiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        adapter.guardar(new BibliotecarioEntity(usuario, UtilFecha.VACIO));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(BibliotecarioJpaEntity.class, usuario).getEliminadoEn()).isNull();
        assertThat(adapter.obtenerPorUsuario(usuario)).map(BibliotecarioEntity::eliminadoEn)
                .contains(UtilFecha.VACIO);
        verify(logger).debug(AgregarBibliotecarioKey.LOG_GUARDADO, usuario);
    }

    @Test
    void debeDejarEliminadoEnNuloSoloDeEseUsuario_cuandoSeReactiva() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        entityManager.persistAndFlush(BibliotecarioJpaEntity.builder()
                .usuarioId(usuario).eliminadoEn(eliminadoEn).build());
        entityManager.persistAndFlush(BibliotecarioJpaEntity.builder()
                .usuarioId(otro).eliminadoEn(eliminadoEn).build());
        var antes = adapter.obtenerPorUsuario(usuario);

        // Act
        adapter.reactivar(usuario);

        // Assert
        assertThat(antes).map(BibliotecarioEntity::eliminadoEn).contains(eliminadoEn);
        assertThat(entityManager.find(BibliotecarioJpaEntity.class, usuario).getEliminadoEn()).isNull();
        assertThat(adapter.obtenerPorUsuario(usuario)).map(BibliotecarioEntity::eliminadoEn)
                .contains(UtilFecha.VACIO);
        assertThat(adapter.obtenerPorUsuario(otro)).map(BibliotecarioEntity::eliminadoEn).contains(eliminadoEn);
        verify(logger).debug(AgregarBibliotecarioKey.LOG_ACTUALIZADO, usuario);
    }

    @Test
    void debeRetornarVacio_cuandoElUsuarioNoEsBibliotecario() {
        // Act
        var bibliotecario = adapter.obtenerPorUsuario(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(bibliotecario).isEmpty();
    }
}
