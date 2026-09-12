package com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.entity.RespuestaEntity;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.EstadoRespuestaJpaEntity;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.RespuestaJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class RespuestaCommandOutputAdapterTest {

    private static final String ESTADO = "EN_REVISION";

    @Autowired
    private RespuestaCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private RespuestaCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RespuestaCommandOutputAdapter(repository, mock(AppLogger.class));

        entityManager.persist(EstadoRespuestaJpaEntity.builder()
                .id(ESTADO).nombre("En revisión").descripcion("desc").build());
        entityManager.flush();
    }

    @Test
    void debeInsertarLaFilaEnRevision_cuandoRegistra() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID solicitudId = UUID.randomUUID();

        // Act
        adapter.registrar(new RespuestaEntity(
                id, solicitudId, LocalDateTime.of(2026, 3, 1, 9, 0, 0), "una respuesta", ESTADO));
        entityManager.flush();
        entityManager.clear();

        // Assert
        RespuestaJpaEntity guardada = entityManager.find(RespuestaJpaEntity.class, id);
        assertThat(guardada).isNotNull();
        assertThat(guardada.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(guardada.getContenido()).isEqualTo("una respuesta");
        assertThat(guardada.getEstadoRespuesta().getId()).isEqualTo(ESTADO);
    }

    @Test
    void debeRetornarTrue_cuandoLaSolicitudYaTieneRespuesta() {
        // Arrange
        UUID solicitudId = UUID.randomUUID();
        adapter.registrar(new RespuestaEntity(
                UUID.randomUUID(), solicitudId, LocalDateTime.of(2026, 3, 1, 9, 0, 0), "r", ESTADO));
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.existePorSolicitud(solicitudId)).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoLaSolicitudNoTieneRespuesta() {
        // Act & Assert
        assertThat(adapter.existePorSolicitud(UUID.randomUUID())).isFalse();
    }
}
