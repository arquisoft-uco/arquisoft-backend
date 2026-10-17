package com.arquisoft.usuarios.infrastructure.jurado.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarJuradoKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.jurado.command.secondaryport.entity.JuradoEntity;
import com.arquisoft.usuarios.infrastructure.jurado.command.secondaryadapter.entity.JuradoJpaEntity;
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
class JuradoCommandOutputAdapterTest {

    @Autowired
    private JuradoCommandRepository juradoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private JuradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JuradoCommandOutputAdapter(juradoCommandRepository, logger);
    }

    @Test
    void debeGuardarVigenteYLeerloDeVuelta_cuandoSePersiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        adapter.guardar(new JuradoEntity(usuario, UtilFecha.VACIO));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(JuradoJpaEntity.class, usuario).getEliminadoEn()).isNull();
        assertThat(adapter.obtenerPorUsuario(usuario)).map(JuradoEntity::eliminadoEn)
                .contains(UtilFecha.VACIO);
        verify(logger).debug(AgregarJuradoKey.LOG_GUARDADO, usuario);
    }

    @Test
    void debeDejarEliminadoEnNuloSoloDeEseUsuario_cuandoSeReactiva() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        entityManager.persistAndFlush(JuradoJpaEntity.builder()
                .usuarioId(usuario).eliminadoEn(eliminadoEn).build());
        entityManager.persistAndFlush(JuradoJpaEntity.builder()
                .usuarioId(otro).eliminadoEn(eliminadoEn).build());
        var antes = adapter.obtenerPorUsuario(usuario);

        // Act
        adapter.reactivar(usuario);

        // Assert
        assertThat(antes).map(JuradoEntity::eliminadoEn).contains(eliminadoEn);
        assertThat(entityManager.find(JuradoJpaEntity.class, usuario).getEliminadoEn()).isNull();
        assertThat(adapter.obtenerPorUsuario(usuario)).map(JuradoEntity::eliminadoEn)
                .contains(UtilFecha.VACIO);
        assertThat(adapter.obtenerPorUsuario(otro)).map(JuradoEntity::eliminadoEn).contains(eliminadoEn);
        verify(logger).debug(AgregarJuradoKey.LOG_ACTUALIZADO, usuario);
    }

    @Test
    void debeMarcarEliminadoEnSoloDeEseUsuario_cuandoSeEliminaLogica() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-10-12T10:00:00Z");
        entityManager.persistAndFlush(JuradoJpaEntity.builder().usuarioId(usuario).build());
        entityManager.persistAndFlush(JuradoJpaEntity.builder().usuarioId(otro).build());

        // Act
        adapter.eliminarLogica(usuario, eliminadoEn);

        // Assert
        assertThat(entityManager.find(JuradoJpaEntity.class, usuario).getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(entityManager.find(JuradoJpaEntity.class, otro).getEliminadoEn()).isNull();
        assertThat(adapter.obtenerPorUsuario(usuario)).map(JuradoEntity::eliminadoEn).contains(eliminadoEn);
        verify(logger).debug(AgregarJuradoKey.LOG_ACTUALIZADO, usuario);
    }

    @Test
    void debeRetornarVacio_cuandoElUsuarioNoEsJurado() {
        // Act
        var jurado = adapter.obtenerPorUsuario(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(jurado).isEmpty();
    }
}
