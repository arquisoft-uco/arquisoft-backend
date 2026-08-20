package com.arquisoft.shared.tracing.application.traza.primaryport;

import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import com.arquisoft.shared.tracing.domain.traza.model.TrazaValores;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.TrazaKeys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GestorTrazaTest {

    private final GestorTraza gestor =
            new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false);

    @AfterEach
    void limpiar() {
        MDC.clear();
    }

    @Test
    void debePublicarLaTrazaEnElContexto_cuandoSeAbreElAlcance() {
        // Arrange
        var solicitud = SolicitudTraza.paraHttp("abc-123", null, "203.0.113.25", "GET", "/api/fichas");

        // Act
        try (var alcance = gestor.abrir(solicitud)) {
            // Assert
            assertThat(gestor.correlacionActual()).isEqualTo("abc-123");
            assertThat(gestor.transaccionActual()).isEqualTo(alcance.transaccionId());
            assertThat(gestor.usuarioActual()).isEqualTo(TrazaValores.ANONIMO);
        }
    }

    @Test
    void debeLimpiarElContexto_cuandoSeCierraElAlcance() {
        // Act
        try (var alcance = gestor.abrir(SolicitudTraza.paraHttp(null, null, "203.0.113.25", "GET", "/api"))) {
            // Assert
            assertThat(alcance.correlacionId()).isNotBlank();
        }

        // Assert
        assertThat(gestor.correlacionActual()).isNull();
        assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
    }

    @Test
    void debeLimpiarElContexto_cuandoElCuerpoDelAlcanceLanza() {
        assertThatThrownBy(() -> {
            try (var alcance = gestor.abrir(SolicitudTraza.paraProgramado())) {
                gestor.registrarUsuario("usuario-1");
                // Assert
                assertThat(alcance.correlacionId()).isNotBlank();
                throw new IllegalStateException("fallo simulado");
            }
        }).isInstanceOf(IllegalStateException.class);

        // Assert
        assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
    }

    @Test
    void debeSobrescribirElUsuarioEnElContexto_cuandoSeRegistraDesdeFuera() {
        // Arrange
        try (var alcance = gestor.abrir(SolicitudTraza.paraHttp(null, null, "203.0.113.25", "GET", "/api"))) {
            // Act
            gestor.registrarUsuario("11111111-2222-3333-4444-555555555555");

            // Assert
            assertThat(gestor.usuarioActual()).isEqualTo("11111111-2222-3333-4444-555555555555");
            assertThat(alcance.correlacionId()).isNotBlank();
        }
    }

    @Test
    void debeConservarLaSemilla_cuandoElUsuarioRegistradoEstaEnBlanco() {
        // Arrange
        try (var alcance = gestor.abrir(SolicitudTraza.paraEvento("abc-123", "usuarios.usuario-creado"))) {
            // Act
            gestor.registrarUsuario("   ");

            // Assert
            assertThat(gestor.usuarioActual()).isEqualTo(TrazaValores.EVENTO);
            assertThat(alcance.correlacionId()).isEqualTo("abc-123");
        }
    }

    @Test
    void debeAnadirLosCamposDeSalida_cuandoSeRegistraElCodigoDeEstado() {
        // Act
        try (var alcance = gestor.abrir(SolicitudTraza.paraHttp(null, null, "203.0.113.25", "GET", "/api"))) {
            // Assert
            assertThat(MDC.get(TrazaKeys.CODIGO_ESTADO)).isNull();
            alcance.registrarSalida(404);

            // Assert
            assertThat(MDC.get(TrazaKeys.CODIGO_ESTADO)).isEqualTo("404");
            assertThat(MDC.get(TrazaKeys.DURACION_MS)).isNotBlank();
        }
    }

    @Test
    void debeRestaurarElAlcanceExterior_cuandoSeCierraUnoAnidado() {
        // Arrange
        try (var externo = gestor.abrir(SolicitudTraza.paraHttp("externa", null, "203.0.113.25", "GET", "/api"))) {
            // Act
            try (var interno = gestor.abrir(SolicitudTraza.paraEvento("interna", "usuarios.usuario-creado"))) {
                // Assert
                assertThat(gestor.correlacionActual()).isEqualTo("interna");
                assertThat(interno.correlacionId()).isEqualTo("interna");
            }

            // Assert
            assertThat(gestor.correlacionActual()).isEqualTo("externa");
            assertThat(externo.correlacionId()).isEqualTo("externa");
        }
    }

    @Test
    void debeDevolverNulo_cuandoSeConsultaFueraDeTodoAlcance() {
        // Assert
        assertThat(gestor.correlacionActual()).isNull();
        assertThat(gestor.transaccionActual()).isNull();
        assertThat(gestor.usuarioActual()).isNull();
    }
}
