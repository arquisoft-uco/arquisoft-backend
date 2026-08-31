package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas;

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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FichaPerfilRegistradaConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private FichaPerfilRegistradaConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new FichaPerfilRegistradaConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt", "carlos.ruiz@soyuco.edu.co"));
    }

    private Message mensajeCon(String idEvento, long deliveryTag) {
        String payloadJson = """
                {
                    "idEvento": "%s",
                    "fichaPerfilId": "%s",
                    "tituloProyecto": "Sistema de gestión",
                    "asesorNombre": "Carlos Ruiz",
                    "asesorEmail": "carlos.ruiz@soyuco.edu.co"
                }
                """.formatted(idEvento, UUID.randomUUID());

        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setHeader("X-Trace-Id", "trace-123");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeNotificarSoloAlAsesor_cuandoLaFichaQuedaRegistrada() throws Exception {
        // Arrange
        String idEvento = UUID.randomUUID().toString();

        // Act
        adapter.onFichaPerfilRegistrada(mensajeCon(idEvento, 1L), channel);

        // Assert
        ArgumentCaptor<EnviarNotificacionCommand> captor =
                ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor).ejecutar(captor.capture());

        EnviarNotificacionCommand command = captor.getValue();
        assertThat(command.idEvento()).isEqualTo(idEvento);
        assertThat(command.tipo()).isEqualTo(TipoNotificacion.FICHA_PERFIL_REGISTRADA_ASESOR);
        assertThat(command.destinatarioNombre()).isEqualTo("Carlos Ruiz");
        assertThat(command.destinatarioEmail()).isEqualTo("carlos.ruiz@soyuco.edu.co");
        assertThat(command.cuerpo()).contains("Carlos Ruiz", "Sistema de gestión");
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarElMensaje_cuandoLaNotificacionSeRegistra() throws Exception {
        // Act
        adapter.onFichaPerfilRegistrada(mensajeCon(UUID.randomUUID().toString(), 7L), channel);

        // Assert
        verify(channel).basicAck(7L, false);
    }
}
