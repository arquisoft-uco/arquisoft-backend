package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.entregables.entregableproyectogrado;

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
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EntregableProyectoGradoDisponibleEvaluacionConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EntregableProyectoGradoDisponibleEvaluacionConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new EntregableProyectoGradoDisponibleEvaluacionConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt-1", "x@uco.edu.co"));
    }

    @Test
    void debeEnviarUnCorreoPorEstudianteConElEstadoDisponibleEvaluacion_cuandoLlegaElEvento() throws Exception {
        // Arrange
        var json = """
                {
                    "idEvento": "evt-1",
                    "entregableProyectoGrado": "44444444-4444-4444-4444-444444444444",
                    "nombreProyecto": "Sistema de riego",
                    "version": 3,
                    "estadoEntregable": "DISPONIBLE_EVALUACION",
                    "estadoEntregableNombre": "Disponible Evaluacion",
                    "estudiantes": [{"nombre": "Ana Gomez", "email": "ana@uco.edu.co"},
                                    {"nombre": "Luis Diaz", "email": "luis@uco.edu.co"}]
                }
                """;
        var props = new MessageProperties();
        props.setDeliveryTag(1L);
        var mensaje = MessageBuilder.withBody(json.getBytes()).andProperties(props).build();

        // Act
        consumer.onEntregableDisponibleEvaluacion(mensaje, channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, times(2)).ejecutar(captor.capture());
        assertThat(captor.getAllValues()).extracting(EnviarNotificacionCommand::destinatarioEmail)
                .containsExactlyInAnyOrder("ana@uco.edu.co", "luis@uco.edu.co");
        assertThat(captor.getAllValues()).extracting(EnviarNotificacionCommand::tipo)
                .containsOnly(TipoNotificacion.ENTREGABLE_ESTADO_CAMBIADO);
        assertThat(captor.getAllValues()).allSatisfy(comando -> {
            assertThat(comando.asunto()).contains("Sistema de riego", "Disponible Evaluacion");
            assertThat(comando.cuerpo()).contains("Sistema de riego", "3", "Disponible Evaluacion");
        });
        verify(logger).info(ConsumidorKey.LOG_ENTREGABLE_DISPONIBLE_EVALUACION_RECIBIDO, "evt-1", "44444444-4444-4444-4444-444444444444", 2);
        verify(channel).basicAck(1L, false);
    }
}
