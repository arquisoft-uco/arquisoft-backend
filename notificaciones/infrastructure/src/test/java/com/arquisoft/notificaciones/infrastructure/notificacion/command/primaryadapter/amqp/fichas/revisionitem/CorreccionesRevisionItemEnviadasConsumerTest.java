package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.revisionitem;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
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
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CorreccionesRevisionItemEnviadasConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private CorreccionesRevisionItemEnviadasConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new CorreccionesRevisionItemEnviadasConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt", "luis.perez@uco.edu.co"));
    }

    private Message mensajeCon(String idEvento, long deliveryTag) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "revisionItemId": "%s",
                    "fichaPerfilId": "%s",
                    "tituloProyecto": "Sistema de gestión",
                    "asesor": {"nombre": "Luis Perez", "email": "luis.perez@uco.edu.co"}
                }
                """.formatted(idEvento, UUID.randomUUID(), UUID.randomUUID());
        return mensaje(payloadJson, deliveryTag);
    }

    private Message mensaje(String cuerpo, long deliveryTag) {
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setHeader("X-Trace-Id", "trace-123");
        props.setHeader("X-User-Id", "user-456");
        return MessageBuilder.withBody(cuerpo.getBytes()).andProperties(props).build();
    }

    @Test
    void debeNotificarAlAsesorConAsuntoCuerpoYPie_cuandoElPayloadEsValido() throws Exception {
        // Arrange
        var idEvento = UUID.randomUUID().toString();

        // Act
        consumer.onCorreccionesRevisionItemEnviadas(mensajeCon(idEvento, 1L), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor).ejecutar(captor.capture());
        var command = captor.getValue();
        assertThat(command.idEvento()).isEqualTo(idEvento);
        assertThat(command.tipo()).isEqualTo(TipoNotificacion.REVISION_ITEM_CORRECCIONES_ENVIADAS);
        assertThat(command.destinatarioNombre()).isEqualTo("Luis Perez");
        assertThat(command.destinatarioEmail()).isEqualTo("luis.perez@uco.edu.co");
        assertThat(command.asunto()).contains("Sistema de gestión");
        assertThat(command.cuerpo()).contains("Luis Perez").contains("Sistema de gestión");
        assertThat(command.pie()).isNotEmpty();
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarNackSinReencolarYNoNotificar_cuandoElPayloadNoSePuedeDeserializar() throws Exception {
        // Act
        consumer.onCorreccionesRevisionItemEnviadas(mensaje("no es json", 2L), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicNack(2L, false, false);
    }
}
