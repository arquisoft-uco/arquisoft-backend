package com.arquisoft.entregables.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.PurgarArtefactosProyectoGradoInteractor;
import com.arquisoft.entregables.application.artefacto.command.result.PurgaArtefactosProyectoGradoResult;
import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.interactor.PurgarPaquetesProyectoGradoInteractor;
import com.arquisoft.entregables.application.paqueteentregable.command.result.PurgaPaquetesProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.CancelarProyectoGradoInteractor;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;
import com.arquisoft.shared.minio.exception.MinioOperationException;
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
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoCanceladoConsumerTest {

    @Mock
    private CancelarProyectoGradoInteractor interactor;

    @Mock
    private PurgarPaquetesProyectoGradoInteractor purgarPaquetesInteractor;

    @Mock
    private PurgarArtefactosProyectoGradoInteractor purgarArtefactosInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private ProyectoGradoCanceladoConsumer consumer;

    private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new ProyectoGradoCanceladoConsumer(
                interactor,
                purgarPaquetesInteractor,
                purgarArtefactosInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        lenient().when(purgarPaquetesInteractor.ejecutar(any()))
                .thenReturn(new PurgaPaquetesProyectoGradoResult(proyectoGrado, 1));
        lenient().when(purgarArtefactosInteractor.ejecutar(any()))
                .thenReturn(new PurgaArtefactosProyectoGradoResult(proyectoGrado, 2));
    }

    private Message mensaje(long deliveryTag) {
        return mensaje(deliveryTag, false);
    }

    private Message mensaje(long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "evt-1",
                    "ocurridoEn": "2026-11-08T10:00:00Z",
                    "proyectoGradoId": "%s",
                    "tituloProyecto": "Sistema de gestion",
                    "motivo": "Falta de avance",
                    "estudiantes": [],
                    "asesores": []
                }
                """.formatted(proyectoGrado);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeCancelarYLuegoPurgarPaquetesYArtefactos_cuandoLlegaElEvento() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new CancelacionProyectoGradoResult.Cancelado(proyectoGrado));

        // Act
        consumer.onProyectoGradoCancelado(mensaje(1L), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(CancelarProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGrado);
        var orden = inOrder(interactor, purgarPaquetesInteractor, purgarArtefactosInteractor);
        orden.verify(interactor).ejecutar(any());
        orden.verify(purgarPaquetesInteractor).ejecutar(any());
        orden.verify(purgarArtefactosInteractor).ejecutar(any());
        verify(logger).info(ProyectoGradoKey.LOG_CANCELADO, proyectoGrado);
        verify(logger).info(PaqueteEntregableKey.LOG_PURGADOS, proyectoGrado, 1);
        verify(logger).info(ArtefactoKey.LOG_PURGADOS, proyectoGrado, 2);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeVolverAPurgarYConfirmar_cuandoElResultadoEsDuplicado() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new CancelacionProyectoGradoResult.Duplicado(proyectoGrado));

        // Act
        consumer.onProyectoGradoCancelado(mensaje(2L), channel);

        // Assert
        verify(logger).info(ProyectoGradoKey.LOG_YA_CANCELADO, proyectoGrado);
        verify(purgarPaquetesInteractor).ejecutar(any());
        verify(purgarArtefactosInteractor).ejecutar(any());
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeReencolarSinRevertirLaCancelacion_cuandoLaPurgaFallaEnLaPrimeraEntrega() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new CancelacionProyectoGradoResult.Cancelado(proyectoGrado));
        doThrow(MinioOperationException.eliminacionFallida(new IllegalStateException("minio caido")))
                .when(purgarPaquetesInteractor).ejecutar(any());

        // Act
        consumer.onProyectoGradoCancelado(mensaje(3L), channel);

        // Assert
        verify(interactor).ejecutar(any());
        verify(purgarArtefactosInteractor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, true);
        verify(channel, never()).basicAck(3L, false);
    }

    @Test
    void debePurgarDeNuevoYConfirmar_cuandoLaReentregaEncuentraElProyectoYaCancelado() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new CancelacionProyectoGradoResult.Duplicado(proyectoGrado));

        // Act
        consumer.onProyectoGradoCancelado(mensaje(4L, true), channel);

        // Assert
        verify(purgarPaquetesInteractor).ejecutar(any());
        verify(purgarArtefactosInteractor).ejecutar(any());
        verify(channel).basicAck(4L, false);
    }

    @Test
    void debeEnviarALaDlqSinPurgar_cuandoElProyectoNoExiste() throws Exception {
        // Arrange
        doThrow(new ProyectoGradoNoEncontradoException(proyectoGrado)).when(interactor).ejecutar(any());

        // Act
        consumer.onProyectoGradoCancelado(mensaje(5L), channel);

        // Assert
        verify(purgarPaquetesInteractor, never()).ejecutar(any());
        verify(purgarArtefactosInteractor, never()).ejecutar(any());
        verify(channel).basicNack(5L, false, false);
    }
}
