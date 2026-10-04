package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

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
class EstudiantesProyectoGradoAsignadosConsumerTest {

    private static final String TRES_ESTUDIANTES = """
            [
                {"nombre": "Ana Gomez", "email": "ana.gomez@soyuco.edu.co"},
                {"nombre": "Luis Diaz", "email": "luis.diaz@soyuco.edu.co"},
                {"nombre": "Eva Ruiz", "email": "eva.ruiz@soyuco.edu.co"}
            ]""";

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EstudiantesProyectoGradoAsignadosConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudiantesProyectoGradoAsignadosConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt-1", "ana.gomez@soyuco.edu.co"));
    }

    private Message mensaje(String estudiantesJson, long deliveryTag) {
        var payloadJson = """
                {
                    "idEvento": "evt-1",
                    "proyectoGradoId": "44444444-4444-4444-4444-444444444444",
                    "tituloProyecto": "Sistema de gestión",
                    "estudiantes": %s
                }
                """.formatted(estudiantesJson);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeNotificarACadaEstudianteConSuTipoYNombre_cuandoSeAsignanAlProyecto() throws Exception {
        // Act
        adapter.onEstudiantesProyectoGradoAsignados(mensaje(TRES_ESTUDIANTES, 1L), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, times(3)).ejecutar(captor.capture());
        var comandos = captor.getAllValues();
        assertThat(comandos).extracting(EnviarNotificacionCommand::destinatarioEmail).containsExactly(
                "ana.gomez@soyuco.edu.co", "luis.diaz@soyuco.edu.co", "eva.ruiz@soyuco.edu.co");
        assertThat(comandos).extracting(EnviarNotificacionCommand::tipo)
                .containsOnly(TipoNotificacion.ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS);
        assertThat(comandos).extracting(EnviarNotificacionCommand::idEvento).containsOnly("evt-1");
        assertThat(comandos.get(2).cuerpo()).contains("Eva Ruiz", "Sistema de gestión");
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeNoEnviarNadaYConfirmar_cuandoElEventoLlegaSinEstudiantes() throws Exception {
        // Act
        adapter.onEstudiantesProyectoGradoAsignados(mensaje("null", 2L), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicAck(2L, false);
    }
}
