package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.evaluaciones.evaluacion;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EvaluacionFinalizadaConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EvaluacionFinalizadaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new EvaluacionFinalizadaConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt-1", "x@uco.edu.co"));
    }

    private Message mensaje(String estudiantesJson, long deliveryTag) {
        var json = """
                {
                    "idEvento": "evt-1",
                    "evaluacionId": "44444444-4444-4444-4444-444444444444",
                    "entregableId": "55555555-5555-5555-5555-555555555555",
                    "nombreProyecto": "Sistema de gestión",
                    "versionEntregable": 2,
                    "resultado": "APROBADA_CON_OBSERVACIONES",
                    "resultadoNombre": "Aprobada con observaciones",
                    "notaFinal": 3.5,
                    "coordinador": {"nombre": "Carlos Coord", "email": "carlos@uco.edu.co"},
                    "asesor": {"nombre": "Alba Asesora", "email": "alba@uco.edu.co"},
                    "estudiantes": %s
                }
                """.formatted(estudiantesJson);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeNotificarUnaVezAEstudiantesAsesorYCoordinador_cuandoSeFinalizaLaEvaluacion() throws Exception {
        // Arrange
        var estudiantes = """
                [{"nombre": "Ana Gomez", "email": "ana@uco.edu.co"},
                 {"nombre": "Luis Diaz", "email": "luis@uco.edu.co"}]""";

        // Act
        consumer.onEvaluacionFinalizada(mensaje(estudiantes, 1L), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, times(4)).ejecutar(captor.capture());
        var comandos = captor.getAllValues();
        assertThat(comandos).extracting(EnviarNotificacionCommand::destinatarioEmail)
                .containsExactlyInAnyOrder("carlos@uco.edu.co", "alba@uco.edu.co", "ana@uco.edu.co", "luis@uco.edu.co");
        assertThat(comandos).extracting(EnviarNotificacionCommand::tipo)
                .containsOnly(TipoNotificacion.EVALUACION_FINALIZADA);
        assertThat(comandos).extracting(EnviarNotificacionCommand::idEvento).containsOnly("evt-1");
        assertThat(comandos).allSatisfy(comando -> assertThat(comando.asunto()).contains("Sistema de gestión"));
        assertThat(comandos).filteredOn(comando -> comando.destinatarioEmail().equals("ana@uco.edu.co"))
                .singleElement()
                .satisfies(comando -> assertThat(comando.cuerpo())
                        .contains("Ana Gomez", "Sistema de gestión", "2", "Aprobada con observaciones"));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeNotificarSoloAAsesorYCoordinadorYConfirmar_cuandoElEventoLlegaSinEstudiantes() throws Exception {
        // Act
        consumer.onEvaluacionFinalizada(mensaje("null", 2L), channel);

        // Assert
        verify(enviarNotificacionInteractor, times(2)).ejecutar(any());
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeConfirmarSinReenviar_cuandoElHelperRegistraDuplicadaOFallida() throws Exception {
        // Arrange
        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Duplicada("evt-1", "x@uco.edu.co"))
                .thenReturn(new EnvioNotificacionResult.Fallida("evt-1", "x@uco.edu.co", "smtp caído"));

        // Act
        consumer.onEvaluacionFinalizada(mensaje("[]", 3L), channel);

        // Assert
        verify(channel).basicAck(3L, false);
        verify(channel, never()).basicNack(3L, false, false);
    }
}
