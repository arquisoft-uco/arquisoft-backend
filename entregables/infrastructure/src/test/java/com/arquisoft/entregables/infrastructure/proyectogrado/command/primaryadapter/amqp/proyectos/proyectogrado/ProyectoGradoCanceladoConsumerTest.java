package com.arquisoft.entregables.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.CancelarProyectoGradoInteractor;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoCanceladoConsumerTest {

    @Mock
    private CancelarProyectoGradoInteractor interactor;

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
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensaje(long deliveryTag) {
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
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarInteractor_cuandoLlegaElEvento() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new CancelacionProyectoGradoResult.Cancelado(proyectoGrado, 1, 2));

        // Act
        consumer.onProyectoGradoCancelado(mensaje(1L), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(CancelarProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGrado);
        verify(logger).info(ProyectoGradoKey.LOG_CANCELADO, proyectoGrado);
        verify(logger).info(PaqueteEntregableKey.LOG_PURGADOS, proyectoGrado, 1);
        verify(logger).info(ArtefactoKey.LOG_PURGADOS, proyectoGrado, 2);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarYLoguearConteos_cuandoElResultadoEsDuplicado() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new CancelacionProyectoGradoResult.Duplicado(proyectoGrado, 0, 3));

        // Act
        consumer.onProyectoGradoCancelado(mensaje(2L), channel);

        // Assert
        verify(logger).info(ProyectoGradoKey.LOG_YA_CANCELADO, proyectoGrado);
        verify(logger).info(PaqueteEntregableKey.LOG_PURGADOS, proyectoGrado, 0);
        verify(logger).info(ArtefactoKey.LOG_PURGADOS, proyectoGrado, 3);
        verify(channel).basicAck(2L, false);
    }
}
