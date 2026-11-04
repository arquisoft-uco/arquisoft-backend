package com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.primaryadapter.amqp.proyectos.asesorproyectogrado;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.interactor.AgregarAsesorProyectoGradoInteractor;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.model.AgregarAsesorProyectoGradoCommand;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.AgregacionAsesorProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AsesorProyectoGradoKey;
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

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesorProyectoGradoAsignadoConsumerTest {

    private static final String ID_EVENTO = "evt-asignado-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-21T10:00:00Z");

    @Mock
    private AgregarAsesorProyectoGradoInteractor interactor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private AsesorProyectoGradoAsignadoConsumer consumer;

    private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();
    private final UUID asesor = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new AsesorProyectoGradoAsignadoConsumer(
                interactor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensaje(String asesorPayload, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "proyectoGradoId": "%s",
                    "asesorId": "%s"
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, proyectoGrado, asesorPayload);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarInteractorYRegistrarAgregada_cuandoLlegaElEvento() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenReturn(new AgregacionAsesorProyectoGradoResult.Agregada(asesor, proyectoGrado));

        // Act
        consumer.onAsesorProyectoGradoAsignado(mensaje(asesor.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarAsesorProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue())
                .isEqualTo(new AgregarAsesorProyectoGradoCommand(asesor, proyectoGrado, OCURRIDO_EN));
        verify(logger).info(AsesorProyectoGradoKey.LOG_AGREGADO, asesor, proyectoGrado);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarReactivada_cuandoElResultadoEsReactivada() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenReturn(new AgregacionAsesorProyectoGradoResult.Reactivada(asesor, proyectoGrado));

        // Act
        consumer.onAsesorProyectoGradoAsignado(mensaje(asesor.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(AsesorProyectoGradoKey.LOG_REACTIVADO, asesor, proyectoGrado);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeRegistrarDuplicada_cuandoElResultadoEsDuplicada() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenReturn(new AgregacionAsesorProyectoGradoResult.Duplicada(asesor, proyectoGrado));

        // Act
        consumer.onAsesorProyectoGradoAsignado(mensaje(asesor.toString(), 3L, false), channel);

        // Assert
        verify(logger).info(AsesorProyectoGradoKey.LOG_DUPLICADO, asesor, proyectoGrado);
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeRegistrarDescartada_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        var vigente = OCURRIDO_EN.plusSeconds(60);
        when(interactor.ejecutar(any()))
                .thenReturn(new AgregacionAsesorProyectoGradoResult.Descartada(asesor, proyectoGrado, vigente));

        // Act
        consumer.onAsesorProyectoGradoAsignado(mensaje(asesor.toString(), 4L, false), channel);

        // Assert
        verify(logger).debug(AsesorProyectoGradoKey.LOG_DESCARTADO, asesor, proyectoGrado, vigente);
        verify(channel).basicAck(4L, false);
    }

    @Test
    void debeEnviarNackSinReencolarNiInvocarElInteractor_cuandoElPayloadEsMalformado() throws Exception {
        // Act
        consumer.onAsesorProyectoGradoAsignado(mensaje("no-es-un-uuid", 5L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void debeEnviarNackReencolando_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onAsesorProyectoGradoAsignado(mensaje(asesor.toString(), 6L, false), channel);

        // Assert
        verify(channel).basicNack(6L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloEsTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onAsesorProyectoGradoAsignado(mensaje(asesor.toString(), 7L, true), channel);

        // Assert
        verify(channel).basicNack(7L, false, false);
        verify(channel, never()).basicAck(7L, false);
    }
}
