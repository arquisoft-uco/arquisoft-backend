package com.arquisoft.shared.redis.catalogo.config;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.CategoriaMensaje;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.ClavesCatalogo;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.prueba.CatalogoMensajesPrueba;
import com.arquisoft.shared.redis.catalogo.CatalogoMensajesRedis;
import com.arquisoft.shared.redis.catalogo.exception.CatalogoMensajesIncompletoException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

/**
 * El fail-fast del arranque.
 *
 * <p>Es la única compuerta que separa un catálogo mal cargado de un despliegue que responde con
 * textos rotos. Que aborte no basta: tiene que decir <em>qué</em> falta, porque quien lo lee está
 * mirando un despliegue caído y necesita saber qué archivo recargar.
 */
@ExtendWith(MockitoExtension.class)
class CatalogoMensajesRedisConfigTest {

    private static final String TEXTO_SIN_PARAMETROS = "Un texto cualquiera";

    @Mock
    private StringRedisTemplate plantilla;

    @Mock
    private ValueOperations<String, String> operaciones;

    @Mock
    private AppLogger logger;

    private final CatalogoMensajesRedisConfig config = new CatalogoMensajesRedisConfig();

    @AfterEach
    void restaurarCatalogoDePrueba() {
        // El arranque instala su catálogo en la fachada estática, que es proceso-global. Sin esto,
        // el resto de la suite resolvería contra un mock de Redis.
        Mensajes.instalar(CatalogoMensajesPrueba.porDefecto());
    }

    @Test
    @DisplayName("carga todas las claves cuando Redis responde completo")
    void debeCargarTodasLasClaves_cuandoRedisRespondeCompleto() {
        // Arrange
        redisDevuelve(textosCoherentes());

        // Act
        CatalogoMensajesRedis catalogo = config.catalogoMensajesRedis(plantilla, logger);

        // Assert
        assertThat(catalogo.clavesEnCache()).isEqualTo(ClavesCatalogo.TODAS.size());
    }

    @Test
    @DisplayName("instala el catálogo en la fachada cuando la carga es correcta")
    void debeInstalarElCatalogo_cuandoLaCargaEsCorrecta() {
        // Arrange
        redisDevuelve(textosCoherentes());

        // Act
        CatalogoMensajesRedis catalogo = config.catalogoMensajesRedis(plantilla, logger);

        // Assert
        assertThat(Mensajes.catalogo()).isSameAs(catalogo);
    }

    @Test
    @DisplayName("aborta el arranque cuando falta una clave")
    void debeAbortarElArranque_cuandoFaltaUnaClave() {
        // Arrange
        List<String> textos = textosCoherentes();
        textos.set(0, null);
        redisDevuelve(textos);

        // Act & Assert
        assertThatThrownBy(() -> config.catalogoMensajesRedis(plantilla, logger))
                .isInstanceOf(CatalogoMensajesIncompletoException.class);
    }

    @Test
    @DisplayName("nombra las claves faltantes al abortar")
    void debeNombrarLasClavesFaltantes_cuandoAborta() {
        // Arrange
        ClaveMensaje ausente = ClavesCatalogo.TODAS.get(0);
        List<String> textos = textosCoherentes();
        textos.set(0, null);
        redisDevuelve(textos);

        // Act & Assert
        assertThatThrownBy(() -> config.catalogoMensajesRedis(plantilla, logger))
                .as("un fallo mudo obligaría a comparar a mano el catálogo contra Redis")
                .hasMessageContaining(ausente.clave());
    }

    @Test
    @DisplayName("no instala el catálogo en la fachada cuando aborta")
    void debeNoInstalarElCatalogo_cuandoAborta() {
        // Arrange
        List<String> textos = textosCoherentes();
        textos.set(0, null);
        redisDevuelve(textos);

        // Act
        assertThatThrownBy(() -> config.catalogoMensajesRedis(plantilla, logger));

        // Assert
        assertThat(Mensajes.catalogo())
                .as("un catálogo a medias instalado sería peor que ninguno")
                .isNotInstanceOf(CatalogoMensajesRedis.class);
    }

    @Test
    @DisplayName("aborta el arranque cuando no hay conexión")
    void debeAbortarElArranque_cuandoNoHayConexion() {
        // Arrange
        when(plantilla.opsForValue()).thenReturn(operaciones);
        when(operaciones.multiGet(anyCollection())).thenThrow(new QueryTimeoutException("Redis caído"));

        // Act & Assert
        assertThatThrownBy(() -> config.catalogoMensajesRedis(plantilla, logger))
                .isInstanceOf(CatalogoMensajesIncompletoException.class)
                .hasCauseInstanceOf(QueryTimeoutException.class);
    }

    @Test
    @DisplayName("aborta el arranque cuando un patrón no tiene la aridad declarada")
    void debeAbortarElArranque_cuandoElPatronNoTieneLaAridadDeclarada() {
        // Arrange
        ClaveMensaje conParametro = primeraClaveConParametros();
        List<String> textos = textosCoherentes();
        textos.set(ClavesCatalogo.TODAS.indexOf(conParametro), TEXTO_SIN_PARAMETROS);
        redisDevuelve(textos);

        // Act & Assert
        assertThatThrownBy(() -> config.catalogoMensajesRedis(plantilla, logger))
                .as("el texto existe, así que el fail-fast de claves faltantes no lo vería; "
                        + "String.formatted lanzaría en el primer uso, construyendo la respuesta "
                        + "de un error ya ocurrido")
                .isInstanceOf(CatalogoMensajesIncompletoException.class)
                .hasMessageContaining(conParametro.clave());
    }

    // -------------------------------------------------------------------------

    private void redisDevuelve(List<String> textos) {
        when(plantilla.opsForValue()).thenReturn(operaciones);
        when(operaciones.multiGet(anyCollection())).thenReturn(textos);
    }

    /**
     * Un texto por clave declarada, con tantos marcadores como su clave dice llevar.
     *
     * <p>El marcador depende de la categoría: los patrones de log los sustituye SLF4J con {@code {}}
     * y el resto los sustituye el catálogo con {@code %s}. Rellenar todos con %s daría por incoherente
     * a la mitad del catálogo.
     */
    private static List<String> textosCoherentes() {
        List<String> textos = new ArrayList<>();

        for (ClaveMensaje clave : ClavesCatalogo.TODAS) {
            String marcador = CategoriaMensaje.LOG == CategoriaMensaje.desde(clave.clave()) ? " {}" : " %s";
            textos.add(TEXTO_SIN_PARAMETROS + marcador.repeat(clave.parametros()));
        }

        return textos;
    }

    private static ClaveMensaje primeraClaveConParametros() {
        return ClavesCatalogo.TODAS.stream()
                .filter(clave -> clave.parametros() > 0)
                .findFirst()
                .orElseThrow();
    }
}
