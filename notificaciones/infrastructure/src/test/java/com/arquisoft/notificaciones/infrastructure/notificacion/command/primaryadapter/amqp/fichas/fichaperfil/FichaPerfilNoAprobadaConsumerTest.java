package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.fichaperfil;

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
class FichaPerfilNoAprobadaConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private FichaPerfilNoAprobadaConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new FichaPerfilNoAprobadaConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt-1", "ana.gomez@soyuco.edu.co"));
    }

    private static MessageProperties propiedades(long deliveryTag) {
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return props;
    }

    @Test
    void debeNotificarACadaEstudianteYAlAsesorConSuTipo_cuandoLlegaLaFichaNoAprobada() throws Exception {
        // Arrange
        var payloadJson = """
                {
                    "idEvento": "evt-1",
                    "fichaPerfilId": "11111111-1111-1111-1111-111111111111",
                    "tituloProyecto": "Sistema de gestión",
                    "asesor": {"nombre": "Carlos Ruiz", "email": "carlos.ruiz@soyuco.edu.co"},
                    "estudiantes": [
                        {"nombre": "Ana Gomez", "email": "ana.gomez@soyuco.edu.co"},
                        {"nombre": "Luis Diaz", "email": "luis.diaz@soyuco.edu.co"}
                    ]
                }
                """;
        var mensaje = MessageBuilder.withBody(payloadJson.getBytes()).andProperties(propiedades(1L)).build();

        // Act
        adapter.onFichaPerfilNoAprobada(mensaje, channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, times(3)).ejecutar(captor.capture());
        var comandos = captor.getAllValues();
        assertThat(comandos).extracting(EnviarNotificacionCommand::destinatarioEmail).containsExactly(
                "ana.gomez@soyuco.edu.co", "luis.diaz@soyuco.edu.co", "carlos.ruiz@soyuco.edu.co");
        assertThat(comandos).extracting(EnviarNotificacionCommand::tipo).containsExactly(
                TipoNotificacion.FICHA_PERFIL_NO_APROBADA_ESTUDIANTE,
                TipoNotificacion.FICHA_PERFIL_NO_APROBADA_ESTUDIANTE,
                TipoNotificacion.FICHA_PERFIL_NO_APROBADA_ASESOR);
        assertThat(comandos).extracting(EnviarNotificacionCommand::idEvento).containsOnly("evt-1");
        assertThat(comandos.get(1).cuerpo()).contains("Luis Diaz", "Sistema de gestión");
        assertThat(comandos.getLast().cuerpo()).contains("Carlos Ruiz", "Sistema de gestión");
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarALaDlqSinNotificar_cuandoElMensajeNoEsJsonValido() throws Exception {
        // Arrange
        var envenenado = MessageBuilder.withBody("no-es-json".getBytes()).andProperties(propiedades(9L)).build();

        // Act
        adapter.onFichaPerfilNoAprobada(envenenado, channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicNack(9L, false, false);
    }
}
