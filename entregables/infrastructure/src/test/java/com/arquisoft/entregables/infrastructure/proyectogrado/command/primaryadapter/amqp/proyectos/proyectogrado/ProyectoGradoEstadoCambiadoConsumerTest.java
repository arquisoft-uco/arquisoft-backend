package com.arquisoft.entregables.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.PurgarArtefactosProyectoGradoInteractor;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.PurgarArtefactosProyectoGradoCommand;
import com.arquisoft.entregables.application.artefacto.command.result.PurgaArtefactosProyectoGradoResult;
import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.interactor.PurgarPaquetesProyectoGradoInteractor;
import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.PurgarPaquetesProyectoGradoCommand;
import com.arquisoft.entregables.application.paqueteentregable.command.result.PurgaPaquetesProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.ActualizarEstadoProyectoGradoInteractor;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.ActualizarEstadoProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.arquisoft.shared.util.UtilUUID;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoEstadoCambiadoConsumerTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-08T10:00:00Z");

    @Mock
    private ActualizarEstadoProyectoGradoInteractor actualizarEstadoProyectoGradoInteractor;
    @Mock
    private PurgarPaquetesProyectoGradoInteractor purgarPaquetesProyectoGradoInteractor;
    @Mock
    private PurgarArtefactosProyectoGradoInteractor purgarArtefactosProyectoGradoInteractor;
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
                purgarPaquetesProyectoGradoInteractor,
                purgarArtefactosProyectoGradoInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        lenient().when(purgarPaquetesProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new PurgaPaquetesProyectoGradoResult(proyecto, 1));
        lenient().when(purgarArtefactosProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new PurgaArtefactosProyectoGradoResult(proyecto, 2));
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

    @Test
    void debeActualizarYPurgarSoloArtefactos_cuandoFinalizado() throws Exception {
        // Arrange
        when(actualizarEstadoProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionEstadoProyectoGradoResult.Actualizado(proyecto, "FINALIZADO"));

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), "FINALIZADO", 1L, false), channel);

        // Assert
        var orden = inOrder(actualizarEstadoProyectoGradoInteractor, purgarArtefactosProyectoGradoInteractor, channel);
        orden.verify(actualizarEstadoProyectoGradoInteractor)
                .ejecutar(new ActualizarEstadoProyectoGradoCommand(proyecto, "FINALIZADO", OCURRIDO_EN));
        orden.verify(purgarArtefactosProyectoGradoInteractor)
                .ejecutar(new PurgarArtefactosProyectoGradoCommand(proyecto));
        orden.verify(channel).basicAck(1L, false);
        verify(purgarPaquetesProyectoGradoInteractor, never()).ejecutar(any());
        verify(logger).info(ProyectoGradoKey.LOG_ESTADO_RECIBIDO,
                "evt-estado-1", proyecto.toString(), "FINALIZADO", OCURRIDO_EN);
        verify(logger).info(ProyectoGradoKey.LOG_ESTADO_APLICADO, proyecto, "FINALIZADO");
        verify(logger).info(ArtefactoKey.LOG_PURGADOS, proyecto, 2);
    }

    @Test
    void debeActualizarYPurgarPaquetesYArtefactosEnOrden_cuandoCancelado() throws Exception {
        // Arrange
        when(actualizarEstadoProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionEstadoProyectoGradoResult.Actualizado(proyecto, "CANCELADO"));

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), "CANCELADO", 2L, false), channel);

        // Assert
        var orden = inOrder(actualizarEstadoProyectoGradoInteractor, purgarPaquetesProyectoGradoInteractor,
                purgarArtefactosProyectoGradoInteractor, channel);
        orden.verify(actualizarEstadoProyectoGradoInteractor).ejecutar(any());
        orden.verify(purgarPaquetesProyectoGradoInteractor)
                .ejecutar(new PurgarPaquetesProyectoGradoCommand(proyecto));
        orden.verify(purgarArtefactosProyectoGradoInteractor)
                .ejecutar(new PurgarArtefactosProyectoGradoCommand(proyecto));
        orden.verify(channel).basicAck(2L, false);
        verify(logger).info(PaqueteEntregableKey.LOG_PURGADOS, proyecto, 1);
        verify(logger).info(ArtefactoKey.LOG_PURGADOS, proyecto, 2);
    }

    @Test
    void debeSoloActualizar_cuandoElEstadoEsAtrasado() throws Exception {
        // Arrange
        when(actualizarEstadoProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionEstadoProyectoGradoResult.Actualizado(proyecto, "ATRASADO"));

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), "ATRASADO", 3L, false), channel);

        // Assert
        verify(actualizarEstadoProyectoGradoInteractor)
                .ejecutar(new ActualizarEstadoProyectoGradoCommand(proyecto, "ATRASADO", OCURRIDO_EN));
        verify(purgarPaquetesProyectoGradoInteractor, never()).ejecutar(any());
        verify(purgarArtefactosProyectoGradoInteractor, never()).ejecutar(any());
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeVolverAPurgar_cuandoElResultadoEsDescartadoYElEstadoEsTerminal() throws Exception {
        // Arrange
        when(actualizarEstadoProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionEstadoProyectoGradoResult.Descartado(proyecto, OCURRIDO_EN));

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), "CANCELADO", 4L, true), channel);

        // Assert
        verify(logger).info(ProyectoGradoKey.LOG_ESTADO_DESCARTADO, proyecto, OCURRIDO_EN);
        verify(purgarPaquetesProyectoGradoInteractor).ejecutar(new PurgarPaquetesProyectoGradoCommand(proyecto));
        verify(purgarArtefactosProyectoGradoInteractor).ejecutar(new PurgarArtefactosProyectoGradoCommand(proyecto));
        verify(channel).basicAck(4L, false);
    }

    @Test
    void debeNoPurgarYReencolar_cuandoActualizarFallaPorCausaTransitoria() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(actualizarEstadoProyectoGradoInteractor).ejecutar(any());

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), "FINALIZADO", 5L, false), channel);

        // Assert
        verify(purgarArtefactosProyectoGradoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(5L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoLaPurgaFallaYaReentregado() throws Exception {
        // Arrange
        when(actualizarEstadoProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionEstadoProyectoGradoResult.Descartado(proyecto, OCURRIDO_EN));
        doThrow(new QueryTimeoutException("timeout")).when(purgarArtefactosProyectoGradoInteractor).ejecutar(any());

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), "FINALIZADO", 6L, true), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }

    @Test
    void debeApartarADlq_cuandoElEstadoEsRechazadoComoInvalido() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("estado fuera del catalogo"))
                .when(actualizarEstadoProyectoGradoInteractor).ejecutar(any());

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), "INEXISTENTE", 7L, false), channel);

        // Assert
        verify(purgarArtefactosProyectoGradoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(7L, false, false);
    }

    @Test
    void debeApartarADlq_cuandoElPayloadEsInvalido() throws Exception {
        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje("no-uuid", "FINALIZADO", 8L, false), channel);

        // Assert
        verify(actualizarEstadoProyectoGradoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(8L, false, false);
    }
}
