package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.usuarios.usuario;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.arquisoft.shared.util.UtilUUID;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UsuarioEstadoCambiadoConsumerTest {

    private static final String USUARIO = "11111111-1111-1111-1111-111111111111";

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private UsuarioEstadoCambiadoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioEstadoCambiadoConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt", "ana.perez@uco.edu.co"));
    }

    private Message mensajeCon(String idEvento, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "2026-09-26T08:00:00Z",
                    "usuario": "%s",
                    "nombre": "Ana Perez",
                    "email": "ana.perez@uco.edu.co",
                    "estado": "INACTIVO",
                    "estadoNombre": "Inactivo"
                }
                """.formatted(idEvento, USUARIO);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        props.setHeader("X-Trace-Id", "trace-123");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeNotificarAlUsuarioConElEstadoNuevoYConfirmar_cuandoElPayloadEsValido() throws Exception {
        // Arrange
        var idEvento = UtilUUID.generarNuevoUUID().toString();

        // Act
        adapter.onUsuarioEstadoCambiado(mensajeCon(idEvento, 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor).ejecutar(captor.capture());
        var command = captor.getValue();
        assertThat(command.idEvento()).isEqualTo(idEvento);
        assertThat(command.tipo()).isEqualTo(TipoNotificacion.USUARIO_ESTADO_CAMBIADO);
        assertThat(command.destinatarioNombre()).isEqualTo("Ana Perez");
        assertThat(command.destinatarioEmail()).isEqualTo("ana.perez@uco.edu.co");
        assertThat(command.asunto()).contains("Inactivo");
        assertThat(command.cuerpo()).contains("Ana Perez").contains("Inactivo");
        assertThat(command.pie()).isNotEmpty();
        verify(logger).info(any(ConsumidorKey.class), eq(USUARIO), eq("INACTIVO"), eq("a***@uco.edu.co"));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarNackConReintento_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        adapter.onUsuarioEstadoCambiado(mensajeCon(UtilUUID.generarNuevoUUID().toString(), 2L, false), channel);

        // Assert
        verify(channel).basicNack(2L, false, true);
    }

    @Test
    void debeEnviarNackSinReintento_cuandoElFalloEsTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        adapter.onUsuarioEstadoCambiado(mensajeCon(UtilUUID.generarNuevoUUID().toString(), 3L, true), channel);

        // Assert
        verify(channel).basicNack(3L, false, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElMensajeEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("payload invalido")).when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        adapter.onUsuarioEstadoCambiado(mensajeCon(UtilUUID.generarNuevoUUID().toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, false);
    }
}
