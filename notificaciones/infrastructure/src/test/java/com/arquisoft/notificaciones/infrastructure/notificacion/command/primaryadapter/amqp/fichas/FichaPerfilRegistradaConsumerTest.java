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

import java.util.List;
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
                .thenReturn(new EnvioNotificacionResult.Enviada("evt", "ana.gomez@soyuco.edu.co"));
    }

    private Message mensajeCon(String idEvento, String estudiantesJson, long deliveryTag) {
        String payloadJson = """
                {
                    "idEvento": "%s",
                    "fichaPerfilId": "%s",
                    "tituloProyecto": "Sistema de gestión",
                    "asesorNombre": "Carlos Ruiz",
                    "asesorEmail": "carlos.ruiz@soyuco.edu.co",
                    "estudiantes": %s
                }
                """.formatted(idEvento, UUID.randomUUID(), estudiantesJson);

        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setHeader("X-Trace-Id", "trace-123");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    private static final String DOS_ESTUDIANTES = """
            [
                {"nombre": "Ana Gomez", "email": "ana.gomez@soyuco.edu.co"},
                {"nombre": "Luis Diaz", "email": "luis.diaz@soyuco.edu.co"}
            ]""";

    private List<EnviarNotificacionCommand> comandosEmitidos(int esperados) {
        ArgumentCaptor<EnviarNotificacionCommand> captor =
                ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, org.mockito.Mockito.times(esperados))
                .ejecutar(captor.capture());
        return captor.getAllValues();
    }

    @Test
    void debeNotificarAlAsesorYACadaEstudiante_cuandoElPayloadTraeVarios() throws Exception {
        // Arrange
        String idEvento = UUID.randomUUID().toString();

        // Act
        adapter.onFichaPerfilRegistrada(mensajeCon(idEvento, DOS_ESTUDIANTES, 1L), channel);

        // Assert
        assertThat(comandosEmitidos(3))
                .extracting(EnviarNotificacionCommand::destinatarioEmail)
                .containsExactly(
                        "carlos.ruiz@soyuco.edu.co",
                        "ana.gomez@soyuco.edu.co",
                        "luis.diaz@soyuco.edu.co");
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeCompartirElIdDelEvento_cuandoUnMismoEventoProduceVariosCorreos() throws Exception {
        // Arrange
        String idEvento = UUID.randomUUID().toString();

        // Act
        adapter.onFichaPerfilRegistrada(mensajeCon(idEvento, DOS_ESTUDIANTES, 1L), channel);

        // Assert
        assertThat(comandosEmitidos(3))
                .extracting(EnviarNotificacionCommand::idEvento)
                .containsOnly(idEvento);
    }

    @Test
    void debeDistinguirElTipoPorDestinatario_cuandoSeAbreElAbanico() throws Exception {
        // Arrange
        String idEvento = UUID.randomUUID().toString();

        // Act
        adapter.onFichaPerfilRegistrada(mensajeCon(idEvento, DOS_ESTUDIANTES, 1L), channel);

        // Assert
        assertThat(comandosEmitidos(3))
                .extracting(EnviarNotificacionCommand::tipo)
                .containsExactly(
                        TipoNotificacion.FICHA_PERFIL_REGISTRADA_ASESOR,
                        TipoNotificacion.FICHA_PERFIL_REGISTRADA_ESTUDIANTE,
                        TipoNotificacion.FICHA_PERFIL_REGISTRADA_ESTUDIANTE);
    }

    @Test
    void debePersonalizarElCuerpoConElNombreDeCadaDestinatario_cuandoSeAbreElAbanico()
            throws Exception {
        // Arrange
        String idEvento = UUID.randomUUID().toString();

        // Act
        adapter.onFichaPerfilRegistrada(mensajeCon(idEvento, DOS_ESTUDIANTES, 1L), channel);

        // Assert
        List<EnviarNotificacionCommand> comandos = comandosEmitidos(3);
        assertThat(comandos.get(0).cuerpo()).contains("Carlos Ruiz", "Sistema de gestión");
        assertThat(comandos.get(1).cuerpo()).contains("Ana Gomez", "Sistema de gestión");
        assertThat(comandos.get(2).cuerpo()).contains("Luis Diaz", "Sistema de gestión");
    }

    @Test
    void debeNotificarSoloAlAsesor_cuandoElEventoNoTraeEstudiantes() throws Exception {
        // Arrange
        String idEvento = UUID.randomUUID().toString();

        // Act
        adapter.onFichaPerfilRegistrada(mensajeCon(idEvento, "[]", 2L), channel);

        // Assert
        assertThat(comandosEmitidos(1))
                .extracting(EnviarNotificacionCommand::destinatarioEmail)
                .containsExactly("carlos.ruiz@soyuco.edu.co");
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeNotificarSoloAlAsesor_cuandoElCampoDeEstudiantesLlegaNulo() throws Exception {
        // Arrange
        String idEvento = UUID.randomUUID().toString();

        // Act
        adapter.onFichaPerfilRegistrada(mensajeCon(idEvento, "null", 3L), channel);

        // Assert
        assertThat(comandosEmitidos(1))
                .extracting(EnviarNotificacionCommand::destinatarioEmail)
                .containsExactly("carlos.ruiz@soyuco.edu.co");
        verify(channel).basicAck(3L, false);
    }
}
