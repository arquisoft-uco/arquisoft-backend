package com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class UsuarioCommandOutputAdapterTest {

    @Autowired
    private UsuarioCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private UsuarioCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    @Test
    void debeInsertarLaReplica_cuandoRegistra() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        adapter.registrar(new UsuarioEntity(id, "EST-1", "Ana", "ana@uco.edu.co"));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(UsuarioJpaEntity.class, id).getNombre()).isEqualTo("Ana");
    }

    @Test
    void debeSobrescribirLosDatos_cuandoActualiza() {
        // Arrange
        UUID id = UUID.randomUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(id).identificador("OLD").nombre("Vieja").email("old@uco.edu.co").build());
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.actualizar(new UsuarioEntity(id, "NEW", "Nueva", "new@uco.edu.co"));
        entityManager.flush();
        entityManager.clear();

        // Assert
        UsuarioJpaEntity guardada = entityManager.find(UsuarioJpaEntity.class, id);
        assertThat(guardada.getNombre()).isEqualTo("Nueva");
        assertThat(guardada.getIdentificador()).isEqualTo("NEW");
    }

    @Test
    void debeReportarExistencia_cuandoSeConsultaPorId() {
        // Arrange
        UUID id = UUID.randomUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(id).identificador("EST-2").nombre("Beto").email("beto@uco.edu.co").build());
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existePorId(id)).isTrue();
        assertThat(adapter.existePorId(UUID.randomUUID())).isFalse();
    }
}
