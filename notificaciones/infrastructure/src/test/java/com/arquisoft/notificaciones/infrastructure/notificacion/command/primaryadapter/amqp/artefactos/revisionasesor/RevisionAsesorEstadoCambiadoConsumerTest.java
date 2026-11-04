package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.revisionasesor;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.dao.QueryTimeoutException;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RevisionAsesorEstadoCambiadoConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private RevisionAsesorEstadoCambiadoConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new RevisionAsesorEstadoCambiadoConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt", "ana@uco.edu.co"));
    }

    private Message mensajeCon(String idEvento, String revisionAsesorId, long deliveryTag, boolean reentregado) {
        return mensajeCon(idEvento, revisionAsesorId, "RESUELTA", deliveryTag, reentregado);
    }

    private Message mensajeCon(String idEvento, String revisionAsesorId, String estado, long deliveryTag,
                               boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "revisionAsesorId": "%s",
                    "estado": "%s",
                    "asesorNombre": "Ana Gomez",
                    "asesorEmail": "ana@uco.edu.co",
                    "estudianteNombre": "Luis Perez"
                }
                """.formatted(idEvento, revisionAsesorId, estado);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        props.setHeader("X-Trace-Id", "trace-123");
        props.setHeader("X-User-Id", "user-456");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeTraducirElEventoEnUnaNotificacionAlAsesor_cuandoElPayloadEsValido() throws Exception {
        // Arrange
        var idEvento = UtilUUID.generarNuevoUUID().toString();
        var revision = UtilUUID.generarNuevoUUID().toString();

        // Act
        consumer.onRevisionAsesorEstadoCambiado(mensajeCon(idEvento, revision, 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor).ejecutar(captor.capture());
        var command = captor.getValue();
        assertThat(command.idEvento()).isEqualTo(idEvento);
        assertThat(command.tipo()).isEqualTo(TipoNotificacion.REVISION_ASESOR_RESUELTA);
        assertThat(command.destinatarioNombre()).isEqualTo("Ana Gomez");
        assertThat(command.destinatarioEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(command.asunto()).contains("Luis Perez");
        assertThat(command.cuerpo()).contains("Ana Gomez").contains("Luis Perez");
        assertThat(command.pie()).isNotEmpty();
        verify(logger).info(eq(ConsumidorKey.LOG_REVISION_ASESOR_ESTADO_CAMBIADO_RECIBIDO), eq(revision),
                eq("a***@uco.edu.co"));
        verify(channel).basicAck(1L, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDIENTE", "EN_PROGRESO", "CERRADA"})
    void debeIgnorar_cuandoElEstadoNoEsResuelta(String estado) throws Exception {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID().toString();
        var mensaje = mensajeCon(UtilUUID.generarNuevoUUID().toString(), revision, estado, 5L, false);

        // Act
        consumer.onRevisionAsesorEstadoCambiado(mensaje, channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(logger).info(ConsumidorKey.LOG_REVISION_ASESOR_ESTADO_CAMBIADO_IGNORADO, revision, estado);
        verify(channel).basicAck(5L, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElMensajeEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("payload inconsistente"))
                .when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        consumer.onRevisionAsesorEstadoCambiado(
                mensajeCon(UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID().toString(), 2L, false), channel);

        // Assert
        verify(channel).basicNack(2L, false, false);
    }

    @Test
    void debeReencolar_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        consumer.onRevisionAsesorEstadoCambiado(
                mensajeCon(UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID().toString(), 3L, false), channel);

        // Assert
        verify(channel).basicNack(3L, false, true);
    }

    @Test
    void debeDescartar_cuandoElFalloTransitorioYaFueReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        consumer.onRevisionAsesorEstadoCambiado(
                mensajeCon(UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID().toString(), 4L, true), channel);

        // Assert
        verify(channel).basicNack(4L, false, false);
    }
}
