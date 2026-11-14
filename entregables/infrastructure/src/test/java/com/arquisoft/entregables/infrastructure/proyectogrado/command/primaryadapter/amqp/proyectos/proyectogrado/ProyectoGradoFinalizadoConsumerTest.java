package com.arquisoft.entregables.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.FinalizarProyectoGradoInteractor;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.FinalizarProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoFinalizadoConsumerTest {

    @Mock
    private FinalizarProyectoGradoInteractor interactor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private ProyectoGradoFinalizadoConsumer consumer;

    private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new ProyectoGradoFinalizadoConsumer(
                interactor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensaje(String proyecto, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "evt-1",
                    "ocurridoEn": "2026-11-08T10:00:00Z",
                    "proyectoGradoId": "%s",
                    "tituloProyecto": "Sistema de gestion",
                    "estudiantes": []
                }
                """.formatted(proyecto);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeFinalizarYConfirmar_cuandoLlegaElEvento() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new FinalizacionProyectoGradoResult.Finalizado(proyectoGrado, 2));

        // Act
        consumer.onProyectoGradoFinalizado(mensaje(proyectoGrado.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(FinalizarProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGrado);
        verify(logger).info(ProyectoGradoKey.LOG_FINALIZADO, proyectoGrado);
        verify(logger).info(ArtefactoKey.LOG_PURGADOS, proyectoGrado, 2);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarYLoguearConteos_cuandoElResultadoEsDuplicado() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new FinalizacionProyectoGradoResult.Duplicado(proyectoGrado, 3));

        // Act
        consumer.onProyectoGradoFinalizado(mensaje(proyectoGrado.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(ProyectoGradoKey.LOG_YA_FINALIZADO, proyectoGrado);
        verify(logger).info(ArtefactoKey.LOG_PURGADOS, proyectoGrado, 3);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarALaDlq_cuandoElProyectoNoExiste() throws Exception {
        // Arrange
        doThrow(new ProyectoGradoNoEncontradoException(proyectoGrado)).when(interactor).ejecutar(any());

        // Act
        consumer.onProyectoGradoFinalizado(mensaje(proyectoGrado.toString(), 3L, false), channel);

        // Assert
        verify(channel).basicNack(3L, false, false);
        verify(channel, never()).basicAck(3L, false);
    }

    @Test
    void debeEnviarNackSinInvocarElInteractor_cuandoElPayloadEsMalformado() throws Exception {
        // Act
        consumer.onProyectoGradoFinalizado(mensaje("no-es-un-uuid", 4L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(4L, false, false);
    }

    @Test
    void debeReencolarUnaVezYLuegoEnviarALaDlq_cuandoElFalloEsTransitorio() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onProyectoGradoFinalizado(mensaje(proyectoGrado.toString(), 5L, false), channel);
        consumer.onProyectoGradoFinalizado(mensaje(proyectoGrado.toString(), 6L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, true);
        verify(channel).basicNack(6L, false, false);
    }
}
