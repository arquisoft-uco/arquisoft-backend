package com.arquisoft.shared.web.filter;

import com.arquisoft.shared.logger.MdcKeys;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TraceIdFilterTest {

    private final TraceIdFilter filter = new TraceIdFilter();

    @Test
    void debeReutilizarCorrelacionEntrante_cuandoHeaderEsValido() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CorrelationHeaders.X_CORRELATION_ID, "abc-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        AtomicReference<String> traceEnCadena = capturarTraceId(request, response);

        // Assert
        assertThat(traceEnCadena.get()).isEqualTo("abc-123");
        assertThat(response.getHeader(CorrelationHeaders.X_CORRELATION_ID)).isEqualTo("abc-123");
    }

    @Test
    void debeExtraerTraceIdDeTraceparent_cuandoNoHayCorrelacionPropia() throws Exception {
        // Arrange
        String traceId = "4bf92f3577b34da6a3ce929d0e0e4736";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CorrelationHeaders.TRACEPARENT, "00-" + traceId + "-00f067aa0ba902b7-01");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        AtomicReference<String> traceEnCadena = capturarTraceId(request, response);

        // Assert
        assertThat(traceEnCadena.get()).isEqualTo(traceId);
    }

    @Test
    void debeGenerarTraceId_cuandoNoLlegaCorrelacionExterna() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        AtomicReference<String> traceEnCadena = capturarTraceId(request, response);

        // Assert
        assertThat(traceEnCadena.get()).isNotBlank();
        assertThat(response.getHeader(CorrelationHeaders.X_CORRELATION_ID)).isEqualTo(traceEnCadena.get());
    }

    @Test
    void debeGenerarTraceId_cuandoCorrelacionEntranteEsInsegura() throws Exception {
        // Arrange — se descarta el valor con caracteres fuera de la whitelist (log injection)
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CorrelationHeaders.X_CORRELATION_ID, "abc\n123 INYECTADO");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        AtomicReference<String> traceEnCadena = capturarTraceId(request, response);

        // Assert
        assertThat(traceEnCadena.get()).doesNotContain("INYECTADO");
    }

    @Test
    void debeLimpiarMdc_cuandoTerminaLaPeticion() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        filter.doFilter(request, response, mock(FilterChain.class));

        // Assert
        assertThat(MDC.get(MdcKeys.TRACE_ID)).isNull();
    }

    private AtomicReference<String> capturarTraceId(MockHttpServletRequest request,
                                                    MockHttpServletResponse response) throws Exception {
        AtomicReference<String> capturado = new AtomicReference<>();
        FilterChain chain = (req, res) -> capturado.set(MDC.get(MdcKeys.TRACE_ID));
        filter.doFilter(request, response, chain);
        return capturado;
    }
}
