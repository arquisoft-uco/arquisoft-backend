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

import java.time.LocalDateTime;
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

    @BeforeEach
    void setUp() {
        adapter = new SolicitudCommandOutputAdapter(repository, mock(AppLogger.class));

        ejecutarSql("CREATE TABLE IF NOT EXISTS respuesta ("
                + "id UUID PRIMARY KEY, solicitud_id UUID NOT NULL, fecha_respuesta TIMESTAMP, "
                + "contenido VARCHAR(100), estado_respuesta_id VARCHAR(60))");

        entityManager.persist(TipoSolicitudJpaEntity.builder()
                .id(TIPO).nombre("Novedad para el Coordinador").descripcion("desc").build());

        remitenteFila = UUID.randomUUID();
        remitenteUsuarioId = UUID.randomUUID();
        destinatarioFila = UUID.randomUUID();
        entityManager.persist(RemitenteJpaEntity.builder()
                .id(remitenteFila).usuarioId(remitenteUsuarioId).build());
        entityManager.persist(DestinatarioJpaEntity.builder()
                .id(destinatarioFila).usuarioId(UUID.randomUUID()).build());
        entityManager.flush();
    }

    private void ejecutarSql(String sql) {
        entityManager.getEntityManager()
                .unwrap(org.hibernate.Session.class)
                .doWork(connection -> {
                    try (var statement = connection.createStatement()) {
                        statement.execute(sql);
                    }
                });
    }

    private UUID sembrarSolicitud(String mensaje) {
        UUID solicitudId = UUID.randomUUID();
        adapter.registrar(new SolicitudEntity(solicitudId, destinatarioFila, remitenteFila,
                LocalDateTime.of(2026, 2, 1, 10, 30, 0), mensaje, TIPO));
        entityManager.flush();
        entityManager.clear();
        return solicitudId;
    }

    @Test
    void debeInsertarLaSolicitudConSusClavesForaneas_cuandoRegistra() {
        // Arrange
        UUID solicitudId = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 1, 10, 30, 0);

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
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 1, 10, 30, 0);
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
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 1, 10, 30, 0);
        adapter.registrar(new SolicitudEntity(
                UUID.randomUUID(), destinatarioFila, remitenteFila, fecha, "original", TIPO));
        entityManager.flush();
        entityManager.clear();

        // Act & Assert — mismo triple, mensaje distinto
        assertThat(adapter.existePorCombinacionUnica(destinatarioFila, remitenteFila, fecha, "otro mensaje"))
                .isFalse();
    }

    @Test
    void debeProyectarRemitenteUsuarioYTipo_cuandoBuscaDatosDeUnaSolicitudExistente() {
        // Arrange
        UUID solicitudId = sembrarSolicitud("una novedad");

        // Act & Assert
        assertThat(adapter.buscarDatos(solicitudId)).hasValueSatisfying(datos -> {
            assertThat(datos.remitenteUsuario()).isEqualTo(remitenteUsuarioId);
            assertThat(datos.tipoSolicitud()).isEqualTo(TIPO);
        });
    }

    @Test
    void debeRetornarVacio_cuandoBuscaDatosDeUnIdInexistente() {
        // Act & Assert
        assertThat(adapter.buscarDatos(UUID.randomUUID())).isEmpty();
    }

    @Test
    void debeRetornarFalse_cuandoLaSolicitudNoTieneRespuestas() {
        // Arrange
        UUID solicitudId = sembrarSolicitud("sin respuestas");

        // Act & Assert
        assertThat(adapter.tieneRespuestas(solicitudId)).isFalse();
    }

    @Test
    void debeRetornarTrue_cuandoLaSolicitudTieneUnaRespuesta() {
        // Arrange
        UUID solicitudId = sembrarSolicitud("con respuesta");
        entityManager.getEntityManager().createNativeQuery(
                        "INSERT INTO respuesta (id, solicitud_id, fecha_respuesta, contenido, "
                        + "estado_respuesta_id) VALUES (:id, :sol, CURRENT_TIMESTAMP, 'r', 'EN_REVISION')")
                .setParameter("id", UUID.randomUUID())
                .setParameter("sol", solicitudId)
                .executeUpdate();

        // Act & Assert
        assertThat(adapter.tieneRespuestas(solicitudId)).isTrue();
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
