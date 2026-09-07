package com.arquisoft.evaluaciones.infrastructure.entregableproyectoacceso.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.entity.EntregableProyectoAccesoEntity;
import com.arquisoft.evaluaciones.infrastructure.entregableproyectoacceso.command.secondaryadapter.entity.EntregableProyectoAccesoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class EntregableProyectoAccesoCommandOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EntregableProyectoAccesoCommandRepository repository;

    private EntregableProyectoAccesoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EntregableProyectoAccesoCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    @Test
    void debeRetornarVacio_cuandoNoExisteProyeccionParaElEntregable() {
        // Act & Assert
        assertThat(adapter.buscarPorEntregable(UUID.randomUUID())).isEmpty();
    }

    @Test
    void debeGuardarYPermitirEncontrarLaProyeccion_cuandoSeGuardaUnaNueva() {
        // Arrange
        UUID entregable = UUID.randomUUID();
        UUID proyecto = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();
        EntregableProyectoAccesoEntity entity =
                new EntregableProyectoAccesoEntity(entregable, proyecto, 1, true, ocurridoEn);

        // Act
        adapter.guardar(entity);
        entityManager.flush();
        entityManager.clear();
        Optional<EntregableProyectoAccesoEntity> encontrado = adapter.buscarPorEntregable(entregable);

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().proyecto()).isEqualTo(proyecto);
        assertThat(encontrado.get().versionEntregable()).isEqualTo(1);
        assertThat(encontrado.get().activo()).isTrue();
    }

    @Test
    void debeActualizarLaFilaExistente_cuandoSeGuardaConElMismoEntregable() {
        // Arrange
        UUID entregable = UUID.randomUUID();
        UUID proyecto = UUID.randomUUID();
        entityManager.persist(EntregableProyectoAccesoJpaEntity.builder()
                .entregable(entregable)
                .proyecto(proyecto)
                .versionEntregable(1)
                .activo(true)
                .ocurridoEn(Instant.now().minusSeconds(120))
                .build());
        entityManager.flush();

        Instant nuevoOcurridoEn = Instant.now();

        // Act
        adapter.guardar(new EntregableProyectoAccesoEntity(entregable, proyecto, 2, true, nuevoOcurridoEn));
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<EntregableProyectoAccesoEntity> encontrado = adapter.buscarPorEntregable(entregable);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().versionEntregable()).isEqualTo(2);
    }
}
