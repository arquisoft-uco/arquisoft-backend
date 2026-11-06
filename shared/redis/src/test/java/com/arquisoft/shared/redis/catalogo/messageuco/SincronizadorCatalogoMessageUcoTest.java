package com.arquisoft.shared.redis.catalogo.messageuco;

import co.edu.uco.messageucolab.client.api.MessageReader;
import co.edu.uco.messageucolab.client.api.model.MessageCollection;
import co.edu.uco.messageucolab.client.api.model.MessageDetails;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.AridadClave;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.ClavesCatalogo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SincronizadorCatalogoMessageUcoTest {

    private static final String TEXTO_NUEVO = "Texto editado en MessageUcoLab";

    @Mock
    private MessageReader lector;

    @Mock
    private StringRedisTemplate plantilla;

    @Mock
    private ValueOperations<String, String> operaciones;

    @Mock
    private AppLogger logger;

    private SincronizadorCatalogoMessageUco sincronizador;
    private Map<String, String> redis;

    @BeforeEach
    void preparar() {
        sincronizador = new SincronizadorCatalogoMessageUco(lector, plantilla, logger);
        redis = textosCoherentes();
        when(plantilla.opsForValue()).thenReturn(operaciones);
        when(operaciones.multiGet(anyCollection())).thenAnswer(invocacion -> {
            Collection<String> claves = invocacion.getArgument(0);
            List<String> valores = new ArrayList<>();
            claves.forEach(clave -> valores.add(redis.get(clave)));
            return valores;
        });
    }

    @Test
    @DisplayName("no escribe en Redis cuando los textos son iguales")
    void debeNoEscribir_cuandoNoHayCambios() {
        // Arrange
        when(lector.getAll()).thenReturn(mensajes(redis));

        // Act
        ResultadoSincronizacion resultado = sincronizador.sincronizar();

        // Assert
        assertThat(resultado.actualizadas()).isZero();
        verify(operaciones, never()).multiSet(anyMap());
    }

    @Test
    @DisplayName("escribe solo la clave cuyo texto cambió")
    void debeEscribirSoloLoCambiado_cuandoUnTextoCambia() {
        // Arrange
        ClaveMensaje clave = claveSinParametros();
        Map<String, String> recibidos = new HashMap<>(redis);
        recibidos.put(clave.clave(), TEXTO_NUEVO);
        when(lector.getAll()).thenReturn(mensajes(recibidos));

        // Act
        ResultadoSincronizacion resultado = sincronizador.sincronizar();

        // Assert
        assertThat(resultado.actualizadas()).isEqualTo(1);
        assertThat(escrito()).containsExactly(Map.entry(clave.clave(), TEXTO_NUEVO));
    }

    @Test
    @DisplayName("carga el catálogo completo cuando Redis está vacío")
    void debeCargarTodo_cuandoRedisEstaVacio() {
        // Arrange
        when(lector.getAll()).thenReturn(mensajes(redis));
        redis = Map.of();

        // Act
        ResultadoSincronizacion resultado = sincronizador.sincronizar();

        // Assert
        assertThat(resultado.actualizadas()).isEqualTo(ClavesCatalogo.TODAS.size());
    }

    @Test
    @DisplayName("rechaza un texto cuya aridad no coincide y conserva el de Redis")
    void debeRechazar_cuandoLaAridadNoCoincide() {
        // Arrange
        ClaveMensaje clave = claveSinParametros();
        Map<String, String> recibidos = new HashMap<>(redis);
        recibidos.put(clave.clave(), marcadoresDeMas(clave));
        when(lector.getAll()).thenReturn(mensajes(recibidos));

        // Act
        ResultadoSincronizacion resultado = sincronizador.sincronizar();

        // Assert
        assertThat(resultado.rechazadas()).containsExactly(clave.clave());
        verify(operaciones, never()).multiSet(anyMap());
    }

    @Test
    @DisplayName("rechaza un texto vacío")
    void debeRechazar_cuandoElTextoEstaVacio() {
        // Arrange
        ClaveMensaje clave = claveSinParametros();
        Map<String, String> recibidos = new HashMap<>(redis);
        recibidos.put(clave.clave(), " ");
        when(lector.getAll()).thenReturn(mensajes(recibidos));

        // Act
        ResultadoSincronizacion resultado = sincronizador.sincronizar();

        // Assert
        assertThat(resultado.rechazadas()).containsExactly(clave.clave());
    }

    @Test
    @DisplayName("conserva la clave en Redis cuando MessageUcoLab no la trae")
    void debeConservarLaClave_cuandoMessageUcoNoLaTrae() {
        // Arrange
        ClaveMensaje clave = claveSinParametros();
        Map<String, String> recibidos = new HashMap<>(redis);
        recibidos.remove(clave.clave());
        when(lector.getAll()).thenReturn(mensajes(recibidos));

        // Act
        ResultadoSincronizacion resultado = sincronizador.sincronizar();

        // Assert
        assertThat(resultado.ausentes()).containsExactly(clave.clave());
        verify(plantilla, never()).delete(any(String.class));
    }

    @Test
    @DisplayName("ignora un título que no es clave del catálogo")
    void debeIgnorar_cuandoElTituloNoEsClaveDeclarada() {
        // Arrange
        Map<String, String> recibidos = new HashMap<>(redis);
        recibidos.put("rate-limit:bucket", TEXTO_NUEVO);
        when(lector.getAll()).thenReturn(mensajes(recibidos));

        // Act
        ResultadoSincronizacion resultado = sincronizador.sincronizar();

        // Assert
        assertThat(resultado.ignorados()).isEqualTo(1);
        verify(operaciones, never()).multiSet(anyMap());
    }

    @Test
    @DisplayName("no propaga el fallo de MessageUcoLab al arrancar")
    void debeNoPropagarElFallo_cuandoMessageUcoCaeAlArrancar() {
        // Arrange
        when(lector.getAll()).thenReturn(Mono.error(new IllegalStateException("sin conexión")));

        // Act + Assert
        assertThatCode(sincronizador::sincronizarAlArrancar).doesNotThrowAnyException();
        verify(operaciones, never()).multiSet(anyMap());
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> escrito() {
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
        verify(operaciones).multiSet(captor.capture());
        return captor.getValue();
    }

    private Mono<MessageCollection> mensajes(Map<String, String> textos) {
        List<MessageDetails> detalles = textos.entrySet().stream()
                .map(entrada -> new MessageDetails(null, null, null, entrada.getKey(), entrada.getValue(),
                        null, null, null, null, null))
                .toList();
        return Mono.just(new MessageCollection("es", detalles.size(), detalles));
    }

    private ClaveMensaje claveSinParametros() {
        return ClavesCatalogo.TODAS.stream().filter(clave -> clave.parametros() == 0).findFirst().orElseThrow();
    }

    private String marcadoresDeMas(ClaveMensaje clave) {
        return AridadClave.marcadores(clave, "{}") == 1 ? "Con marcador {}" : "Con marcador %s";
    }

    private Map<String, String> textosCoherentes() {
        Map<String, String> textos = new HashMap<>();
        for (ClaveMensaje clave : ClavesCatalogo.TODAS) {
            String marcador = AridadClave.marcadores(clave, "{}") == 1 ? " {}" : " %s";
            textos.put(clave.clave(), "Un texto cualquiera" + marcador.repeat(clave.parametros()));
        }
        return textos;
    }
}
