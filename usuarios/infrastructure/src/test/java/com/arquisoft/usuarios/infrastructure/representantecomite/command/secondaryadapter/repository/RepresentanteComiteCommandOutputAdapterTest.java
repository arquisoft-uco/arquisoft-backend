package com.arquisoft.usuarios.infrastructure.representantecomite.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarRepresentanteComiteKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.usuarios.infrastructure.representantecomite.command.secondaryadapter.entity.RepresentanteComiteJpaEntity;
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
class RepresentanteComiteCommandOutputAdapterTest {

    @Autowired
    private RepresentanteComiteCommandRepository representanteComiteCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private RepresentanteComiteCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RepresentanteComiteCommandOutputAdapter(representanteComiteCommandRepository, logger);
    }

    @Test
    void debeGuardarVigenteYLeerloDeVuelta_cuandoSePersiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        adapter.guardar(new RepresentanteComiteEntity(usuario, UtilFecha.VACIO));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(RepresentanteComiteJpaEntity.class, usuario).getEliminadoEn()).isNull();
        assertThat(adapter.obtenerPorUsuario(usuario)).map(RepresentanteComiteEntity::eliminadoEn)
                .contains(UtilFecha.VACIO);
        verify(logger).debug(AgregarRepresentanteComiteKey.LOG_GUARDADO, usuario);
    }

    @Test
    void debeDejarEliminadoEnNulo_cuandoSeReactiva() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        entityManager.persistAndFlush(RepresentanteComiteJpaEntity.builder()
                .usuarioId(usuario).eliminadoEn(eliminadoEn).build());
        var antes = adapter.obtenerPorUsuario(usuario);

        // Act
        adapter.reactivar(usuario);

        // Assert
        assertThat(antes).map(RepresentanteComiteEntity::eliminadoEn).contains(eliminadoEn);
        assertThat(entityManager.find(RepresentanteComiteJpaEntity.class, usuario).getEliminadoEn()).isNull();
        assertThat(adapter.obtenerPorUsuario(usuario)).map(RepresentanteComiteEntity::eliminadoEn)
                .contains(UtilFecha.VACIO);
        verify(logger).debug(AgregarRepresentanteComiteKey.LOG_ACTUALIZADO, usuario);
    }

    @Test
    void debeRetornarVacio_cuandoElUsuarioNoEsRepresentante() {
        // Act
        var representanteComite = adapter.obtenerPorUsuario(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(representanteComite).isEmpty();
    }
}
