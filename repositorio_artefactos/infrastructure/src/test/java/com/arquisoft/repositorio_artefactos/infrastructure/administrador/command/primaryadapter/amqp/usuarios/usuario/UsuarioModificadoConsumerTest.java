package com.arquisoft.repositorio_artefactos.infrastructure.administrador.command.primaryadapter.amqp.usuarios.usuario;

import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor.ActualizarAdministradorInteractor;
import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.ActualizarAdministradorCommand;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.ActualizacionAdministradorResult;
import com.arquisoft.shared.logger.AppLogger;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioModificadoConsumerTest {

    private static final String OCURRIDO_EN = "2026-09-16T10:00:00Z";

    @Mock
    private ActualizarAdministradorInteractor actualizarAdministradorInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private UsuarioModificadoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioModificadoConsumer(
                actualizarAdministradorInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(actualizarAdministradorInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionAdministradorResult.Actualizada(UtilUUID.generarNuevoUUID()));
    }

    private Message mensajeCon(UUID usuario, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "usuario": "%s",
                    "identificador": "20161020999",
                    "nombre": "Ana Actualizada",
                    "email": "actualizada@uco.edu.co"
                }
                """.formatted(UtilUUID.generarNuevoUUID(), OCURRIDO_EN, usuario);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarElInteractorYConfirmar_cuandoElResultadoEsActualizada() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        adapter.onUsuarioModificado(mensajeCon(usuario, 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(ActualizarAdministradorCommand.class);
        verify(actualizarAdministradorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(usuario);
        assertThat(captor.getValue().identificador()).isEqualTo("20161020999");
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Actualizada");
        assertThat(captor.getValue().email()).isEqualTo("actualizada@uco.edu.co");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(Instant.parse(OCURRIDO_EN));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarElMensaje_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        when(actualizarAdministradorInteractor.ejecutar(any())).thenReturn(
                new ActualizacionAdministradorResult.Descartada(UtilUUID.generarNuevoUUID(), Instant.now()));

        // Act
        adapter.onUsuarioModificado(mensajeCon(UtilUUID.generarNuevoUUID(), 2L, false), channel);

        // Assert
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeConfirmarElMensaje_cuandoElResultadoEsNoReplicado() throws Exception {
        // Arrange
        when(actualizarAdministradorInteractor.ejecutar(any())).thenReturn(
                new ActualizacionAdministradorResult.NoReplicado(UtilUUID.generarNuevoUUID()));

        // Act
        adapter.onUsuarioModificado(mensajeCon(UtilUUID.generarNuevoUUID(), 3L, false), channel);

        // Assert
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeEnviarNackConReintento_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(actualizarAdministradorInteractor).ejecutar(any());

        // Act
        adapter.onUsuarioModificado(mensajeCon(UtilUUID.generarNuevoUUID(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarNackSinReintento_cuandoElFalloEsTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(actualizarAdministradorInteractor).ejecutar(any());

        // Act
        adapter.onUsuarioModificado(mensajeCon(UtilUUID.generarNuevoUUID(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("id invalido")).when(actualizarAdministradorInteractor).ejecutar(any());

        // Act
        adapter.onUsuarioModificado(mensajeCon(UtilUUID.generarNuevoUUID(), 6L, false), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
