package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.estadofichaperfil;

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
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.dao.QueryTimeoutException;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EstadoFichaPerfilAgregadoConsumerTest {

    private static final String FICHA_PERFIL_ID = "11111111-1111-1111-1111-111111111111";

    private static final String PAYLOAD_TRES_ESTUDIANTES = """
            {
                "idEvento": "evt-1",
                "estadoFichaPerfilId": "22222222-2222-2222-2222-222222222222",
                "fichaPerfilId": "11111111-1111-1111-1111-111111111111",
                "tituloProyecto": "Sistema de gestión",
                "estadoFicha": "DISPONIBLE_PARA_EVALUACION",
                "estadoFichaNombre": "Disponible Para Evaluacion",
                "estudiantes": [
                    {"nombre": "Ana Gomez", "email": "ana.gomez@soyuco.edu.co"},
                    {"nombre": "Luis Diaz", "email": "luis.diaz@soyuco.edu.co"},
                    {"nombre": "Eva Ruiz", "email": "eva.ruiz@soyuco.edu.co"}
                ]
            }
            """;

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EstadoFichaPerfilAgregadoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoFichaPerfilAgregadoConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt-1", "ana.gomez@soyuco.edu.co"));
    }

    private static Message mensaje(String json, long deliveryTag, boolean reentregado) {
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeNotificarACadaEstudianteConSuCorreoPersonalizado_cuandoLlegaElCambioDeEstado() throws Exception {
        // Act
        adapter.onEstadoFichaPerfilAgregado(mensaje(PAYLOAD_TRES_ESTUDIANTES, 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, times(3)).ejecutar(captor.capture());
        var comandos = captor.getAllValues();
        assertThat(comandos).extracting(EnviarNotificacionCommand::destinatarioEmail).containsExactly(
                "ana.gomez@soyuco.edu.co", "luis.diaz@soyuco.edu.co", "eva.ruiz@soyuco.edu.co");
        assertThat(comandos).extracting(EnviarNotificacionCommand::tipo)
                .containsOnly(TipoNotificacion.ESTADO_FICHA_PERFIL_AGREGADO);
        assertThat(comandos).extracting(EnviarNotificacionCommand::idEvento).containsOnly("evt-1");
        assertThat(comandos.get(1).asunto()).contains("Sistema de gestión", "Disponible Para Evaluacion");
        assertThat(comandos.get(1).cuerpo()).contains("Luis Diaz", "Sistema de gestión", "Disponible Para Evaluacion");
        assertThat(comandos.get(1).pie()).isNotBlank();
        verify(logger).info(any(ClaveMensaje.class), eq(FICHA_PERFIL_ID), eq("DISPONIBLE_PARA_EVALUACION"), eq(3));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarSinNotificar_cuandoElEventoNoTraeEstudiantes() throws Exception {
        // Arrange
        var json = """
                {"idEvento":"evt-2","fichaPerfilId":"11111111-1111-1111-1111-111111111111",
                 "tituloProyecto":"Sistema","estadoFicha":"DESCARTADA","estadoFichaNombre":"Descartada"}
                """;

        // Act
        adapter.onEstadoFichaPerfilAgregado(mensaje(json, 2L, false), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarNackConReintento_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        adapter.onEstadoFichaPerfilAgregado(mensaje(PAYLOAD_TRES_ESTUDIANTES, 3L, false), channel);

        // Assert
        verify(channel).basicNack(3L, false, true);
    }

    @Test
    void debeEnviarNackSinReintento_cuandoElFalloEsTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        adapter.onEstadoFichaPerfilAgregado(mensaje(PAYLOAD_TRES_ESTUDIANTES, 4L, true), channel);

        // Assert
        verify(channel).basicNack(4L, false, false);
    }

    @Test
    void debeEnviarALaDlqSinNotificar_cuandoElMensajeNoEsJsonValido() throws Exception {
        // Act
        adapter.onEstadoFichaPerfilAgregado(mensaje("no-es-json", 5L, false), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicNack(5L, false, false);
    }
}
