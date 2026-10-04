package com.arquisoft.fichas.infrastructure.representantecomite.command.primaryadapter.amqp.usuarios.representantecomite;

import com.arquisoft.fichas.application.representantecomite.command.primaryport.interactor.RemoverRepresentanteComiteInteractor;
import com.arquisoft.fichas.application.representantecomite.command.primaryport.model.RemoverRepresentanteComiteCommand;
import com.arquisoft.fichas.application.representantecomite.command.result.RemocionRepresentanteComiteResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepresentanteComiteRemovidoConsumerTest {

    private static final String OCURRIDO_EN = "2026-09-24T10:00:00Z";

    @Mock
    private RemoverRepresentanteComiteInteractor removerRepresentanteComiteInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private RepresentanteComiteRemovidoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new RepresentanteComiteRemovidoConsumer(
                removerRepresentanteComiteInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(removerRepresentanteComiteInteractor.ejecutar(any()))
                .thenReturn(new RemocionRepresentanteComiteResult.Removida(UtilUUID.generarNuevoUUID()));
    }

    private Message mensajeCon(UUID idEvento, UUID usuario, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "usuario": "%s",
                    "identificador": "20161020123",
                    "nombre": "Ana Pérez",
                    "email": "ana.perez@uco.edu.co"
                }
                """.formatted(idEvento, OCURRIDO_EN, usuario);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    private Message mensajeCon(UUID usuario, long deliveryTag, boolean reentregado) {
        return mensajeCon(UtilUUID.generarNuevoUUID(), usuario, deliveryTag, reentregado);
    }

    @Test
    void debeInvocarElInteractorConElPayloadYConfirmar_cuandoElResultadoEsRemovida() throws Exception {
        // Arrange
        var idEvento = UtilUUID.generarNuevoUUID();
        var usuario = UtilUUID.generarNuevoUUID();
        when(removerRepresentanteComiteInteractor.ejecutar(any()))
                .thenReturn(new RemocionRepresentanteComiteResult.Removida(usuario));

        // Act
        adapter.onRepresentanteComiteRemovido(mensajeCon(idEvento, usuario, 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(RemoverRepresentanteComiteCommand.class);
        verify(removerRepresentanteComiteInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(usuario);
        assertThat(captor.getValue().identificador()).isEqualTo("20161020123");
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Pérez");
        assertThat(captor.getValue().email()).isEqualTo("ana.perez@uco.edu.co");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(Instant.parse(OCURRIDO_EN));
        verify(logger).info(RepresentanteComiteKey.LOG_REMOVIDO_RECIBIDO, idEvento.toString(), usuario.toString());
        verify(logger).info(RepresentanteComiteKey.LOG_REMOVIDO, usuario);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarLaLapidaYConfirmar_cuandoElResultadoEsLapida() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(removerRepresentanteComiteInteractor.ejecutar(any()))
                .thenReturn(new RemocionRepresentanteComiteResult.Lapida(usuario));

        // Act
        adapter.onRepresentanteComiteRemovido(mensajeCon(usuario, 2L, false), channel);

        // Assert
        verify(logger).info(RepresentanteComiteKey.LOG_LAPIDA, usuario);
        verify(logger, never()).info(eq(RepresentanteComiteKey.LOG_REMOVIDO), any());
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeConfirmarSinInfoDeCierre_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var vigente = Instant.parse("2026-09-25T10:00:00Z");
        when(removerRepresentanteComiteInteractor.ejecutar(any()))
                .thenReturn(new RemocionRepresentanteComiteResult.Descartada(usuario, vigente));

        // Act
        adapter.onRepresentanteComiteRemovido(mensajeCon(usuario, 3L, false), channel);

        // Assert
        verify(logger).debug(RepresentanteComiteKey.LOG_REMOCION_DESCARTADA, usuario, vigente);
        verify(logger, never()).info(any(ClaveMensaje.class), eq(usuario));
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeReencolar_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerRepresentanteComiteInteractor).ejecutar(any());

        // Act
        adapter.onRepresentanteComiteRemovido(mensajeCon(UtilUUID.generarNuevoUUID(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloTransitorioYaFueReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerRepresentanteComiteInteractor).ejecutar(any());

        // Act
        adapter.onRepresentanteComiteRemovido(mensajeCon(UtilUUID.generarNuevoUUID(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void debeEnviarNackSinReencolarNiInvocarInteractor_cuandoElPayloadEsInvalido() throws Exception {
        // Arrange
        var props = new MessageProperties();
        props.setDeliveryTag(6L);
        var json = """
                {"idEvento":"evt-1","ocurridoEn":"%s","usuario":"no-es-un-uuid",
                 "identificador":" ","nombre":"Ana Pérez","email":"ana.perez@uco.edu.co"}
                """.formatted(OCURRIDO_EN);
        var mensaje = MessageBuilder.withBody(json.getBytes()).andProperties(props).build();

        // Act
        adapter.onRepresentanteComiteRemovido(mensaje, channel);

        // Assert
        verify(removerRepresentanteComiteInteractor, never()).ejecutar(any());
        verify(channel).basicNack(6L, false, false);
    }
}
