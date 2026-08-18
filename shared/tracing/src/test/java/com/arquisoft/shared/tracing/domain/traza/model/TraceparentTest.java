package com.arquisoft.shared.tracing.domain.traza.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TraceparentTest {

    private static final String TRACE_ID = "4bf92f3577b34da6a3ce929d0e0e4736";

    private static final String PARENT_ID = "00f067aa0ba902b7";

    @Test
    void debeExtraerTraceId_cuandoElTraceparentEsValido() {
        // Act
        var traceId = Traceparent.extraerTraceId("00-" + TRACE_ID + "-" + PARENT_ID + "-01");

        // Assert
        assertThat(traceId).contains(TRACE_ID);
    }

    @Test
    void debeDevolverVacio_cuandoElTraceparentEstaMalformado() {
        // Assert
        assertThat(Traceparent.extraerTraceId(null)).isEmpty();
        assertThat(Traceparent.extraerTraceId("")).isEmpty();
        assertThat(Traceparent.extraerTraceId("00-" + TRACE_ID + "-" + PARENT_ID)).isEmpty();
        assertThat(Traceparent.extraerTraceId("00-" + TRACE_ID.toUpperCase() + "-" + PARENT_ID + "-01")).isEmpty();
    }

    @Test
    void debeDevolverVacio_cuandoElTraceIdEsTodoCeros() {
        // Assert
        assertThat(Traceparent.extraerTraceId("00-" + "0".repeat(32) + "-" + PARENT_ID + "-01")).isEmpty();
    }

    @Test
    void debeEmitirTraceparent_cuandoLaCorrelacionTieneFormaW3C() {
        // Act
        var cabecera = Traceparent.emitir(TRACE_ID, PARENT_ID);

        // Assert
        assertThat(cabecera).contains("00-" + TRACE_ID + "-" + PARENT_ID + "-01");
    }

    @Test
    void debeNoEmitirTraceparent_cuandoLaCorrelacionNoEsW3C() {
        // Assert
        assertThat(Traceparent.emitir("abc-123", PARENT_ID)).isEmpty();
        assertThat(Traceparent.emitir(TRACE_ID, null)).isEmpty();
    }
}
