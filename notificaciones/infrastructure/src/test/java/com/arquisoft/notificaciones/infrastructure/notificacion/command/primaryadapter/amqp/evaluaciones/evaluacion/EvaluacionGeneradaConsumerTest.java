package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.evaluaciones.evaluacion;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EvaluacionGeneradaConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EvaluacionGeneradaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new EvaluacionGeneradaConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt-1", "carlos@uco.edu.co"));
    }

    private Message mensaje(String coordinadorJson, long deliveryTag) {
        var json = """
                {
                    "idEvento": "evt-1",
                    "evaluacionId": "44444444-4444-4444-4444-444444444444",
                    "entregableId": "55555555-5555-5555-5555-555555555555",
                    "proyectoGradoId": "66666666-6666-6666-6666-666666666666",
                    "nombreProyecto": "Sistema de gestión",
                    "versionEntregable": 2,
                    "coordinador": %s
                }
                """.formatted(coordinadorJson);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeEnviarUnCorreoAlCoordinador_cuandoLlegaElEvento() throws Exception {
        // Arrange
        var coordinador = "{\"nombre\": \"Carlos Coord\", \"email\": \"carlos@uco.edu.co\"}";

        // Act
        consumer.onEvaluacionGenerada(mensaje(coordinador, 1L), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor).ejecutar(captor.capture());
        var comando = captor.getValue();
        assertThat(comando.tipo()).isEqualTo(TipoNotificacion.EVALUACION_GENERADA_COORDINADOR);
        assertThat(comando.idEvento()).isEqualTo("evt-1");
        assertThat(comando.destinatarioEmail()).isEqualTo("carlos@uco.edu.co");
        assertThat(comando.asunto()).contains("Sistema de gestión");
        assertThat(comando.cuerpo()).contains("Carlos Coord", "Sistema de gestión", "2");
        verify(logger).info(eq(ConsumidorKey.LOG_EVALUACION_GENERADA_RECIBIDO), eq("evt-1"),
                eq("44444444-4444-4444-4444-444444444444"), eq("55555555-5555-5555-5555-555555555555"));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRechazarElMensajeSinEnviar_cuandoElCoordinadorViajaVacio() throws Exception {
        // Act
        consumer.onEvaluacionGenerada(mensaje("null", 2L), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicNack(2L, false, false);
        verify(channel, never()).basicAck(2L, false);
    }
}
