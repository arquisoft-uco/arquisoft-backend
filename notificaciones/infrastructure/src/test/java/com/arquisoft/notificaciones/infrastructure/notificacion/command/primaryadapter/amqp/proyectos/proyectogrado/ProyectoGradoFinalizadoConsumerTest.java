package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.proyectogrado;

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
import org.springframework.dao.QueryTimeoutException;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoFinalizadoConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private ProyectoGradoFinalizadoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProyectoGradoFinalizadoConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt-1", "ana.ruiz@soyuco.edu.co"));
    }

    private Message mensaje(long deliveryTag, boolean reentregado, String estudiantes) {
        var payloadJson = """
                {
                    "idEvento": "evt-1",
                    "proyectoGradoId": "44444444-4444-4444-4444-444444444444",
                    "tituloProyecto": "Sistema de gestión",
                    "estudiantes": %s
                }
                """.formatted(estudiantes);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    private static final String DOS_ESTUDIANTES = """
            [{"id": "11111111-1111-1111-1111-111111111111", "nombre": "Ana Ruiz", "email": "ana.ruiz@soyuco.edu.co"},
             {"id": "22222222-2222-2222-2222-222222222222", "nombre": "Luis Diaz", "email": "luis.diaz@soyuco.edu.co"}]
            """;

    @Test
    void debeNotificarACadaEstudianteConLasTresPlantillas_cuandoSeFinalizaElProyecto() throws Exception {
        // Act
        adapter.onProyectoGradoFinalizado(mensaje(1L, false, DOS_ESTUDIANTES), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, times(2)).ejecutar(captor.capture());
        var comandos = captor.getAllValues();
        assertThat(comandos).extracting(EnviarNotificacionCommand::destinatarioEmail)
                .containsExactly("ana.ruiz@soyuco.edu.co", "luis.diaz@soyuco.edu.co");
        assertThat(comandos).allSatisfy(comando -> {
            assertThat(comando.idEvento()).isEqualTo("evt-1");
            assertThat(comando.tipo()).isEqualTo(TipoNotificacion.PROYECTO_GRADO_FINALIZADO_ESTUDIANTE);
            assertThat(comando.asunto()).contains("Sistema de gestión");
            assertThat(comando.cuerpo()).contains(comando.destinatarioNombre(), "Sistema de gestión");
            assertThat(comando.pie()).isNotEmpty();
        });
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarSinNotificar_cuandoNoHayEstudiantes() throws Exception {
        // Act
        adapter.onProyectoGradoFinalizado(mensaje(2L, false, "[]"), channel);
        adapter.onProyectoGradoFinalizado(mensaje(3L, false, "null"), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicAck(2L, false);
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeReencolarUnaVezYLuegoEnviarALaDlq_cuandoFallaUnRecursoTransitorio() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        adapter.onProyectoGradoFinalizado(mensaje(4L, false, DOS_ESTUDIANTES), channel);
        adapter.onProyectoGradoFinalizado(mensaje(5L, true, DOS_ESTUDIANTES), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void debeEnviarALaDlqSinReencolar_cuandoElMensajeEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("payload invalido"))
                .when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        adapter.onProyectoGradoFinalizado(mensaje(6L, false, DOS_ESTUDIANTES), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
