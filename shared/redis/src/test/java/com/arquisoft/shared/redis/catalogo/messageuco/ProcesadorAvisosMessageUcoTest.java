package com.arquisoft.shared.redis.catalogo.messageuco;

import co.edu.uco.messageucolab.client.api.exception.MessageClientResponseException;
import co.edu.uco.messageucolab.client.api.exception.MessageClientTransportException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.redis.catalogo.exception.AvisoMessageUcoInvalidoException;
import com.arquisoft.shared.redis.catalogo.messageuco.ProcesadorAvisosMessageUco.AvisoMessageUco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Qué hace el catálogo con cada lote de avisos, y qué fallos merecen otra entrega.
 */
@ExtendWith(MockitoExtension.class)
class ProcesadorAvisosMessageUcoTest {

    private static final String CODIGO = "MSG_0001";
    private static final int UMBRAL = 3;
    private static final String FORMATO_AVISO =
            "{\"eventType\":\"%s\",\"messageCode\":\"%s\",\"environmentId\":\"amb\",\"eventId\":\"ev-1\"}";

    @Mock
    private SincronizadorCatalogoMessageUco sincronizador;

    @Mock
    private AppLogger logger;

    private ProcesadorAvisosMessageUco procesador() {
        return new ProcesadorAvisosMessageUco(sincronizador, JsonMapper.shared(), UMBRAL, logger);
    }

    @Test
    @DisplayName("lee el tipo, el código y el id del aviso")
    void debeLeerElAviso() {
        // Act
        AvisoMessageUco aviso = procesador().leer(FORMATO_AVISO.formatted(ProcesadorAvisosMessageUco.MODIFICADO, CODIGO));

        // Assert
        assertThat(aviso).isEqualTo(new AvisoMessageUco(ProcesadorAvisosMessageUco.MODIFICADO, CODIGO, "ev-1"));
    }

    @Test
    @DisplayName("JSON inválido: lanza un error que no se reintenta")
    void debeLanzarNoTransitorio_cuandoElJsonEsInvalido() {
        // Act + Assert
        assertThatThrownBy(() -> procesador().leer("no es json"))
                .isInstanceOf(AvisoMessageUcoInvalidoException.class)
                .satisfies(error -> assertThat(ProcesadorAvisosMessageUco.esTransitorio(error)).isFalse());
    }

    @ParameterizedTest
    @ValueSource(strings = {ProcesadorAvisosMessageUco.CREADO, ProcesadorAvisosMessageUco.MODIFICADO})
    @DisplayName("creación o modificación: actualiza solo ese mensaje")
    void debeActualizarElMensaje_cuandoSeCreaOModifica(String tipo) {
        // Act
        procesador().aplicar(List.of(aviso(tipo, CODIGO)));

        // Assert
        verify(sincronizador).actualizar(CODIGO);
        verify(sincronizador, never()).sincronizar();
    }

    @Test
    @DisplayName("el mismo código repetido en un lote se consulta una sola vez")
    void debeConsultarUnaVez_cuandoElCodigoSeRepite() {
        // Act
        procesador().aplicar(List.of(
                aviso(ProcesadorAvisosMessageUco.CREADO, CODIGO), aviso(ProcesadorAvisosMessageUco.MODIFICADO, CODIGO)));

        // Assert
        verify(sincronizador, times(1)).actualizar(CODIGO);
    }

    @Test
    @DisplayName("lote con más mensajes que el umbral: una sola recarga completa, ninguna consulta por mensaje")
    void debeRecargarTodo_cuandoElLoteSuperaElUmbral() {
        // Arrange
        List<AvisoMessageUco> avisos = new ArrayList<>();
        for (int i = 0; i <= UMBRAL; i++) {
            avisos.add(aviso(ProcesadorAvisosMessageUco.MODIFICADO, "MSG_" + i));
        }

        // Act
        procesador().aplicar(avisos);

        // Assert
        verify(sincronizador).sincronizar();
        verify(sincronizador, never()).actualizar(any());
    }

    @Test
    @DisplayName("cambio masivo: sincroniza el catálogo completo")
    void debeSincronizarTodo_cuandoHayCambioMasivo() {
        // Act
        procesador().aplicar(List.of(aviso(ProcesadorAvisosMessageUco.CAMBIO_MASIVO, ""),
                aviso(ProcesadorAvisosMessageUco.MODIFICADO, CODIGO)));

        // Assert
        verify(sincronizador).sincronizar();
        verify(sincronizador, never()).actualizar(any());
    }

    @Test
    @DisplayName("eliminación, tipo desconocido o modificación sin código: no toca Redis")
    void debeNoTocarRedis_cuandoElAvisoNoSePuedeAplicar() {
        // Act
        procesador().aplicar(List.of(aviso(ProcesadorAvisosMessageUco.ELIMINADO, CODIGO),
                aviso("OtroTipo", CODIGO), aviso(ProcesadorAvisosMessageUco.MODIFICADO, "")));

        // Assert
        verifyNoInteractions(sincronizador);
    }

    @Test
    @DisplayName("un 404 descarta solo ese mensaje y sigue con el resto del lote")
    void debeSeguir_cuandoUnMensajeYaNoExiste() {
        // Arrange
        doThrow(new MessageClientResponseException(404, null)).when(sincronizador).actualizar("MSG_A");

        // Act
        procesador().aplicar(List.of(aviso(ProcesadorAvisosMessageUco.MODIFICADO, "MSG_A"),
                aviso(ProcesadorAvisosMessageUco.MODIFICADO, "MSG_B")));

        // Assert
        verify(sincronizador).actualizar("MSG_B");
    }

    @Test
    @DisplayName("un fallo transitorio se propaga para que se vuelva a entregar el lote")
    void debePropagar_cuandoElFalloEsTransitorio() {
        // Arrange
        doThrow(new MessageClientResponseException(429, null)).when(sincronizador).actualizar(CODIGO);

        // Act + Assert
        assertThatThrownBy(() -> procesador().aplicar(List.of(aviso(ProcesadorAvisosMessageUco.MODIFICADO, CODIGO))))
                .isInstanceOf(MessageClientResponseException.class);
    }

    @Test
    @DisplayName("reintenta caídas, 5xx, 401 y 429 de MessageUcoLab; descarta un 404")
    void debeClasificarLosErrores() {
        assertThat(ProcesadorAvisosMessageUco.esTransitorio(mock(MessageClientTransportException.class))).isTrue();
        assertThat(ProcesadorAvisosMessageUco.esTransitorio(respuesta(503))).isTrue();
        assertThat(ProcesadorAvisosMessageUco.esTransitorio(respuesta(401))).isTrue();
        assertThat(ProcesadorAvisosMessageUco.esTransitorio(respuesta(429))).isTrue();
        assertThat(ProcesadorAvisosMessageUco.esTransitorio(respuesta(404))).isFalse();
        assertThat(ProcesadorAvisosMessageUco.esTransitorio(new RuntimeException(respuesta(404)))).isFalse();
    }

    private static AvisoMessageUco aviso(String tipo, String codigo) {
        return new AvisoMessageUco(tipo, codigo, "ev-1");
    }

    private static MessageClientResponseException respuesta(int estado) {
        return new MessageClientResponseException(estado, null);
    }
}
