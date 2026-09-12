package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.evaluaciones.evaluacioncualitativajurado;

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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EvaluacionesCualitativasJuradoRegistradasConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EvaluacionesCualitativasJuradoRegistradasConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionesCualitativasJuradoRegistradasConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt", "estudiante@uco.edu.co"));
    }

    private Message mensajeCon(String idEvento, long deliveryTag, String estudiantesJson) {
        String payloadJson = """
                {
                    "idEvento": "%s",
                    "evaluacionJuradoId": "%s",
                    "evaluacionId": "%s",
                    "entregableId": "%s",
                    "juradoId": "%s",
                    "proyecto": "Sistema de gestión académica",
                    "versionEntregable": 2,
                    "cantidad": 3,
                    "estudiantes": %s
                }
                """.formatted(idEvento, UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), estudiantesJson);

        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setHeader("X-Trace-Id", "trace-123");
        props.setHeader("X-User-Id", "user-456");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeNotificarACadaEstudiante_cuandoElPayloadTieneVariosContactos() throws Exception {
        // Arrange
        String idEvento = UUID.randomUUID().toString();
        String estudiantes = """
                [
                  {"estudiante": "%s", "email": "ana@uco.edu.co"},
                  {"estudiante": "%s", "email": "luis@uco.edu.co"}
                ]
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        // Act
        adapter.onEvaluacionesCualitativasJuradoRegistradas(mensajeCon(idEvento, 1L, estudiantes), channel);

        // Assert
        ArgumentCaptor<EnviarNotificacionCommand> captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, org.mockito.Mockito.times(2)).ejecutar(captor.capture());

        List<EnviarNotificacionCommand> comandos = captor.getAllValues();
        assertThat(comandos).extracting(EnviarNotificacionCommand::destinatarioEmail)
                .containsExactlyInAnyOrder("ana@uco.edu.co", "luis@uco.edu.co");
        assertThat(comandos).allSatisfy(comando -> {
            assertThat(comando.idEvento()).isEqualTo(idEvento);
            assertThat(comando.tipo()).isEqualTo(TipoNotificacion.EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS);
            assertThat(comando.destinatarioNombre()).isEqualTo(comando.destinatarioEmail());
            assertThat(comando.asunto()).contains("Sistema de gestión académica");
            assertThat(comando.cuerpo()).contains("3").contains("Sistema de gestión académica").contains("2");
            assertThat(comando.pie()).isNotEmpty();
        });
    }

    @Test
    void debeConfirmarElMensaje_cuandoElProcesamientoTermina() throws Exception {
        // Arrange
        String estudiantes = """
                [{"estudiante": "%s", "email": "ana@uco.edu.co"}]
                """.formatted(UUID.randomUUID());

        // Act
        adapter.onEvaluacionesCualitativasJuradoRegistradas(
                mensajeCon(UUID.randomUUID().toString(), 1L, estudiantes), channel);

        // Assert
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeAceptarSinNotificar_cuandoElPayloadNoTraeEstudiantes() throws Exception {
        // Act
        adapter.onEvaluacionesCualitativasJuradoRegistradas(
                mensajeCon(UUID.randomUUID().toString(), 2L, "[]"), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElInteractorFalla() throws Exception {
        // Arrange
        doThrow(new RuntimeException("fallo al notificar"))
                .when(enviarNotificacionInteractor).ejecutar(any());
        String estudiantes = """
                [{"estudiante": "%s", "email": "ana@uco.edu.co"}]
                """.formatted(UUID.randomUUID());

        // Act
        adapter.onEvaluacionesCualitativasJuradoRegistradas(
                mensajeCon(UUID.randomUUID().toString(), 3L, estudiantes), channel);

        // Assert
        verify(channel).basicNack(3L, false, false);
    }
}
