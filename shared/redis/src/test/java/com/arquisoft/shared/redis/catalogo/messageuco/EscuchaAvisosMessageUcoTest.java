package com.arquisoft.shared.redis.catalogo.messageuco;

import co.edu.uco.messageucolab.client.api.exception.MessageClientResponseException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.redis.catalogo.exception.AvisoMessageUcoInvalidoException;
import com.arquisoft.shared.redis.catalogo.messageuco.ProcesadorAvisosMessageUco.AvisoMessageUco;
import com.arquisoft.shared.tracing.application.traza.primaryport.AlcanceTraza;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Cuándo se confirma un aviso a Pulsar y cuándo se pide que lo vuelva a entregar.
 *
 * <p>Confirmar uno que falló por MessageUcoLab dejaría el texto viejo hasta el próximo arranque;
 * no confirmar uno que nunca va a funcionar lo haría volver para siempre.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EscuchaAvisosMessageUcoTest {

    private static final String CARGA_A = "{\"eventType\":\"MessageUpdated\",\"messageCode\":\"MSG_A\"}";
    private static final String CARGA_B = "{\"eventType\":\"MessageUpdated\",\"messageCode\":\"MSG_B\"}";
    private static final String CLIENT_ID = "svc-prueba-arquisoft-dev";

    @Mock
    private ProcesadorAvisosMessageUco procesador;

    @Mock
    private SincronizadorCatalogoMessageUco sincronizador;

    @Mock
    private GestorTraza gestorTraza;

    @Mock
    private AlcanceTraza alcance;

    @Mock
    private AppLogger logger;

    @Mock
    private Consumer<String> consumidor;

    private EscuchaAvisosMessageUco escucha;

    @BeforeEach
    void preparar() {
        var eventos = new MessageUcoProperties.Eventos("pulsar://localhost:6800",
                "persistent://public/default/message-events", "arquisoft-catalogo",
                Duration.ofSeconds(30), Duration.ofMinutes(1), Duration.ofSeconds(2), 20);
        escucha = new EscuchaAvisosMessageUco(procesador, sincronizador, eventos, CLIENT_ID, gestorTraza, logger);
        when(gestorTraza.abrir(any())).thenReturn(alcance);
        when(procesador.leer(CARGA_A)).thenReturn(new AvisoMessageUco("MessageUpdated", "MSG_A", "ev-a"));
        when(procesador.leer(CARGA_B)).thenReturn(new AvisoMessageUco("MessageUpdated", "MSG_B", "ev-b"));
    }

    @Test
    @DisplayName("el topic es el prefijo más el client-id de la identidad")
    void debeArmarElTopicConElClientId() {
        assertThat(escucha.topic()).isEqualTo("persistent://public/default/message-events-" + CLIENT_ID);
    }

    @Test
    @DisplayName("al recibir solo encola: el lote se aplica cuando vence la ventana")
    void debeEncolarSinAplicar_cuandoRecibe() {
        // Act
        escucha.recibir(consumidor, aviso(CARGA_A));

        // Assert
        verifyNoInteractions(procesador);
    }

    @Test
    @DisplayName("aplica juntos los avisos de la ventana y los confirma todos")
    void debeAplicarElLoteYConfirmar_cuandoSeAplica() {
        // Arrange
        Message<String> primero = aviso(CARGA_A);
        Message<String> segundo = aviso(CARGA_B);
        escucha.recibir(consumidor, primero);
        escucha.recibir(consumidor, segundo);

        // Act
        escucha.aplicarLote();

        // Assert
        verify(procesador).aplicar(List.of(
                new AvisoMessageUco("MessageUpdated", "MSG_A", "ev-a"),
                new AvisoMessageUco("MessageUpdated", "MSG_B", "ev-b")));
        verify(consumidor).acknowledgeAsync(primero);
        verify(consumidor).acknowledgeAsync(segundo);
        verify(consumidor, never()).negativeAcknowledge(any(Message.class));
    }

    @Test
    @DisplayName("pide otra entrega de todo el lote cuando MessageUcoLab no responde")
    void debePedirOtraEntrega_cuandoElFalloEsTransitorio() {
        // Arrange
        Message<String> primero = aviso(CARGA_A);
        escucha.recibir(consumidor, primero);
        doThrow(new MessageClientResponseException(429, null)).when(procesador).aplicar(anyList());

        // Act
        escucha.aplicarLote();

        // Assert
        verify(consumidor).negativeAcknowledge(primero);
        verify(consumidor, never()).acknowledgeAsync(primero);
    }

    @Test
    @DisplayName("descarta (confirma) el aviso ilegible y aplica el resto del lote")
    void debeDescartarElIlegible_yAplicarElResto() {
        // Arrange
        Message<String> ilegible = aviso("no es json");
        Message<String> bueno = aviso(CARGA_B);
        when(procesador.leer("no es json")).thenThrow(new AvisoMessageUcoInvalidoException("inválido", null));
        escucha.recibir(consumidor, ilegible);
        escucha.recibir(consumidor, bueno);

        // Act
        escucha.aplicarLote();

        // Assert
        verify(consumidor).acknowledgeAsync(ilegible);
        verify(procesador).aplicar(List.of(new AvisoMessageUco("MessageUpdated", "MSG_B", "ev-b")));
        verify(consumidor).acknowledgeAsync(bueno);
    }

    @Test
    @DisplayName("un lote ya aplicado no se vuelve a aplicar")
    void debeVaciarLaCola_cuandoAplicaElLote() {
        // Arrange
        escucha.recibir(consumidor, aviso(CARGA_A));
        escucha.aplicarLote();

        // Act
        escucha.aplicarLote();

        // Assert
        verify(procesador).aplicar(anyList());
    }

    @SuppressWarnings("unchecked")
    private static Message<String> aviso(String carga) {
        Message<String> mensaje = mock(Message.class);
        when(mensaje.getValue()).thenReturn(carga);
        return mensaje;
    }
}
