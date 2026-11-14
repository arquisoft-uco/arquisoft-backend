package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.proyectogrado;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoEstadoCambiadoConsumerTest {

    private static final String PROYECTO = "44444444-4444-4444-4444-444444444444";
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

    private ProyectoGradoEstadoCambiadoConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new ProyectoGradoEstadoCambiadoConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt-1", "ana.ruiz@soyuco.edu.co"));
    }

    private Message mensaje(long deliveryTag, String estadoNuevo, String estudiantes, String asesores) {
        var payloadJson = """
                {
                    "idEvento": "evt-1",
                    "ocurridoEn": "2026-11-08T10:00:00Z",
                    "proyectoGradoId": "%s",
                    "tituloProyecto": "Sistema de gestión",
                    "estadoAnterior": "EN_PROCESO",
                    "estadoNuevo": "%s",
                    "motivo": "Falta de avance",
                    "estudiantes": %s,
                    "asesores": %s
                }
                """.formatted(PROYECTO, estadoNuevo, estudiantes, asesores);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeNotificarSoloEstudiantes_cuandoFinalizado() throws Exception {
        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(1L, "FINALIZADO", DOS_ESTUDIANTES, UN_ASESOR), channel);

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
    void debeNotificarEstudiantesYAsesores_cuandoCancelado() throws Exception {
        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(2L, "CANCELADO", DOS_ESTUDIANTES, UN_ASESOR), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, times(3)).ejecutar(captor.capture());
        var comandos = captor.getAllValues();
        assertThat(comandos).filteredOn(comando -> comando.tipo() == TipoNotificacion.PROYECTO_GRADO_CANCELADO_ESTUDIANTE)
                .extracting(EnviarNotificacionCommand::destinatarioEmail)
                .containsExactly("ana.ruiz@soyuco.edu.co", "luis.diaz@soyuco.edu.co");
        assertThat(comandos).filteredOn(comando -> comando.tipo() == TipoNotificacion.PROYECTO_GRADO_CANCELADO_ASESOR)
                .singleElement().satisfies(comando -> {
                    assertThat(comando.destinatarioEmail()).isEqualTo("mario.pena@uco.edu.co");
                    assertThat(comando.destinatarioNombre()).isEqualTo("Mario Peña");
                    assertThat(comando.cuerpo()).contains("Sistema de gestión", "Falta de avance");
                });
        assertThat(comandos).allSatisfy(comando -> assertThat(comando.idEvento()).isEqualTo("evt-1"));
        verify(channel).basicAck(2L, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ATRASADO", "EN_PROCESO"})
    void debeConfirmarSinEnviar_cuandoElEstadoNoAvisa(String estado) throws Exception {
        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(3L, estado, DOS_ESTUDIANTES, UN_ASESOR), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(logger).info(ConsumidorKey.LOG_PROYECTO_GRADO_ESTADO_CAMBIADO_IGNORADO, PROYECTO, estado);
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeConfirmarSinEnviar_cuandoNoHayDestinatarios() throws Exception {
        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(4L, "CANCELADO", "[]", "[]"), channel);
        consumer.onProyectoGradoEstadoCambiado(mensaje(5L, "FINALIZADO", "null", "null"), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(logger).info(eq(ConsumidorKey.LOG_PROYECTO_GRADO_ESTADO_CAMBIADO_RECIBIDO),
                eq(PROYECTO), eq("EN_PROCESO"), eq("CANCELADO"), eq(0), eq(0));
        verify(channel).basicAck(4L, false);
        verify(channel).basicAck(5L, false);
    }
}
