package com.arquisoft.notificaciones.infrastructure.notificacion.command.adapter.out.persistence;

import com.arquisoft.notificaciones.domain.notificacion.aggregate.NotificacionAggregate;
import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.notificaciones.infrastructure.notificacion.persistence.NotificacionEntity;
import com.arquisoft.notificaciones.infrastructure.notificacion.persistence.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class NotificacionCommandOutputAdapterTest {

    private static final String DESTINATARIO = "ana.gomez@soyuco.edu.co";
    private static final String ASUNTO = "Se te asignó la ficha";

    @Autowired
    private NotificacionRepository repository;

    private NotificacionCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new NotificacionCommandOutputAdapter(repository);
    }

    private NotificacionAggregate notificacionCon(String eventId) {
        return NotificacionAggregate.crear(
                eventId, TipoNotificacion.ASESOR_FICHA_CAMBIADO, DESTINATARIO, ASUNTO);
    }

    @Test
    void debePersistirTodosLosCampos_cuandoSeGuardaUnaNotificacionEnviada() {
        // Arrange
        String eventId = UUID.randomUUID().toString();
        NotificacionAggregate notificacion = notificacionCon(eventId);
        notificacion.marcarEnviada();

        // Act
        adapter.guardar(notificacion);

        // Assert
        NotificacionEntity guardada = repository.findById(notificacion.getId()).orElseThrow();
        assertThat(guardada.getEventId()).isEqualTo(eventId);
        assertThat(guardada.getTipo()).isEqualTo(TipoNotificacion.ASESOR_FICHA_CAMBIADO.getCodigo());
        assertThat(guardada.getDestinatario()).isEqualTo(DESTINATARIO);
        assertThat(guardada.getAsunto()).isEqualTo(ASUNTO);
        assertThat(guardada.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA.name());
        assertThat(guardada.getFechaEnvio()).isNotNull();
        assertThat(guardada.getDetalleError()).isNull();
    }

    @Test
    void debeConservarElMotivoDelFallo_cuandoSeGuardaUnaNotificacionFallida() {
        // Arrange
        NotificacionAggregate notificacion = notificacionCon(UUID.randomUUID().toString());
        notificacion.marcarFallida("servidor SMTP no disponible");

        // Act
        adapter.guardar(notificacion);

        // Assert
        NotificacionEntity guardada = repository.findById(notificacion.getId()).orElseThrow();
        assertThat(guardada.getEstado()).isEqualTo(EstadoNotificacion.FALLIDA.name());
        assertThat(guardada.getDetalleError()).isEqualTo("servidor SMTP no disponible");
    }

    @Test
    void debeReportarTrue_cuandoElEventoYaTieneNotificacion() {
        // Arrange
        String eventId = UUID.randomUUID().toString();
        adapter.guardar(notificacionCon(eventId));

        // Act & Assert
        assertThat(adapter.existePorEventId(eventId)).isTrue();
    }

    @Test
    void debeReportarFalse_cuandoElEventoNoTieneNotificacion() {
        // Act & Assert
        assertThat(adapter.existePorEventId(UUID.randomUUID().toString())).isFalse();
    }
}
