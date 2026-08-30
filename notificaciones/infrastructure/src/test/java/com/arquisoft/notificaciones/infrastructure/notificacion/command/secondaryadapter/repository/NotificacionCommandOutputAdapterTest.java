package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.repository;

import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;
import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.mapper.NotificacionMapper;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.entity.NotificacionJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class NotificacionCommandOutputAdapterTest {

    private static final String DESTINATARIO = "ana.gomez@soyuco.edu.co";
    private static final String ASUNTO = "Se te asignó la ficha";
    private static final String NOMBRE = "Ana Gomez";
    private static final String CUERPO = "Hola Ana, ahora eres la asesora.";

    @Autowired
    private NotificacionCommandRepository repository;

    private NotificacionCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new NotificacionCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    private NotificacionDomain notificacionCon(String idEvento) {
        return NotificacionDomain.crear(
                idEvento, TipoNotificacion.ASESOR_FICHA_CAMBIADO, DESTINATARIO, ASUNTO,
                NOMBRE, CUERPO);
    }

    @Test
    void debePersistirTodosLosCampos_cuandoSeGuardaUnaNotificacionEnviada() {
        // Arrange
        String idEvento = UUID.randomUUID().toString();
        NotificacionDomain notificacion = notificacionCon(idEvento);
        notificacion.marcarEnviada();

        // Act
        adapter.guardar(NotificacionMapper.toEntity(notificacion));

        // Assert
        NotificacionJpaEntity guardada = repository.findById(notificacion.getId()).orElseThrow();
        assertThat(guardada.getIdEvento()).isEqualTo(idEvento);
        assertThat(guardada.getTipo()).isEqualTo(TipoNotificacion.ASESOR_FICHA_CAMBIADO.getId());
        assertThat(guardada.getDestinatario()).isEqualTo(DESTINATARIO);
        assertThat(guardada.getAsunto()).isEqualTo(ASUNTO);
        assertThat(guardada.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA.name());
        assertThat(guardada.getFechaEnvio()).isNotNull();
        assertThat(guardada.getDetalleError()).isNull();
    }

    @Test
    void debeConservarElMotivoDelFallo_cuandoSeGuardaUnaNotificacionFallida() {
        // Arrange
        NotificacionDomain notificacion = notificacionCon(UUID.randomUUID().toString());
        notificacion.marcarFallida("servidor SMTP no disponible");

        // Act
        adapter.guardar(NotificacionMapper.toEntity(notificacion));

        // Assert
        NotificacionJpaEntity guardada = repository.findById(notificacion.getId()).orElseThrow();
        assertThat(guardada.getEstado()).isEqualTo(EstadoNotificacion.FALLIDA.name());
        assertThat(guardada.getDetalleError()).isEqualTo("servidor SMTP no disponible");
    }

    @Test
    void debeReportarTrue_cuandoElEventoYaTieneNotificacion() {
        // Arrange
        String idEvento = UUID.randomUUID().toString();
        adapter.guardar(NotificacionMapper.toEntity(notificacionCon(idEvento)));

        // Act & Assert
        assertThat(adapter.existePorIdEvento(idEvento)).isTrue();
    }

    @Test
    void debeReportarFalse_cuandoElEventoNoTieneNotificacion() {
        // Act & Assert
        assertThat(adapter.existePorIdEvento(UUID.randomUUID().toString())).isFalse();
    }

    @Test
    void debeDevolverSoloLasFallidasBajoElMaximoDeIntentos_cuandoSeBuscanReintentables() {
        // Arrange
        var fallida = notificacionCon(UUID.randomUUID().toString());
        fallida.marcarFallida("SMTP caido");
        adapter.guardar(NotificacionMapper.toEntity(fallida));

        var enviada = notificacionCon(UUID.randomUUID().toString());
        enviada.marcarEnviada();
        adapter.guardar(NotificacionMapper.toEntity(enviada));

        // Act
        var reintentables = adapter.buscarFallidasReintentables(5, 50);

        // Assert
        assertThat(reintentables).hasSize(1);
        assertThat(reintentables.getFirst().idEvento()).isEqualTo(fallida.getIdEvento());
        assertThat(reintentables.getFirst().cuerpo()).isEqualTo(CUERPO);
    }
}
