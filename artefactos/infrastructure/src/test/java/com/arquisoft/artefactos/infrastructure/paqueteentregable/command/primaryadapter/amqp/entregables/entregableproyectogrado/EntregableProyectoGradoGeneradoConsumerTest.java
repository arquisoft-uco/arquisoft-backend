package com.arquisoft.artefactos.infrastructure.paqueteentregable.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.interactor.GenerarPaqueteEntregableInteractor;
import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model.GenerarPaqueteEntregableCommand;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.GeneracionPaqueteEntregableResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.PaqueteEntregableKey;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntregableProyectoGradoGeneradoConsumerTest {

    @Mock
    private GenerarPaqueteEntregableInteractor generarPaqueteEntregableInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EntregableProyectoGradoGeneradoConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new EntregableProyectoGradoGeneradoConsumer(
                generarPaqueteEntregableInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(generarPaqueteEntregableInteractor.ejecutar(any()))
                .thenReturn(new GeneracionPaqueteEntregableResult.Generado(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 10L));
    }

    private Message mensajeCon(String entregable, String proyecto, Integer version, long deliveryTag,
                               boolean redelivered) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "2026-11-08T10:00:00Z",
                    "entregableProyectoGrado": "%s",
                    "proyectoGrado": "%s",
                    "version": %s
                }
                """.formatted(UtilUUID.generarNuevoUUID(), entregable, proyecto, version);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(redelivered);

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    private Message mensajeValido(long deliveryTag, boolean redelivered) {
        return mensajeCon(UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID().toString(), 2,
                deliveryTag, redelivered);
    }

    @Test
    void debeInvocarInteractorYConfirmar_cuandoEventoValido() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        consumer.onEntregableProyectoGradoGenerado(
                mensajeCon(entregable.toString(), proyecto.toString(), 3, 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(GenerarPaqueteEntregableCommand.class);
        verify(generarPaqueteEntregableInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().versionEntregable()).isEqualTo(3);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarYRegistrarDuplicado_cuandoElPaqueteYaExiste() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(generarPaqueteEntregableInteractor.ejecutar(any()))
                .thenReturn(new GeneracionPaqueteEntregableResult.Duplicado(entregable));

        // Act
        consumer.onEntregableProyectoGradoGenerado(mensajeValido(2L, false), channel);

        // Assert
        verify(logger).info(PaqueteEntregableKey.LOG_DUPLICADO, entregable);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeApartarADlq_cuandoPayloadInvalido() throws Exception {
        // Act
        consumer.onEntregableProyectoGradoGenerado(mensajeCon("no-uuid", "tampoco", 0, 3L, false), channel);

        // Assert
        verify(generarPaqueteEntregableInteractor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
    }

    @Test
    void debeReencolar_cuandoFallaTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(generarPaqueteEntregableInteractor).ejecutar(any());

        // Act
        consumer.onEntregableProyectoGradoGenerado(mensajeValido(4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFallaTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(generarPaqueteEntregableInteractor).ejecutar(any());

        // Act
        consumer.onEntregableProyectoGradoGenerado(mensajeValido(5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void debeEnviarADlq_cuandoFallaNoTransitoria() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("invalido")).when(generarPaqueteEntregableInteractor).ejecutar(any());

        // Act
        consumer.onEntregableProyectoGradoGenerado(mensajeValido(6L, false), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
