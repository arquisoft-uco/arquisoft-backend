package com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
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
class UsuarioSolicitudesCommandOutputAdapterTest {

    @Autowired
    private UsuarioSolicitudesCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private UsuarioSolicitudesCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioSolicitudesCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    @Test
    void debeInsertarLaReplica_cuandoGuardaUnUsuarioNuevo() {
        // Arrange
        UUID id = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();

        // Act
        adapter.guardar(new UsuarioEntity(id, "EST-1", "Ana", "ana@uco.edu.co", ocurridoEn));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(UsuarioJpaEntity.class, id).getNombre()).isEqualTo("Ana");
    }

    @Test
    void debeSobrescribirLosDatos_cuandoGuardaUnUsuarioExistente() {
        // Arrange
        UUID id = UUID.randomUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(id).identificador("OLD").nombre("Vieja").email("old@uco.edu.co")
                .ocurridoEn(Instant.now().minusSeconds(60)).build());
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.guardar(new UsuarioEntity(id, "NEW", "Nueva", "new@uco.edu.co", Instant.now()));
        entityManager.flush();
        entityManager.clear();

        // Assert
        UsuarioJpaEntity guardada = entityManager.find(UsuarioJpaEntity.class, id);
        assertThat(guardada.getNombre()).isEqualTo("Nueva");
        assertThat(guardada.getIdentificador()).isEqualTo("NEW");
    }

    @Test
    void debeDevolverLaReplica_cuandoBuscaPorId() {
        // Arrange
        UUID id = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(id).identificador("COORD-1").nombre("Pedro").email("pedro@uco.edu.co")
                .ocurridoEn(ocurridoEn).build());
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.buscarPorId(id))
                .hasValueSatisfying(u -> {
                    assertThat(u.nombre()).isEqualTo("Pedro");
                    assertThat(u.email()).isEqualTo("pedro@uco.edu.co");
                    assertThat(u.ocurridoEn()).isEqualTo(ocurridoEn);
                });
        assertThat(adapter.buscarPorId(UUID.randomUUID())).isEmpty();
    }

    @Test
    void debeActualizarLosDatosPersistidos_cuandoSeInvocaActualizar() {
        // Arrange
        UUID id = UUID.randomUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(id).identificador("EST-1").nombre("Ana").email("ana@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-09-01T10:00:00Z")).build());
        entityManager.flush();
        entityManager.clear();
        var nuevoOcurridoEn = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        adapter.actualizar(new UsuarioEntity(id, "EST-999", "Ana Actualizada", "actualizada@uco.edu.co",
                nuevoOcurridoEn));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardada = entityManager.find(UsuarioJpaEntity.class, id);
        assertThat(guardada.getIdentificador()).isEqualTo("EST-999");
        assertThat(guardada.getNombre()).isEqualTo("Ana Actualizada");
        assertThat(guardada.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
    }
}
