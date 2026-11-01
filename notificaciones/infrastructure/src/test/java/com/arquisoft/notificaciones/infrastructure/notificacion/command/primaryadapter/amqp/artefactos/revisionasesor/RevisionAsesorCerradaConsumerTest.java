package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.revisionasesor;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.arquisoft.shared.util.UtilUUID;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RevisionAsesorCerradaConsumerTest {

    private static final String DOS_ESTUDIANTES = """
            { "nombre": "Luis Perez", "email": "luis@uco.edu.co" },
            { "nombre": "Sara Mora", "email": "sara@uco.edu.co" }
            """;

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private RevisionAsesorCerradaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new RevisionAsesorCerradaConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt", "luis@uco.edu.co"));
    }

    private static Message mensaje(String cuerpo, long deliveryTag, boolean reentregado) {
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(cuerpo.getBytes()).andProperties(props).build();
    }

    private static String payloadConEstudiantes(String idEvento, String estudiantes) {
        return """
                {
                    "idEvento": "%s",
                    "documento": "Anteproyecto",
                    "numeroVersion": 3,
                    "asesor": { "nombre": "Ana Perez", "email": "ana.perez@uco.edu.co" },
                    "estudiantes": [%s]
                }
                """.formatted(idEvento, estudiantes);
    }

    @Test
    void debeEnviarUnCorreoPorEstudiante_cuandoElEventoTraeVarios() throws Exception {
        // Arrange
        var idEvento = UtilUUID.generarNuevoUUID().toString();

        // Act
        consumer.onRevisionAsesorCerrada(
                mensaje(payloadConEstudiantes(idEvento, DOS_ESTUDIANTES), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, times(2)).ejecutar(captor.capture());
        var comandos = captor.getAllValues();
        assertThat(comandos).extracting(EnviarNotificacionCommand::destinatarioEmail)
                .containsExactly("luis@uco.edu.co", "sara@uco.edu.co");
        assertThat(comandos).allSatisfy(command -> {
            assertThat(command.idEvento()).isEqualTo(idEvento);
            assertThat(command.tipo()).isEqualTo(TipoNotificacion.REVISION_ASESOR_CERRADA);
            assertThat(command.asunto()).contains("Anteproyecto").contains("3");
            assertThat(command.cuerpo()).contains("Ana Perez").contains("Anteproyecto").contains("3");
            assertThat(command.pie()).isNotEmpty();
        });
        assertThat(comandos.get(0).destinatarioNombre()).isEqualTo("Luis Perez");
        assertThat(comandos.get(0).cuerpo()).contains("Luis Perez");
        assertThat(comandos.get(1).destinatarioNombre()).isEqualTo("Sara Mora");
        assertThat(comandos.get(1).cuerpo()).contains("Sara Mora");
        verify(logger).info(ConsumidorKey.LOG_REVISION_ASESOR_CERRADA_RECIBIDO, idEvento, 2);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeNoEnviar_cuandoListaVacia() throws Exception {
        // Arrange
        var idEvento = UtilUUID.generarNuevoUUID().toString();

        // Act
        consumer.onRevisionAsesorCerrada(mensaje(payloadConEstudiantes(idEvento, ""), 2L, false), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(logger).info(ConsumidorKey.LOG_REVISION_ASESOR_CERRADA_RECIBIDO, idEvento, 0);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElMensajeEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("payload inconsistente"))
                .when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        consumer.onRevisionAsesorCerrada(
                mensaje(payloadConEstudiantes(UtilUUID.generarNuevoUUID().toString(), DOS_ESTUDIANTES), 3L, false),
                channel);

        // Assert
        verify(channel).basicNack(3L, false, false);
    }

    @Test
    void debeReencolar_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        consumer.onRevisionAsesorCerrada(
                mensaje(payloadConEstudiantes(UtilUUID.generarNuevoUUID().toString(), DOS_ESTUDIANTES), 4L, false),
                channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeDescartar_cuandoElFalloTransitorioYaFueReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        consumer.onRevisionAsesorCerrada(
                mensaje(payloadConEstudiantes(UtilUUID.generarNuevoUUID().toString(), DOS_ESTUDIANTES), 5L, true),
                channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }
}
