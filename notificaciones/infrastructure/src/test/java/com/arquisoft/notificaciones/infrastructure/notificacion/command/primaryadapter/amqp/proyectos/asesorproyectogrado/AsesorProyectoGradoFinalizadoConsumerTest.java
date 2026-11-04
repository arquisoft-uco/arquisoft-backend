package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.asesorproyectogrado;

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
import org.springframework.dao.QueryTimeoutException;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsesorProyectoGradoFinalizadoConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private AsesorProyectoGradoFinalizadoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new AsesorProyectoGradoFinalizadoConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt-1", "luis.diaz@soyuco.edu.co"));
    }

    private Message mensaje(long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "evt-1",
                    "proyectoGradoId": "44444444-4444-4444-4444-444444444444",
                    "tituloProyecto": "Sistema de gestión",
                    "asesor": {"nombre": "Luis Diaz", "email": "luis.diaz@soyuco.edu.co"}
                }
                """;
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeNotificarAlAsesorConSuTipoNombreYTitulo_cuandoSeFinalizaSuVinculo() throws Exception {
        // Act
        adapter.onAsesorProyectoGradoFinalizado(mensaje(1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor).ejecutar(captor.capture());
        var comando = captor.getValue();
        assertThat(comando.idEvento()).isEqualTo("evt-1");
        assertThat(comando.tipo()).isEqualTo(TipoNotificacion.ASESOR_PROYECTO_GRADO_FINALIZADO);
        assertThat(comando.destinatarioNombre()).isEqualTo("Luis Diaz");
        assertThat(comando.destinatarioEmail()).isEqualTo("luis.diaz@soyuco.edu.co");
        assertThat(comando.asunto()).contains("Sistema de gestión");
        assertThat(comando.cuerpo()).contains("Luis Diaz", "Sistema de gestión");
        assertThat(comando.pie()).isNotEmpty();
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeReencolar_cuandoFallaUnRecursoTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        adapter.onAsesorProyectoGradoFinalizado(mensaje(2L, false), channel);

        // Assert
        verify(channel).basicNack(2L, false, true);
    }

    @Test
    void debeEnviarALaDlq_cuandoFallaUnRecursoTransitorioYElMensajeYaFueReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        adapter.onAsesorProyectoGradoFinalizado(mensaje(3L, true), channel);

        // Assert
        verify(channel).basicNack(3L, false, false);
    }

    @Test
    void debeEnviarALaDlqSinReencolar_cuandoElMensajeEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("payload invalido"))
                .when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        adapter.onAsesorProyectoGradoFinalizado(mensaje(4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, false);
    }
}
