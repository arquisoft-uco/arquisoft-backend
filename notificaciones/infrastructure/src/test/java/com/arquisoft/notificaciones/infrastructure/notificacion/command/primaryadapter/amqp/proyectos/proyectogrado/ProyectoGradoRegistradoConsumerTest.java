package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoRegistradoConsumerTest {

    private static final String PROYECTO = "44444444-4444-4444-4444-444444444444";
    private static final String FICHA = "11111111-1111-1111-1111-111111111111";

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private ProyectoGradoRegistradoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProyectoGradoRegistradoConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt-1", "laura.mesa@uco.edu.co"));
    }

    private static MessageProperties propiedades(long deliveryTag) {
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return props;
    }

    @Test
    void debeNotificarSoloAlCoordinadorYLoguearSuCorreoEnmascarado_cuandoSeRegistraElProyecto() throws Exception {
        // Arrange
        var payloadJson = """
                {
                    "idEvento": "evt-1",
                    "proyectoGradoId": "%s",
                    "fichaPerfilId": "%s",
                    "tituloProyecto": "Sistema de gestión",
                    "coordinador": {"nombre": "Laura Mesa", "email": "laura.mesa@uco.edu.co"}
                }
                """.formatted(PROYECTO, FICHA);
        var mensaje = MessageBuilder.withBody(payloadJson.getBytes()).andProperties(propiedades(1L)).build();

        // Act
        adapter.onProyectoGradoRegistrado(mensaje, channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor).ejecutar(captor.capture());
        var comando = captor.getValue();
        assertThat(comando.tipo()).isEqualTo(TipoNotificacion.PROYECTO_GRADO_REGISTRADO_COORDINADOR);
        assertThat(comando.destinatarioEmail()).isEqualTo("laura.mesa@uco.edu.co");
        assertThat(comando.asunto()).contains("Sistema de gestión");
        assertThat(comando.cuerpo()).contains("Laura Mesa", "Sistema de gestión");
        verify(logger).info(any(ClaveMensaje.class), eq(PROYECTO), eq(FICHA), eq("l***@uco.edu.co"));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarALaDlqSinNotificar_cuandoElMensajeNoEsJsonValido() throws Exception {
        // Arrange
        var envenenado = MessageBuilder.withBody("no-es-json".getBytes()).andProperties(propiedades(9L)).build();

        // Act
        adapter.onProyectoGradoRegistrado(envenenado, channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicNack(9L, false, false);
    }
}
