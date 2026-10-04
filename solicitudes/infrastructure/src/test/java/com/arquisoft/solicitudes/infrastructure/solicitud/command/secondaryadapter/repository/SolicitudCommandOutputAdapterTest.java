package com.arquisoft.solicitudes.infrastructure.solicitud.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.SolicitudEntity;
import com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.entity.DestinatarioJpaEntity;
import com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.entity.RemitenteJpaEntity;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.secondaryadapter.entity.SolicitudJpaEntity;
import com.arquisoft.solicitudes.infrastructure.tiposolicitud.command.secondaryadapter.entity.TipoSolicitudJpaEntity;
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
class SolicitudCommandOutputAdapterTest {

    private static final String TIPO = "NOVEDAD_PARA_EL_COORDINADOR";

    @Autowired
    private SolicitudCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private SolicitudCommandOutputAdapter adapter;

    private UUID remitenteFila;
    private UUID remitenteUsuarioId;
    private UUID destinatarioFila;
    private UUID destinatarioUsuarioId;

    @BeforeEach
    void setUp() {
        adapter = new SolicitudCommandOutputAdapter(repository, mock(AppLogger.class));

        entityManager.persist(TipoSolicitudJpaEntity.builder()
                .id(TIPO).nombre("Novedad para el Coordinador").descripcion("desc").build());

        remitenteFila = UUID.randomUUID();
        remitenteUsuarioId = UUID.randomUUID();
        destinatarioFila = UUID.randomUUID();
        destinatarioUsuarioId = UUID.randomUUID();
        entityManager.persist(RemitenteJpaEntity.builder()
                .id(remitenteFila).usuarioId(remitenteUsuarioId).build());
        entityManager.persist(DestinatarioJpaEntity.builder()
                .id(destinatarioFila).usuarioId(destinatarioUsuarioId).build());
        entityManager.flush();
    }

    private UUID sembrarSolicitud(String mensaje) {
        UUID solicitudId = UUID.randomUUID();
        adapter.registrar(new SolicitudEntity(solicitudId, destinatarioFila, remitenteFila,
                Instant.parse("2026-02-01T10:30:00Z"), mensaje, TIPO));
        entityManager.flush();
        entityManager.clear();
        return solicitudId;
    }

    @Test
    void debeInsertarLaSolicitudConSusClavesForaneas_cuandoRegistra() {
        // Arrange
        UUID solicitudId = UUID.randomUUID();
        Instant fecha = Instant.parse("2026-02-01T10:30:00Z");

        // Act
        adapter.registrar(new SolicitudEntity(
                solicitudId, destinatarioFila, remitenteFila, fecha, "una novedad", TIPO));
        entityManager.flush();
        entityManager.clear();

        // Assert
        SolicitudJpaEntity guardada = entityManager.find(SolicitudJpaEntity.class, solicitudId);
        assertThat(guardada).isNotNull();
        assertThat(guardada.getMensajeSolicitud()).isEqualTo("una novedad");
        assertThat(guardada.getRemitente().getId()).isEqualTo(remitenteFila);
        assertThat(guardada.getDestinatario().getId()).isEqualTo(destinatarioFila);
        assertThat(guardada.getTipoSolicitud().getId()).isEqualTo(TIPO);
    }

    @Test
    void debeRetornarTrue_cuandoYaExisteLaCombinacionUnica() {
        // Arrange
        Instant fecha = Instant.parse("2026-02-01T10:30:00Z");
        adapter.registrar(new SolicitudEntity(
                UUID.randomUUID(), destinatarioFila, remitenteFila, fecha, "duplicable", TIPO));
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.existePorCombinacionUnica(destinatarioFila, remitenteFila, fecha, "duplicable"))
                .isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoLaCombinacionNoCoincide() {
        // Arrange
        Instant fecha = Instant.parse("2026-02-01T10:30:00Z");
        adapter.registrar(new SolicitudEntity(
                UUID.randomUUID(), destinatarioFila, remitenteFila, fecha, "original", TIPO));
        entityManager.flush();
        entityManager.clear();

        // Act & Assert — mismo triple, mensaje distinto
        assertThat(adapter.existePorCombinacionUnica(destinatarioFila, remitenteFila, fecha, "otro mensaje"))
                .isFalse();
    }

    @Test
    void debeProyectarUsuariosYTipo_cuandoBuscaDatosDeUnaSolicitudExistente() {
        // Arrange
        UUID solicitudId = sembrarSolicitud("una novedad");

        // Act & Assert
        assertThat(adapter.buscarDatos(solicitudId)).hasValueSatisfying(datos -> {
            assertThat(datos.remitenteUsuario()).isEqualTo(remitenteUsuarioId);
            assertThat(datos.destinatarioUsuario()).isEqualTo(destinatarioUsuarioId);
            assertThat(datos.tipoSolicitud()).isEqualTo(TIPO);
        });
    }

    @Test
    void debeRetornarVacio_cuandoBuscaDatosDeUnIdInexistente() {
        // Act & Assert
        assertThat(adapter.buscarDatos(UUID.randomUUID())).isEmpty();
    }

    @Test
    void debeBorrarLaFila_cuandoElimina() {
        // Arrange
        UUID solicitudId = sembrarSolicitud("a eliminar");

        // Act
        adapter.eliminar(solicitudId);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(SolicitudJpaEntity.class, solicitudId)).isNull();
    }
}
