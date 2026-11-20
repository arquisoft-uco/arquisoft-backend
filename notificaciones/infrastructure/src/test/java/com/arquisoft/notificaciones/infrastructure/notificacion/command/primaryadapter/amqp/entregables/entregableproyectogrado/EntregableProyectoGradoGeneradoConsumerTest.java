package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.message.key.notificaciones.PlantillaKey;
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
class EntregableProyectoGradoGeneradoConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EntregableProyectoGradoGeneradoConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new EntregableProyectoGradoGeneradoConsumer(
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
                    "entregableProyectoGrado": "44444444-4444-4444-4444-444444444444",
                    "proyectoGrado": "55555555-5555-5555-5555-555555555555",
                    "nombreProyecto": "Sistema de riego",
                    "version": 2,
                    "estadoEntregable": "NUEVO",
                    "estadoEntregableNombre": "Nuevo",
                    "estudiantes": %s
                }
                """.formatted(estudiantesJson);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeEnviarUnCorreoPorEstudianteConElFormatoDelEstado_cuandoSeGeneraElEntregable() throws Exception {
        // Arrange
        var estudiantes = """
                [{"nombre": "Ana Gomez", "email": "ana@uco.edu.co"},
                 {"nombre": "Luis Diaz", "email": "luis@uco.edu.co"}]""";

        // Act
        consumer.onEntregableGenerado(mensaje(estudiantes, 1L), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, times(2)).ejecutar(captor.capture());
        var comandos = captor.getAllValues();
        assertThat(comandos).extracting(EnviarNotificacionCommand::destinatarioEmail)
                .containsExactlyInAnyOrder("ana@uco.edu.co", "luis@uco.edu.co");
        assertThat(comandos).extracting(EnviarNotificacionCommand::tipo)
                .containsOnly(TipoNotificacion.ENTREGABLE_ESTADO_CAMBIADO);
        assertThat(comandos).extracting(EnviarNotificacionCommand::idEvento).containsOnly("evt-1");
        assertThat(comandos).allSatisfy(comando -> {
            assertThat(comando.asunto()).isEqualTo(
                    Mensajes.formatear(PlantillaKey.ASUNTO_ENTREGABLE_ESTADO_CAMBIADO, "Sistema de riego", "Nuevo"));
            assertThat(comando.pie()).isEqualTo(Mensajes.formatear(PlantillaKey.PIE_GENERICO));
        });
        assertThat(comandos).filteredOn(comando -> comando.destinatarioEmail().equals("ana@uco.edu.co"))
                .singleElement()
                .satisfies(comando -> {
                    assertThat(comando.destinatarioNombre()).isEqualTo("Ana Gomez");
                    assertThat(comando.cuerpo()).isEqualTo(Mensajes.formatear(
                            PlantillaKey.CUERPO_ENTREGABLE_ESTADO_CAMBIADO, "Ana Gomez", 2, "Sistema de riego", "Nuevo"));
                    assertThat(comando.cuerpo()).contains("Ana Gomez", "Sistema de riego", "2", "Nuevo");
                });
        verify(logger).info(ConsumidorKey.LOG_ENTREGABLE_GENERADO_RECIBIDO,
                "evt-1", "44444444-4444-4444-4444-444444444444", 2);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarSinEnviarNada_cuandoLaListaDeEstudiantesEsVaciaONula() throws Exception {
        // Act
        consumer.onEntregableGenerado(mensaje("[]", 2L), channel);
        consumer.onEntregableGenerado(mensaje("null", 3L), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicAck(2L, false);
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeConfirmarSinReenviar_cuandoElHelperRegistraDuplicadaOFallida() throws Exception {
        // Arrange
        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Duplicada("evt-1", "x@uco.edu.co"))
                .thenReturn(new EnvioNotificacionResult.Fallida("evt-1", "x@uco.edu.co", "smtp caido"));
        var estudiantes = """
                [{"nombre": "Ana Gomez", "email": "ana@uco.edu.co"},
                 {"nombre": "Luis Diaz", "email": "luis@uco.edu.co"}]""";

        // Act
        consumer.onEntregableGenerado(mensaje(estudiantes, 4L), channel);

        // Assert
        verify(enviarNotificacionInteractor, times(2)).ejecutar(any());
        verify(channel).basicAck(4L, false);
        verify(channel, never()).basicNack(4L, false, false);
    }
}
