package com.arquisoft.fichas.infrastructure.representantecomite.command.primaryadapter.amqp.usuarios.representantecomite;

import com.arquisoft.fichas.application.representantecomite.command.primaryport.interactor.AgregarRepresentanteComiteInteractor;
import com.arquisoft.fichas.application.representantecomite.command.primaryport.model.AgregarRepresentanteComiteCommand;
import com.arquisoft.fichas.application.representantecomite.command.result.AgregacionRepresentanteComiteResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepresentanteComiteAgregadoConsumerTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private AgregarRepresentanteComiteInteractor agregarRepresentanteComiteInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private RepresentanteComiteAgregadoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new RepresentanteComiteAgregadoConsumer(
                agregarRepresentanteComiteInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(agregarRepresentanteComiteInteractor.ejecutar(any()))
                .thenReturn(new AgregacionRepresentanteComiteResult.Agregada(UtilUUID.generarNuevoUUID()));
    }

    private Message mensajeCon(String idEvento, UUID usuario, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "usuario": "%s",
                    "identificador": "20161020123",
                    "nombre": "Ana Perez",
                    "email": "ana@uco.edu.co"
                }
                """.formatted(idEvento, OCURRIDO_EN, usuario);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        props.setHeader("X-Trace-Id", "trace-123");
        props.setHeader("X-User-Id", "user-456");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    private Message mensaje(long deliveryTag) {
        return mensajeCon(UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID(), deliveryTag, false);
    }

    @Test
    void debeConstruirElCommandRegistrarYConfirmar_cuandoElResultadoEsAgregada() throws Exception {
        // Arrange
        var idEvento = UtilUUID.generarNuevoUUID().toString();
        var usuario = UtilUUID.generarNuevoUUID();
        when(agregarRepresentanteComiteInteractor.ejecutar(any()))
                .thenReturn(new AgregacionRepresentanteComiteResult.Agregada(usuario));

        // Act
        adapter.onRepresentanteComiteAgregado(mensajeCon(idEvento, usuario, 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarRepresentanteComiteCommand.class);
        verify(agregarRepresentanteComiteInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(usuario);
        assertThat(captor.getValue().identificador()).isEqualTo("20161020123");
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Perez");
        assertThat(captor.getValue().email()).isEqualTo("ana@uco.edu.co");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(OCURRIDO_EN);
        verify(logger).info(eq(RepresentanteComiteKey.LOG_AGREGADO_RECIBIDO), eq(idEvento),
                eq(usuario.toString()), eq("a***@uco.edu.co"));
        verify(logger).info(RepresentanteComiteKey.LOG_AGREGADO, usuario);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarLaReactivacionYConfirmar_cuandoElResultadoEsReactivada() throws Exception {
        // Arrange
        var representanteComite = UtilUUID.generarNuevoUUID();
        when(agregarRepresentanteComiteInteractor.ejecutar(any()))
                .thenReturn(new AgregacionRepresentanteComiteResult.Reactivada(representanteComite));

        // Act
        adapter.onRepresentanteComiteAgregado(mensaje(2L), channel);

        // Assert
        verify(logger).info(RepresentanteComiteKey.LOG_REACTIVADO, representanteComite);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeRegistrarElDuplicadoYConfirmar_cuandoElResultadoEsDuplicada() throws Exception {
        // Arrange
        var representanteComite = UtilUUID.generarNuevoUUID();
        when(agregarRepresentanteComiteInteractor.ejecutar(any()))
                .thenReturn(new AgregacionRepresentanteComiteResult.Duplicada(representanteComite));

        // Act
        adapter.onRepresentanteComiteAgregado(mensaje(3L), channel);

        // Assert
        verify(logger).info(RepresentanteComiteKey.LOG_DUPLICADO, representanteComite);
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeRegistrarEnDebugYConfirmar_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        var representanteComite = UtilUUID.generarNuevoUUID();
        var ocurridoEnVigente = Instant.parse("2026-09-25T10:00:00Z");
        when(agregarRepresentanteComiteInteractor.ejecutar(any())).thenReturn(
                new AgregacionRepresentanteComiteResult.Descartada(representanteComite, ocurridoEnVigente));

        // Act
        adapter.onRepresentanteComiteAgregado(mensaje(4L), channel);

        // Assert
        verify(logger).debug(eq(RepresentanteComiteKey.LOG_DESCARTADO), eq(representanteComite),
                eq(ocurridoEnVigente));
        verify(channel).basicAck(4L, false);
    }

    @Test
    void debeEnviarNackConReintento_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout"))
                .when(agregarRepresentanteComiteInteractor).ejecutar(any());

        // Act
        adapter.onRepresentanteComiteAgregado(mensajeCon(UtilUUID.generarNuevoUUID().toString(),
                UtilUUID.generarNuevoUUID(), 5L, false), channel);

        // Assert
        verify(channel).basicNack(5L, false, true);
    }

    @Test
    void debeEnviarNackSinReintento_cuandoElFalloEsTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout"))
                .when(agregarRepresentanteComiteInteractor).ejecutar(any());

        // Act
        adapter.onRepresentanteComiteAgregado(mensajeCon(UtilUUID.generarNuevoUUID().toString(),
                UtilUUID.generarNuevoUUID(), 6L, true), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("payload invalido"))
                .when(agregarRepresentanteComiteInteractor).ejecutar(any());

        // Act
        adapter.onRepresentanteComiteAgregado(mensaje(7L), channel);

        // Assert
        verify(channel).basicNack(7L, false, false);
    }
}
