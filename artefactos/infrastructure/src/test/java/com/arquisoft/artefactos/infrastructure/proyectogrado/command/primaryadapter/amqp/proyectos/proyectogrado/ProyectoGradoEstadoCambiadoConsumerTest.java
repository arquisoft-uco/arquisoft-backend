package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.ActualizarEstadoProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.ActualizarEstadoProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.PurgarVersionesProyectoGradoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.PurgarVersionesProyectoGradoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.result.PurgaVersionesProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.arquisoft.shared.util.UtilUUID;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.dao.QueryTimeoutException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoEstadoCambiadoConsumerTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-08T10:00:00Z");

    @Mock
    private ActualizarEstadoProyectoGradoInteractor actualizarEstadoProyectoGradoInteractor;
    @Mock
    private PurgarVersionesProyectoGradoInteractor purgarVersionesProyectoGradoInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private ProyectoGradoEstadoCambiadoConsumer consumer;

    private final UUID proyecto = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new ProyectoGradoEstadoCambiadoConsumer(
                actualizarEstadoProyectoGradoInteractor,
                purgarVersionesProyectoGradoInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensaje(String proyectoGradoId, String estadoNuevo, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "evt-estado-1",
                    "ocurridoEn": "%s",
                    "proyectoGradoId": "%s",
                    "tituloProyecto": "Sistema de gestion",
                    "estadoAnterior": "EN_PROCESO",
                    "estadoNuevo": "%s",
                    "motivo": "",
                    "estudiantes": [],
                    "asesores": []
                }
                """.formatted(OCURRIDO_EN, proyectoGradoId, estadoNuevo);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"FINALIZADO", "CANCELADO"})
    void debeActualizarYPurgarEnOrden_cuandoElEstadoEsTerminal(String estado) throws Exception {
        // Arrange
        when(actualizarEstadoProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionEstadoProyectoGradoResult.Actualizado(proyecto, estado));
        when(purgarVersionesProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new PurgaVersionesProyectoGradoResult(proyecto, 4));

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), estado, 1L, false), channel);

        // Assert
        var orden = inOrder(actualizarEstadoProyectoGradoInteractor, purgarVersionesProyectoGradoInteractor, channel);
        orden.verify(actualizarEstadoProyectoGradoInteractor)
                .ejecutar(new ActualizarEstadoProyectoGradoCommand(proyecto, estado, OCURRIDO_EN));
        orden.verify(purgarVersionesProyectoGradoInteractor)
                .ejecutar(new PurgarVersionesProyectoGradoCommand(proyecto));
        orden.verify(channel).basicAck(1L, false);
        verify(logger).info(ProyectoGradoKey.LOG_ESTADO_RECIBIDO, "evt-estado-1", proyecto.toString(), estado, OCURRIDO_EN);
        verify(logger).info(ProyectoGradoKey.LOG_ESTADO_APLICADO, proyecto, estado);
        verify(logger).info(VersionArtefactoKey.LOG_PURGADAS, proyecto, 4);
    }

    @Test
    void debeSoloActualizar_cuandoElEstadoEsAtrasado() throws Exception {
        // Arrange
        when(actualizarEstadoProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionEstadoProyectoGradoResult.Actualizado(proyecto, "ATRASADO"));

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), "ATRASADO", 2L, false), channel);

        // Assert
        verify(actualizarEstadoProyectoGradoInteractor)
                .ejecutar(new ActualizarEstadoProyectoGradoCommand(proyecto, "ATRASADO", OCURRIDO_EN));
        verify(purgarVersionesProyectoGradoInteractor, never()).ejecutar(any());
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debePurgar_cuandoElResultadoEsDescartadoYElEstadoEsTerminal() throws Exception {
        // Arrange
        when(actualizarEstadoProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionEstadoProyectoGradoResult.Descartado(proyecto, OCURRIDO_EN));
        when(purgarVersionesProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new PurgaVersionesProyectoGradoResult(proyecto, 0));

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), "FINALIZADO", 3L, true), channel);

        // Assert
        verify(logger).info(ProyectoGradoKey.LOG_ESTADO_DESCARTADO, proyecto, OCURRIDO_EN);
        verify(purgarVersionesProyectoGradoInteractor).ejecutar(new PurgarVersionesProyectoGradoCommand(proyecto));
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeNoPurgarYReencolar_cuandoActualizarFallaPorCausaTransitoria() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(actualizarEstadoProyectoGradoInteractor).ejecutar(any());

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), "FINALIZADO", 4L, false), channel);

        // Assert
        verify(purgarVersionesProyectoGradoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoLaPurgaFallaYaReentregado() throws Exception {
        // Arrange
        when(actualizarEstadoProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionEstadoProyectoGradoResult.Descartado(proyecto, OCURRIDO_EN));
        doThrow(new QueryTimeoutException("timeout")).when(purgarVersionesProyectoGradoInteractor).ejecutar(any());

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), "CANCELADO", 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void debeApartarADlq_cuandoElEstadoEsRechazadoComoInvalido() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("estado fuera del catalogo"))
                .when(actualizarEstadoProyectoGradoInteractor).ejecutar(any());

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), "INEXISTENTE", 6L, false), channel);

        // Assert
        verify(purgarVersionesProyectoGradoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(6L, false, false);
    }

    @Test
    void debeApartarADlq_cuandoElPayloadEsInvalido() throws Exception {
        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje("no-uuid", "FINALIZADO", 7L, false), channel);

        // Assert
        verify(actualizarEstadoProyectoGradoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(7L, false, false);
    }
}
