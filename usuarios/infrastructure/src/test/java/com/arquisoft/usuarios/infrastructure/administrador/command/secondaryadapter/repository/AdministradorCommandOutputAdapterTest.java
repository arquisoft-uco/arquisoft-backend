package com.arquisoft.usuarios.infrastructure.administrador.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarAdministradorKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.administrador.command.secondaryport.entity.AdministradorEntity;
import com.arquisoft.usuarios.infrastructure.administrador.command.secondaryadapter.entity.AdministradorJpaEntity;
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
class AdministradorCommandOutputAdapterTest {

    @Autowired
    private AdministradorCommandRepository administradorCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private AdministradorCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AdministradorCommandOutputAdapter(administradorCommandRepository, logger);
    }

    @Test
    void debeGuardarVigenteYLeerloDeVuelta_cuandoSePersiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        adapter.guardar(new AdministradorEntity(usuario, UtilFecha.VACIO));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(AdministradorJpaEntity.class, usuario).getEliminadoEn()).isNull();
        assertThat(adapter.obtenerPorUsuario(usuario)).map(AdministradorEntity::eliminadoEn)
                .contains(UtilFecha.VACIO);
        verify(logger).debug(AgregarAdministradorKey.LOG_GUARDADO, usuario);
    }

    @Test
    void debeDejarEliminadoEnNulo_cuandoSeReactiva() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        entityManager.persistAndFlush(AdministradorJpaEntity.builder()
                .usuarioId(usuario).eliminadoEn(eliminadoEn).build());
        var antes = adapter.obtenerPorUsuario(usuario);

        // Act
        adapter.reactivar(usuario);

        // Assert
        assertThat(antes).map(AdministradorEntity::eliminadoEn).contains(eliminadoEn);
        assertThat(entityManager.find(AdministradorJpaEntity.class, usuario).getEliminadoEn()).isNull();
        assertThat(adapter.obtenerPorUsuario(usuario)).map(AdministradorEntity::eliminadoEn)
                .contains(UtilFecha.VACIO);
        verify(logger).debug(AgregarAdministradorKey.LOG_ACTUALIZADO, usuario);
    }

    @Test
    void debeRetornarVacio_cuandoElUsuarioNoEsAdministrador() {
        // Act
        var administrador = adapter.obtenerPorUsuario(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(administrador).isEmpty();
    }
}
