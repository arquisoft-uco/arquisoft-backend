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
import org.mockito.Mockito;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EstudiantesFichaPerfilAsignadosConsumerTest {

    private static final String DOS_ESTUDIANTES = """
            [
                {"nombre": "Ana Gomez", "email": "ana.gomez@soyuco.edu.co"},
                {"nombre": "Luis Diaz", "email": "luis.diaz@soyuco.edu.co"}
            ]""";

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EstudiantesFichaPerfilAsignadosConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudiantesFichaPerfilAsignadosConsumer(
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
                    "estudiantes": %s
                }
                """.formatted(idEvento, UUID.randomUUID(), estudiantesJson);

        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setHeader("X-Trace-Id", "trace-123");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    private List<EnviarNotificacionCommand> comandosEmitidos(int esperados) {
        ArgumentCaptor<EnviarNotificacionCommand> captor =
                ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, Mockito.times(esperados)).ejecutar(captor.capture());
        return captor.getAllValues();
    }

    @Test
    void debeNotificarACadaEstudiante_cuandoElPayloadTraeVarios() throws Exception {
        // Arrange
        String idEvento = UUID.randomUUID().toString();

        // Act
        adapter.onEstudiantesFichaPerfilAsignados(
                mensajeCon(idEvento, DOS_ESTUDIANTES, 1L), channel);

        // Assert
        assertThat(comandosEmitidos(2))
                .extracting(EnviarNotificacionCommand::destinatarioEmail)
                .containsExactly("ana.gomez@soyuco.edu.co", "luis.diaz@soyuco.edu.co");
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeCompartirElIdDelEvento_cuandoUnMismoEventoProduceVariosCorreos() throws Exception {
        // Arrange — es lo que obliga a que la idempotencia sea por evento Y destinatario
        String idEvento = UUID.randomUUID().toString();

        // Act
        adapter.onEstudiantesFichaPerfilAsignados(
                mensajeCon(idEvento, DOS_ESTUDIANTES, 1L), channel);

        // Assert
        assertThat(comandosEmitidos(2))
                .extracting(EnviarNotificacionCommand::idEvento)
                .containsOnly(idEvento);
    }

    @Test
    void debeUsarElTipoDeEstudiante_cuandoSeAbreElAbanico() throws Exception {
        // Act
        adapter.onEstudiantesFichaPerfilAsignados(
                mensajeCon(UUID.randomUUID().toString(), DOS_ESTUDIANTES, 1L), channel);

        // Assert
        assertThat(comandosEmitidos(2))
                .extracting(EnviarNotificacionCommand::tipo)
                .containsOnly(TipoNotificacion.ESTUDIANTES_FICHA_PERFIL_ASIGNADOS);
    }

    @Test
    void debePersonalizarElCuerpoConElNombreDeCadaEstudiante_cuandoSeAbreElAbanico()
            throws Exception {
        // Act
        adapter.onEstudiantesFichaPerfilAsignados(
                mensajeCon(UUID.randomUUID().toString(), DOS_ESTUDIANTES, 1L), channel);

        // Assert
        List<EnviarNotificacionCommand> comandos = comandosEmitidos(2);
        assertThat(comandos.get(0).cuerpo()).contains("Ana Gomez", "Sistema de gestión");
        assertThat(comandos.get(1).cuerpo()).contains("Luis Diaz", "Sistema de gestión");
    }

    @Test
    void debeNoEnviarNada_cuandoElEventoLlegaSinEstudiantes() throws Exception {
        // Act
        adapter.onEstudiantesFichaPerfilAsignados(
                mensajeCon(UUID.randomUUID().toString(), "[]", 2L), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeNoEnviarNada_cuandoElCampoDeEstudiantesLlegaNulo() throws Exception {
        // Act
        adapter.onEstudiantesFichaPerfilAsignados(
                mensajeCon(UUID.randomUUID().toString(), "null", 3L), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicAck(3L, false);
    }
}
