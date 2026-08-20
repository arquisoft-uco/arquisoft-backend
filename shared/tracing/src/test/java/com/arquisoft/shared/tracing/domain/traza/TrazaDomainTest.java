package com.arquisoft.shared.tracing.domain.traza;

import com.arquisoft.shared.tracing.domain.traza.model.DetalleOrigenTraza;
import com.arquisoft.shared.tracing.domain.traza.model.OrigenTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import com.arquisoft.shared.tracing.domain.traza.model.TrazaValores;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TrazaDomainTest {

    private static final String TRACE_ID = "4bf92f3577b34da6a3ce929d0e0e4736";

    @Test
    void debeReutilizarLaCorrelacionEntrante_cuandoEsValida() {
        // Arrange
        var solicitud = SolicitudTraza.paraHttp("abc-123", null, "203.0.113.25", "GET", "/api/fichas");

        // Act
        var traza = TrazaDomain.crear(solicitud, false);

        // Assert
        assertThat(traza.getCorrelacionId()).isEqualTo("abc-123");
    }

    @Test
    void debeUsarElTraceIdDelTraceparent_cuandoNoLlegaCorrelacionPropia() {
        // Arrange
        var solicitud = SolicitudTraza.paraHttp(null, "00-" + TRACE_ID + "-00f067aa0ba902b7-01",
                "203.0.113.25", "GET", "/api/fichas");

        // Act
        var traza = TrazaDomain.crear(solicitud, false);

        // Assert
        assertThat(traza.getCorrelacionId()).isEqualTo(TRACE_ID);
    }

    @Test
    void debeGenerarCorrelacion_cuandoNoLlegaNingunaCabecera() {
        // Arrange
        var solicitud = SolicitudTraza.paraHttp(null, null, "203.0.113.25", "GET", "/api/fichas");

        // Act
        var traza = TrazaDomain.crear(solicitud, false);

        // Assert
        assertThat(traza.getCorrelacionId()).matches("[0-9a-f]{32}");
    }

    @Test
    void debeGenerarUnaTransaccionDistintaPorTraza_cuandoLaCorrelacionSeReutiliza() {
        // Arrange
        var solicitud = SolicitudTraza.paraHttp("abc-123", null, "203.0.113.25", "GET", "/api/fichas");

        var primera = TrazaDomain.crear(solicitud, false);
        // Act
        var segunda = TrazaDomain.crear(solicitud, false);

        // Assert
        assertThat(primera.getCorrelacionId()).isEqualTo(segunda.getCorrelacionId());
        assertThat(primera.getTransaccionId()).isNotEqualTo(segunda.getTransaccionId());
    }

    @Test
    void debeSembrarUsuarioAnonimo_cuandoElOrigenEsHttp() {
        // Arrange
        var solicitud = SolicitudTraza.paraHttp(null, null, "203.0.113.25", "GET", "/api/fichas");

        // Act
        var traza = TrazaDomain.crear(solicitud, false);

        // Assert
        assertThat(traza.getUsuarioId()).isEqualTo(TrazaValores.ANONIMO);
    }

    @Test
    void debeSembrarUsuarioSistema_cuandoElOrigenEsProgramado() {
        // Act
        var traza = TrazaDomain.crear(SolicitudTraza.paraProgramado(), false);

        // Assert
        assertThat(traza.getUsuarioId()).isEqualTo(TrazaValores.SISTEMA);
        assertThat(traza.getOrigen()).isEqualTo(OrigenTraza.PROGRAMADO);
    }

    @Test
    void debeCalcularLaSalida_cuandoSeRegistraElCodigoDeEstado() {
        // Arrange
        var traza = TrazaDomain.crear(SolicitudTraza.paraHttp(null, null, "203.0.113.25", "GET", "/api"), false);

        // Act
        var salida = traza.registrarSalida(403);

        // Assert
        assertThat(salida.codigoEstado()).isEqualTo(403);
        assertThat(salida.duracionMs()).isNotNegative();
        assertThat(salida.tiempoSalida()).isAfterOrEqualTo(traza.getTiempoEntrada());
    }

    @Test
    void debeSanearLaRuta_cuandoIntentaInyectarUnaLineaDeLog() {
        // Arrange
        var solicitud = SolicitudTraza.paraHttp(null, null, "203.0.113.25", "GET", "/api\r\nERROR");

        // Act
        var traza = TrazaDomain.crear(solicitud, false);

        // Assert
        var detalle = (DetalleOrigenTraza.DetalleHttpTraza) traza.getDetalle();
        assertThat(detalle.rutaUri()).isEqualTo("/api__ERROR");
    }

    @Test
    void debePoblarElDetalleHttp_cuandoElOrigenEsHttp() {
        // Arrange
        var solicitud = SolicitudTraza.paraHttp(null, null, "203.0.113.25", "GET", "/api/fichas");

        // Act
        var traza = TrazaDomain.crear(solicitud, false);

        // Assert
        var detalle = (DetalleOrigenTraza.DetalleHttpTraza) traza.getDetalle();
        assertThat(detalle.clienteIp()).isEqualTo("203.0.113.25");
        assertThat(detalle.metodoHttp()).isEqualTo("GET");
        assertThat(detalle.rutaUri()).isEqualTo("/api/fichas");
    }

    @Test
    void debePoblarLaColaDelEvento_cuandoElOrigenEsEvento() {
        // Arrange
        var solicitud = SolicitudTraza.paraEvento("abc-123", "usuarios.usuario-creado");

        // Act
        var traza = TrazaDomain.crear(solicitud, false);

        // Assert
        var detalle = (DetalleOrigenTraza.DetalleEventoTraza) traza.getDetalle();
        assertThat(detalle.colaEvento()).isEqualTo("usuarios.usuario-creado");
    }

    @Test
    void debeDevolverColaDesconocida_cuandoElEventoNoTraeNombreDeCola() {
        // Arrange
        var solicitud = SolicitudTraza.paraEvento("abc-123", null);

        // Act
        var traza = TrazaDomain.crear(solicitud, false);

        // Assert
        var detalle = (DetalleOrigenTraza.DetalleEventoTraza) traza.getDetalle();
        assertThat(detalle.colaEvento()).isEqualTo(TrazaValores.DESCONOCIDO);
    }

    @Test
    void debePoblarElDetalleProgramado_cuandoElOrigenEsProgramado() {
        // Act
        var traza = TrazaDomain.crear(SolicitudTraza.paraProgramado(), false);

        // Assert
        assertThat(traza.getDetalle()).isInstanceOf(DetalleOrigenTraza.DetalleProgramadoTraza.class);
    }

    @Test
    void debeEmitirTraceparente_cuandoLaCorrelacionEsGenerada() {
        // Arrange
        var traza = TrazaDomain.crear(SolicitudTraza.paraHttp(null, null, "203.0.113.25", "GET", "/api"), false);

        // Act
        var traceparent = traza.traceparenteSaliente();

        // Assert
        assertThat(traceparent).isPresent();
        assertThat(traceparent.orElseThrow()).contains(traza.getCorrelacionId());
    }

    @Test
    void debeNoEmitirTraceparente_cuandoLaCorrelacionEntranteNoEsW3C() {
        // Act
        var traza = TrazaDomain.crear(SolicitudTraza.paraHttp("abc-123", null, "203.0.113.25", "GET", "/api"), false);

        // Assert
        assertThat(traza.traceparenteSaliente()).isEmpty();
    }
}
