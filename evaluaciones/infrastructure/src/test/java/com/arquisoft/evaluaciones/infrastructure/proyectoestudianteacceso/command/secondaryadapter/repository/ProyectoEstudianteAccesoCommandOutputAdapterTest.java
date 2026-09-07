package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.ProyectoEstudianteAccesoEntity;
import com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.entity.ProyectoEstudianteAccesoJpaEntity;
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
class ProyectoEstudianteAccesoCommandOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProyectoEstudianteAccesoCommandRepository repository;

    private ProyectoEstudianteAccesoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProyectoEstudianteAccesoCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    @Test
    void debeRetornarVacio_cuandoNoExisteMembresiaParaElParProyectoEstudiante() {
        // Act & Assert
        assertThat(adapter.buscarPorProyectoYEstudiante(UUID.randomUUID(), UUID.randomUUID())).isEmpty();
    }

    @Test
    void debeGuardarYPermitirEncontrarLaMembresia_cuandoSeGuardaUnaNueva() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();
        ProyectoEstudianteAccesoEntity entity =
                new ProyectoEstudianteAccesoEntity(proyecto, estudiante, true, ocurridoEn);

        // Act
        adapter.guardar(entity);
        entityManager.flush();
        entityManager.clear();
        Optional<ProyectoEstudianteAccesoEntity> encontrado =
                adapter.buscarPorProyectoYEstudiante(proyecto, estudiante);

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().activo()).isTrue();
    }

    @Test
    void debeActualizarLaFilaExistente_cuandoLlegaUnaDestitucionParaLaMismaClaveCompuesta() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        entityManager.persist(ProyectoEstudianteAccesoJpaEntity.builder()
                .proyecto(proyecto)
                .estudiante(estudiante)
                .activo(true)
                .ocurridoEn(Instant.now().minusSeconds(120))
                .build());
        entityManager.flush();

        // Act
        adapter.guardar(new ProyectoEstudianteAccesoEntity(proyecto, estudiante, false, Instant.now()));
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<ProyectoEstudianteAccesoEntity> encontrado =
                adapter.buscarPorProyectoYEstudiante(proyecto, estudiante);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().activo()).isFalse();
    }
}
