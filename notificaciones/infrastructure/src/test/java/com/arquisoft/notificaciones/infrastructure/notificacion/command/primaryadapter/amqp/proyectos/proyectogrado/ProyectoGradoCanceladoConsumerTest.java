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
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoCanceladoConsumerTest {

    private static final String DOS_ESTUDIANTES = """
            [{"id": "11111111-1111-1111-1111-111111111111", "nombre": "Ana Ruiz", "email": "ana.ruiz@soyuco.edu.co"},
             {"id": "22222222-2222-2222-2222-222222222222", "nombre": "Luis Diaz", "email": "luis.diaz@soyuco.edu.co"}]
            """;
    private static final String UN_ASESOR = """
            [{"nombre": "Mario Peña", "email": "mario.pena@uco.edu.co"}]
            """;

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private ProyectoGradoCanceladoConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new ProyectoGradoCanceladoConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt-1", "ana.ruiz@soyuco.edu.co"));
    }

    private Message mensaje(long deliveryTag, String estudiantes, String asesores) {
        var payloadJson = """
                {
                    "idEvento": "evt-1",
                    "proyectoGradoId": "44444444-4444-4444-4444-444444444444",
                    "tituloProyecto": "Sistema de gestión",
                    "motivo": "Falta de avance",
                    "estudiantes": %s,
                    "asesores": %s
                }
                """.formatted(estudiantes, asesores);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeNotificarACadaEstudiante_cuandoHayVarios() throws Exception {
        // Act
        consumer.onProyectoGradoCancelado(mensaje(1L, DOS_ESTUDIANTES, "[]"), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, times(2)).ejecutar(captor.capture());
        var comandos = captor.getAllValues();
        assertThat(comandos).extracting(EnviarNotificacionCommand::destinatarioEmail)
                .containsExactly("ana.ruiz@soyuco.edu.co", "luis.diaz@soyuco.edu.co");
        assertThat(comandos).allSatisfy(comando -> {
            assertThat(comando.idEvento()).isEqualTo("evt-1");
            assertThat(comando.tipo()).isEqualTo(TipoNotificacion.PROYECTO_GRADO_CANCELADO_ESTUDIANTE);
            assertThat(comando.asunto()).contains("Sistema de gestión");
            assertThat(comando.cuerpo()).contains(comando.destinatarioNombre(), "Sistema de gestión", "Falta de avance");
            assertThat(comando.pie()).isNotEmpty();
        });
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeNotificarAlAsesor_cuandoHayAsesorAsignado() throws Exception {
        // Act
        consumer.onProyectoGradoCancelado(mensaje(2L, DOS_ESTUDIANTES, UN_ASESOR), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, times(3)).ejecutar(captor.capture());
        var delAsesor = captor.getAllValues().stream()
                .filter(comando -> comando.tipo() == TipoNotificacion.PROYECTO_GRADO_CANCELADO_ASESOR)
                .toList();
        assertThat(delAsesor).singleElement().satisfies(comando -> {
            assertThat(comando.destinatarioEmail()).isEqualTo("mario.pena@uco.edu.co");
            assertThat(comando.destinatarioNombre()).isEqualTo("Mario Peña");
            assertThat(comando.cuerpo()).contains("Sistema de gestión", "Falta de avance");
        });
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeNotificarSoloEstudiantes_cuandoSinAsesor() throws Exception {
        // Act
        consumer.onProyectoGradoCancelado(mensaje(3L, DOS_ESTUDIANTES, "[]"), channel);
        consumer.onProyectoGradoCancelado(mensaje(4L, DOS_ESTUDIANTES, "null"), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, times(4)).ejecutar(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(EnviarNotificacionCommand::tipo)
                .containsOnly(TipoNotificacion.PROYECTO_GRADO_CANCELADO_ESTUDIANTE);
        verify(channel).basicAck(3L, false);
        verify(channel).basicAck(4L, false);
    }

    @Test
    void debeNoEnviarNada_cuandoNoHayDestinatarios() throws Exception {
        // Act
        consumer.onProyectoGradoCancelado(mensaje(5L, "[]", "[]"), channel);
        consumer.onProyectoGradoCancelado(mensaje(6L, "null", "null"), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicAck(5L, false);
        verify(channel).basicAck(6L, false);
    }
}
