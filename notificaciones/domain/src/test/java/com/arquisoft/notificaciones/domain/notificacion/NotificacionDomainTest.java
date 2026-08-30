package com.arquisoft.notificaciones.domain.notificacion;

import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.constant.NotificacionesFields;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class NotificacionDomainTest {

    private static final String ID_EVENTO = "8f14e45f-ceea-467a-9575-1a1b2c3d4e5f";
    private static final String DESTINATARIO = "ana.gomez@soyuco.edu.co";
    private static final String ASUNTO = "Se te asignó la ficha";
    private static final String NOMBRE = "Ana Gomez";
    private static final String CUERPO = "Hola Ana, ahora eres la asesora.";

    private NotificacionDomain notificacionValida() {
        return NotificacionDomain.crear(
                ID_EVENTO, TipoNotificacion.ASESOR_FICHA_CAMBIADO, DESTINATARIO, ASUNTO,
                NOMBRE, CUERPO);
    }

    @Test
    void debeNacerPendiente_cuandoLosDatosSonValidos() {
        // Act
        NotificacionDomain notificacion = notificacionValida();

        // Assert
        assertThat(notificacion.getId()).isNotNull();
        assertThat(notificacion.getIdEvento()).isEqualTo(ID_EVENTO);
        assertThat(notificacion.getTipo()).isEqualTo(TipoNotificacion.ASESOR_FICHA_CAMBIADO);
        assertThat(notificacion.getDestinatario()).isEqualTo(DESTINATARIO);
        assertThat(notificacion.getAsunto()).isEqualTo(ASUNTO);
        assertThat(notificacion.getEstado()).isEqualTo(EstadoNotificacion.PENDIENTE);
        assertThat(notificacion.getFechaCreacion()).isNotNull();
        assertThat(notificacion.getFechaEnvio()).isNull();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElEventIdEstaEnBlanco() {
        // Act
        Throwable excepcion = catchThrowable(() -> NotificacionDomain.crear(
                "  ", TipoNotificacion.ASESOR_FICHA_CAMBIADO, DESTINATARIO, ASUNTO,
                NOMBRE, CUERPO));

        // Assert
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        assertThat(((DomainValidationException) excepcion).getValidationResult().getErrores())
                .anySatisfy(error -> {
                    assertThat(error.campo()).isEqualTo(NotificacionesFields.Notificacion.ID_EVENTO);
                    assertThat(error.codigoError())
                            .isEqualTo(NotificacionesCodes.Notificacion.ID_EVENTO_REQUERIDO);
                });
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElTipoEsNulo() {
        // Act
        Throwable excepcion = catchThrowable(() ->
                NotificacionDomain.crear(ID_EVENTO, null, DESTINATARIO, ASUNTO, NOMBRE, CUERPO));

        // Assert
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        assertThat(((DomainValidationException) excepcion).getValidationResult().getErrores())
                .anySatisfy(error -> assertThat(error.codigoError())
                        .isEqualTo(NotificacionesCodes.Notificacion.TIPO_REQUERIDO));
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElDestinatarioNoEsUnCorreo() {
        // Act
        Throwable excepcion = catchThrowable(() -> NotificacionDomain.crear(
                ID_EVENTO, TipoNotificacion.ASESOR_FICHA_CAMBIADO, "no-es-un-correo", ASUNTO,
                NOMBRE, CUERPO));

        // Assert
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        assertThat(((DomainValidationException) excepcion).getValidationResult().getErrores())
                .anySatisfy(error -> assertThat(error.codigoError())
                        .isEqualTo(NotificacionesCodes.Notificacion.DESTINATARIO_INVALIDO));
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElAsuntoEstaEnBlanco() {
        // Act
        Throwable excepcion = catchThrowable(() -> NotificacionDomain.crear(
                ID_EVENTO, TipoNotificacion.ASESOR_FICHA_CAMBIADO, DESTINATARIO, "  ",
                NOMBRE, CUERPO));

        // Assert
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        assertThat(((DomainValidationException) excepcion).getValidationResult().getErrores())
                .anySatisfy(error -> assertThat(error.codigoError())
                        .isEqualTo(NotificacionesCodes.Notificacion.ASUNTO_REQUERIDO));
    }

    @Test
    void debeAcumularVariosErrores_cuandoFallanVariosCampos() {
        // Act — la validación de integridad acumula, no corta en el primer error
        Throwable excepcion = catchThrowable(() ->
                NotificacionDomain.crear(null, null, null, null, null, null));

        // Assert
        assertThat(((DomainValidationException) excepcion).getValidationResult().getErrores())
                .hasSize(6);
    }

    @Test
    void debeQuedarEnviadaConFecha_cuandoSeMarcaEnviada() {
        // Arrange
        NotificacionDomain notificacion = notificacionValida();

        // Act
        notificacion.marcarEnviada();

        // Assert
        assertThat(notificacion.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(notificacion.getFechaEnvio()).isNotNull();
        assertThat(notificacion.getDetalleError()).isNull();
    }

    @Test
    void debeConservarElMotivo_cuandoSeMarcaFallida() {
        // Arrange
        NotificacionDomain notificacion = notificacionValida();

        // Act
        notificacion.marcarFallida("servidor SMTP no disponible");

        // Assert
        assertThat(notificacion.getEstado()).isEqualTo(EstadoNotificacion.FALLIDA);
        assertThat(notificacion.getDetalleError()).isEqualTo("servidor SMTP no disponible");
        assertThat(notificacion.getFechaEnvio()).isNotNull();
    }

    @Test
    void debeRechazarUnaSegundaTransicion_cuandoYaEstaEnEstadoTerminal() {
        // Arrange
        NotificacionDomain notificacion = notificacionValida();
        notificacion.marcarEnviada();

        // Act
        Throwable excepcion = catchThrowable(() -> notificacion.marcarFallida("otro motivo"));

        // Assert — una notificación ya resuelta no vuelve a cambiar de estado
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        assertThat(((DomainValidationException) excepcion).getValidationResult().getErrores())
                .anySatisfy(error -> assertThat(error.codigoError())
                        .isEqualTo(NotificacionesCodes.Notificacion.TRANSICION_INVALIDA));
    }

    @Test
    void debeReconstruirElEstadoPersistido_cuandoSeLeeDeLaBase() {
        // Arrange
        UUID id = UUID.randomUUID();
        Instant creacion = Instant.now().minusSeconds(120);
        Instant envio = Instant.now();

        // Act
        NotificacionDomain notificacion = NotificacionDomain.reconstruir(
                new NotificacionDomain.DatosNotificacion(
                        id, ID_EVENTO, TipoNotificacion.ASESOR_FICHA_CAMBIADO,
                        DESTINATARIO, ASUNTO, NOMBRE, CUERPO, creacion, envio, 0, null),
                EstadoNotificacion.ENVIADA,
                null);

        // Assert
        assertThat(notificacion.getId()).isEqualTo(id);
        assertThat(notificacion.getIdEvento()).isEqualTo(ID_EVENTO);
        assertThat(notificacion.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(notificacion.getFechaCreacion()).isEqualTo(creacion);
        assertThat(notificacion.getFechaEnvio()).isEqualTo(envio);
    }
}
