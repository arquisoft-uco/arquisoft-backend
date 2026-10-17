package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.fichaperfil;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.notificaciones.PlantillaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FichaPerfilAprobadaConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private FichaPerfilAprobadaConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new FichaPerfilAprobadaConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt-1", "ana.gomez@soyuco.edu.co"));
    }

    private Message mensaje(String estadoFicha, long deliveryTag) {
        var payloadJson = """
                {
                    "idEvento": "evt-1",
                    "fichaPerfilId": "11111111-1111-1111-1111-111111111111",
                    "tituloProyecto": "Sistema de gestión",
                    "estadoFicha": "%s",
                    "asesor": {"nombre": "Carlos Ruiz", "email": "carlos.ruiz@soyuco.edu.co"},
                    "estudiantes": [
                        {"estudiante": "22222222-2222-2222-2222-222222222222",
                         "contacto": {"nombre": "Ana Gomez", "email": "ana.gomez@soyuco.edu.co"}},
                        {"estudiante": "33333333-3333-3333-3333-333333333333",
                         "contacto": {"nombre": "Luis Diaz", "email": "luis.diaz@soyuco.edu.co"}}
                    ]
                }
                """.formatted(estadoFicha);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    private List<EnviarNotificacionCommand> comandosEmitidos(int esperados) {
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, times(esperados)).ejecutar(captor.capture());
        return captor.getAllValues();
    }

    @Test
    void debeNotificarACadaEstudianteYAlAsesorConSuTipo_cuandoLlegaLaFichaAprobada() throws Exception {
        // Act
        adapter.onFichaPerfilAprobada(mensaje("APROBADA", 1L), channel);

        // Assert
        var comandos = comandosEmitidos(3);
        assertThat(comandos).extracting(EnviarNotificacionCommand::destinatarioEmail).containsExactly(
                "ana.gomez@soyuco.edu.co", "luis.diaz@soyuco.edu.co", "carlos.ruiz@soyuco.edu.co");
        assertThat(comandos).extracting(EnviarNotificacionCommand::tipo).containsExactly(
                TipoNotificacion.FICHA_PERFIL_APROBADA_ESTUDIANTE,
                TipoNotificacion.FICHA_PERFIL_APROBADA_ESTUDIANTE,
                TipoNotificacion.FICHA_PERFIL_APROBADA_ASESOR);
        assertThat(comandos).extracting(EnviarNotificacionCommand::idEvento).containsOnly("evt-1");
        assertThat(comandos).allSatisfy(comando -> assertThat(comando.asunto()).contains("Sistema de gestión"));
        assertThat(comandos.getFirst().cuerpo()).contains("Ana Gomez", "Sistema de gestión");
        assertThat(comandos.getLast().cuerpo()).contains("Carlos Ruiz", "Sistema de gestión");
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeDecirConObservacionesEnElCuerpo_cuandoElEstadoEsAprobadaConObservaciones() throws Exception {
        // Act
        adapter.onFichaPerfilAprobada(mensaje("APROBADA_CON_OBSERVACIONES", 1L), channel);

        // Assert
        var conObservaciones = Mensajes.obtener(PlantillaKey.TEXTO_ESTADO_APROBADA_CON_OBSERVACIONES);
        assertThat(comandosEmitidos(3)).allSatisfy(comando ->
                assertThat(comando.cuerpo()).contains(conObservaciones));
    }

    @Test
    void debeUsarElTextoDeAprobadaSinObservaciones_cuandoElEstadoEsAprobada() throws Exception {
        // Act
        adapter.onFichaPerfilAprobada(mensaje("APROBADA", 1L), channel);

        // Assert
        var aprobada = Mensajes.obtener(PlantillaKey.TEXTO_ESTADO_APROBADA);
        var conObservaciones = Mensajes.obtener(PlantillaKey.TEXTO_ESTADO_APROBADA_CON_OBSERVACIONES);
        assertThat(comandosEmitidos(3)).allSatisfy(comando -> assertThat(comando.cuerpo())
                .contains(aprobada)
                .doesNotContain(conObservaciones));
    }

    @Test
    void debeEnviarALaDlqSinNotificar_cuandoElMensajeNoEsJsonValido() throws Exception {
        // Arrange
        var props = new MessageProperties();
        props.setDeliveryTag(9L);
        var envenenado = MessageBuilder.withBody("no-es-json".getBytes()).andProperties(props).build();

        // Act
        adapter.onFichaPerfilAprobada(envenenado, channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicNack(9L, false, false);
    }
}
