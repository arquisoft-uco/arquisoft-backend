package com.arquisoft.shared.redis.catalogo;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.respaldo.CatalogoMensajesRespaldo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogoMensajesRedisTest {

    // El adaptador no consulta la aridad declarada: aplica String.formatted sobre lo que Redis
    // devuelva. Por eso el patrón se fija aquí y la clave solo aporta su categoría, que es lo que
    // decide el texto de respaldo.
    private static final ClaveMensaje CLAVE_ERROR = FichaPerfilKey.ERROR_NO_ENCONTRADA;
    private static final ClaveMensaje CLAVE_LOG = FichaPerfilKey.LOG_REGISTRADA;

    private static final String PATRON_CON_PARAMETRO = "El título ya existe: %s";
    private static final String PATRON_DE_LOG = "Ficha de perfil registrada — id={}";
    private static final String TEXTO_PLANO = "La ficha de perfil no existe";

    @Mock
    private StringRedisTemplate plantilla;

    @Mock
    private ValueOperations<String, String> operaciones;

    @Mock
    private AppLogger logger;

    private final CatalogoMensajes respaldo = CatalogoMensajesRespaldo.porDefecto();

    private CatalogoMensajesRedis catalogo;

    @BeforeEach
    void prepararCatalogo() {
        catalogo = new CatalogoMensajesRedis(plantilla, respaldo, logger);
    }

    @Test
    @DisplayName("devuelve el texto de Redis cuando la clave existe")
    void debeDevolverElTextoDeRedis_cuandoLaClaveExiste() {
        // Arrange
        redisDevuelve(CLAVE_ERROR, TEXTO_PLANO);

        // Act
        String texto = catalogo.obtener(CLAVE_ERROR);

        // Assert
        assertThat(texto).isEqualTo(TEXTO_PLANO);
    }

    @Test
    @DisplayName("puebla la caché cuando Redis resuelve la clave")
    void debePoblarLaCache_cuandoRedisResuelveLaClave() {
        // Arrange
        redisDevuelve(CLAVE_ERROR, TEXTO_PLANO);

        // Act
        catalogo.obtener(CLAVE_ERROR);

        // Assert
        assertThat(catalogo.clavesEnCache()).isEqualTo(1);
    }

    @Test
    @DisplayName("sirve desde la caché cuando Redis falla después de haber respondido")
    void debeDevolverElTextoDeLaCache_cuandoRedisFalla() {
        // Arrange
        when(plantilla.opsForValue()).thenReturn(operaciones);
        when(operaciones.get(CLAVE_ERROR.clave()))
                .thenReturn(TEXTO_PLANO)
                .thenThrow(new QueryTimeoutException("Redis caído"));
        catalogo.obtener(CLAVE_ERROR);

        // Act
        String texto = catalogo.obtener(CLAVE_ERROR);

        // Assert
        assertThat(texto).isEqualTo(TEXTO_PLANO);
    }

    @Test
    @DisplayName("devuelve el respaldo cuando Redis falla y la caché está vacía")
    void debeDevolverElRespaldo_cuandoRedisFallaYLaCacheEstaVacia() {
        // Arrange
        redisLanza();

        // Act
        String texto = catalogo.obtener(CLAVE_ERROR);

        // Assert
        assertThat(texto).isEqualTo(respaldo.obtener(CLAVE_ERROR));
    }

    @Test
    @DisplayName("devuelve el respaldo cuando la clave no existe en Redis")
    void debeDevolverElRespaldo_cuandoLaClaveNoExisteEnRedis() {
        // Arrange
        redisDevuelve(CLAVE_ERROR, null);

        // Act
        String texto = catalogo.obtener(CLAVE_ERROR);

        // Assert
        assertThat(texto).isEqualTo(respaldo.obtener(CLAVE_ERROR));
    }

    @Test
    @DisplayName("no propaga la excepción cuando Redis lanza")
    void debeNoPropagarExcepcion_cuandoRedisLanza() {
        // Arrange
        redisLanza();

        // Act & Assert
        assertThat(catalogo.obtener(CLAVE_ERROR)).isNotNull();
        assertThat(catalogo.formatear(CLAVE_ERROR, "Mi proyecto")).isNotNull();
        assertThat(catalogo.contiene(CLAVE_ERROR)).isFalse();
    }

    @Test
    @DisplayName("marca el estado degradado cuando Redis lanza")
    void debeMarcarDegradado_cuandoRedisLanza() {
        // Arrange
        redisLanza();

        // Act
        catalogo.obtener(CLAVE_ERROR);

        // Assert
        assertThat(catalogo.estaDegradado()).isTrue();
    }

    @Test
    @DisplayName("no devuelve la clave al degradar al respaldo — la clave es un detalle interno")
    void debeNoDevolverLaClave_cuandoDegradaANivelDeRespaldo() {
        // Arrange
        redisLanza();

        // Act
        String texto = catalogo.obtener(CLAVE_ERROR);

        // Assert
        assertThat(texto)
                .as("un consumidor de la API no debe ver el identificador interno del mensaje")
                .doesNotContain(CLAVE_ERROR.clave());
    }

    @Test
    @DisplayName("devuelve el respaldo cuando faltan argumentos para el patrón")
    void debeDevolverElRespaldo_cuandoFaltanArgumentos() {
        // Arrange
        redisDevuelve(CLAVE_ERROR, PATRON_CON_PARAMETRO);

        // Act
        String texto = catalogo.formatear(CLAVE_ERROR);

        // Assert
        assertThat(texto).isEqualTo(respaldo.formatear(CLAVE_ERROR));
    }

    @Test
    @DisplayName("no expone el patrón crudo cuando la aridad no coincide")
    void debeNoExponerElPatronCrudo_cuandoLaAridadNoCoincide() {
        // Arrange
        redisDevuelve(CLAVE_ERROR, PATRON_CON_PARAMETRO);

        // Act
        String texto = catalogo.formatear(CLAVE_ERROR);

        // Assert
        assertThat(texto)
                .as("los marcadores de formato son sintaxis interna, no texto para el usuario")
                .doesNotContain("%s");
    }

    @Test
    @DisplayName("registra el error cuando la aridad no coincide")
    void debeRegistrarError_cuandoLaAridadNoCoincide() {
        // Arrange
        redisDevuelve(CLAVE_ERROR, PATRON_CON_PARAMETRO);

        // Act
        catalogo.formatear(CLAVE_ERROR);

        // Assert
        verify(logger).error(anyString(), any(Throwable.class), anyString(), anyInt());
    }

    @Test
    @DisplayName("sustituye los parámetros cuando la aridad coincide")
    void debeSustituirParametros_cuandoLaAridadCoincide() {
        // Arrange
        redisDevuelve(CLAVE_ERROR, PATRON_CON_PARAMETRO);

        // Act
        String texto = catalogo.formatear(CLAVE_ERROR, "Mi proyecto");

        // Assert
        assertThat(texto).isEqualTo("El título ya existe: Mi proyecto");
    }

    @Test
    @DisplayName("ignora los argumentos que sobran, como hace String.formatted")
    void debeIgnorarLosArgumentosQueSobran_cuandoElPatronDeclaraMenos() {
        // Arrange
        redisDevuelve(CLAVE_ERROR, PATRON_CON_PARAMETRO);

        // Act
        String texto = catalogo.formatear(CLAVE_ERROR, "Mi proyecto", "de más");

        // Assert
        assertThat(texto)
                .as("String.formatted los descarta en silencio; quien vigila esto es la aridad "
                        + "declarada, comprobada en el build y en el arranque, no el adaptador")
                .isEqualTo("El título ya existe: Mi proyecto");
    }

    @Test
    @DisplayName("contiene devuelve falso cuando el texto vendría del respaldo")
    void debeDevolverFalso_cuandoElTextoVieneDelRespaldo() {
        // Arrange
        redisDevuelve(CLAVE_ERROR, null);

        // Act & Assert
        assertThat(catalogo.contiene(CLAVE_ERROR)).isFalse();
    }

    @Test
    @DisplayName("conserva los marcadores {} de SLF4J en los patrones de log")
    void debeConservarMarcadoresSlf4j_cuandoElPatronEsDeLog() {
        // Arrange
        redisDevuelve(CLAVE_LOG, PATRON_DE_LOG);

        // Act
        String patron = catalogo.obtener(CLAVE_LOG);

        // Assert
        assertThat(patron).contains("{}");
    }

    @Test
    @DisplayName("avisa una sola vez de la degradación, por muchas claves que fallen")
    void debeAvisarUnaSolaVez_cuandoRedisSigueCaido() {
        // Arrange
        redisLanza();

        // Act
        catalogo.obtener(CLAVE_ERROR);
        catalogo.obtener(CLAVE_LOG);
        catalogo.obtener(FichaPerfilKey.ERROR_MISMO_ASESOR);

        // Assert
        verify(logger).warn(anyString(), anyString());
    }

    @Test
    @DisplayName("no consulta Redis al inspeccionar el estado o el tamaño de la caché")
    void debeNoConsultarRedis_cuandoSeInspeccionaElEstado() {
        // Act
        catalogo.estaDegradado();
        catalogo.clavesEnCache();

        // Assert
        verify(plantilla, never()).opsForValue();
    }

    @Test
    @DisplayName("marcarSano limpia el estado degradado")
    void debeLimpiarElEstado_cuandoSeMarcaSano() {
        // Arrange
        redisLanza();
        catalogo.obtener(CLAVE_ERROR);

        // Act
        catalogo.marcarSano();

        // Assert
        assertThat(catalogo.estaDegradado()).isFalse();
    }

    @Test
    @DisplayName("hayConexion es falso cuando Redis lanza, y no propaga")
    void debeDevolverFalso_cuandoNoHayConexion() {
        // Arrange
        when(plantilla.hasKey(anyString())).thenThrow(new QueryTimeoutException("Redis caído"));

        // Act & Assert
        assertThat(catalogo.hayConexion()).isFalse();
    }

    @Test
    @DisplayName("hayConexion es verdadero cuando Redis responde")
    void debeDevolverVerdadero_cuandoHayConexion() {
        // Arrange
        when(plantilla.hasKey(anyString())).thenReturn(true);

        // Act & Assert
        assertThat(catalogo.hayConexion()).isTrue();
    }

    @Test
    @DisplayName("registra un error cuando la clave se queda sin texto")
    void debeRegistrarError_cuandoLaClaveNoTieneTexto() {
        // Arrange
        redisDevuelve(CLAVE_ERROR, null);

        // Act
        catalogo.obtener(CLAVE_ERROR);

        // Assert
        verify(logger, atLeastOnce()).error(anyString(), anyString());
    }

    // -------------------------------------------------------------------------

    private void redisDevuelve(ClaveMensaje clave, String texto) {
        when(plantilla.opsForValue()).thenReturn(operaciones);
        when(operaciones.get(clave.clave())).thenReturn(texto);
    }

    private void redisLanza() {
        when(plantilla.opsForValue()).thenReturn(operaciones);
        when(operaciones.get(anyString())).thenThrow(new QueryTimeoutException("Redis caído"));
    }
}
