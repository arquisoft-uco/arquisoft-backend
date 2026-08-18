package com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc;

import com.arquisoft.shared.tracing.domain.traza.TrazaDomain;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;

class MdcContextoDiagnosticoOutputAdapterTest {

    private final MdcContextoDiagnosticoOutputAdapter adaptador = new MdcContextoDiagnosticoOutputAdapter();

    @AfterEach
    void limpiar() {
        MDC.clear();
    }

    @Test
    void debeEscribirLasClavesDeLaTraza_cuandoSeEscribeUnaTraza() {
        // Arrange
        var traza = TrazaDomain.crear(
                SolicitudTraza.paraHttp("abc-123", null, "203.0.113.25", "GET", "/api/fichas"), false);

        // Act
        adaptador.escribirTraza(traza);

        // Assert
        assertThat(MDC.get(TrazaKeys.CORRELACION_ID)).isEqualTo("abc-123");
        assertThat(MDC.get(TrazaKeys.TRANSACCION_ID)).isEqualTo(traza.getTransaccionId());
        assertThat(MDC.get(TrazaKeys.ORIGEN)).isEqualTo("HTTP");
        assertThat(MDC.get(TrazaKeys.CLIENTE_IP)).isEqualTo("203.0.113.25");
        assertThat(MDC.get(TrazaKeys.METODO_HTTP)).isEqualTo("GET");
        assertThat(MDC.get(TrazaKeys.RUTA_URI)).isEqualTo("/api/fichas");
        assertThat(MDC.get(TrazaKeys.TIEMPO_ENTRADA)).isNotBlank();
    }

    @Test
    void debeOmitirLasClavesVacias_cuandoLaTrazaNoEsHttp() {
        // Arrange
        var traza = TrazaDomain.crear(SolicitudTraza.paraEvento("abc-123"), false);

        // Act
        adaptador.escribirTraza(traza);

        // Assert
        assertThat(MDC.get(TrazaKeys.METODO_HTTP)).isNull();
        assertThat(MDC.get(TrazaKeys.RUTA_URI)).isNull();
        assertThat(MDC.getCopyOfContextMap()).doesNotContainKey(TrazaKeys.METODO_HTTP);
    }

    @Test
    void debeEscribirLosCamposDeSalida_cuandoSeCierraLaTraza() {
        // Arrange
        var traza = TrazaDomain.crear(SolicitudTraza.paraHttp(null, null, "203.0.113.25", "GET", "/api"), false);

        // Act
        adaptador.escribirSalida(traza.registrarSalida(500));

        // Assert
        assertThat(MDC.get(TrazaKeys.CODIGO_ESTADO)).isEqualTo("500");
        assertThat(MDC.get(TrazaKeys.DURACION_MS)).isNotBlank();
        assertThat(MDC.get(TrazaKeys.TIEMPO_SALIDA)).isNotBlank();
    }

    @Test
    void debeVaciarElContexto_cuandoSeRestauraSinContextoPrevio() {
        // Arrange
        MDC.put(TrazaKeys.CORRELACION_ID, "abc-123");

        // Act
        adaptador.restaurar(null);

        // Assert
        assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
    }

    @Test
    void debeDevolverElValorPrevio_cuandoSeRestauraTrasUnAlcanceAnidado() {
        // Arrange
        MDC.put(TrazaKeys.CORRELACION_ID, "externa");
        var previo = adaptador.capturar();

        MDC.put(TrazaKeys.CORRELACION_ID, "interna");
        // Act
        adaptador.restaurar(previo);

        // Assert
        assertThat(adaptador.leerCorrelacion()).isEqualTo("externa");
    }

    @Test
    void debeLeerLasClavesDeTrazabilidad_cuandoEstanEnElContexto() {
        // Arrange
        MDC.put(TrazaKeys.CORRELACION_ID, "abc-123");
        MDC.put(TrazaKeys.TRANSACCION_ID, "00f067aa0ba902b7");
        // Act
        MDC.put(TrazaKeys.USUARIO_ID, "usuario-1");

        // Assert
        assertThat(adaptador.leerCorrelacion()).isEqualTo("abc-123");
        assertThat(adaptador.leerTransaccion()).isEqualTo("00f067aa0ba902b7");
        assertThat(adaptador.leerUsuario()).isEqualTo("usuario-1");
    }
}
