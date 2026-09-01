package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.seguridad.infrastructure.config.ratelimit.BucketResolver;
import tools.jackson.databind.ObjectMapper;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LimitadorSolicitudesFilterTest {

    @Mock
    private BucketResolver bucketResolver;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AppLogger logger;

    @Mock
    private GestorTraza gestorTraza;

    @InjectMocks
    private LimitadorSolicitudesFilter filter;

    @Test
    void debeContinuar_cuandoRateLimitDeshabilitado() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(bucketResolver.estaLimiteSolicitudesHabilitado()).thenReturn(false);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(bucketResolver, never()).consumir(anyString(), anyBoolean());
    }

    @Test
    void debeContinuar_cuandoPreflightOptions() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(bucketResolver.estaLimiteSolicitudesHabilitado()).thenReturn(true);
        when(request.getMethod()).thenReturn("OPTIONS");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(bucketResolver, never()).consumir(anyString(), anyBoolean());
    }

    @Test
    void debeContinuar_cuandoDentroDeLimiteGlobal() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        ConsumptionProbe probe = mock(ConsumptionProbe.class);

        when(probe.isConsumed()).thenReturn(true);
        when(probe.getRemainingTokens()).thenReturn(50L);

        when(bucketResolver.estaLimiteSolicitudesHabilitado()).thenReturn(true);
        when(bucketResolver.consumir(anyString(), eq(false))).thenReturn(probe);

        when(request.getRequestURI()).thenReturn("/api/fichas-perfil");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRemoteAddr()).thenReturn("192.168.1.10");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(response).addHeader("X-Rate-Limit-Remaining", "50");
    }

    @Test
    void debeContinuar_cuandoDentroDeLimiteLogin() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        ConsumptionProbe probe = mock(ConsumptionProbe.class);

        when(probe.isConsumed()).thenReturn(true);
        when(probe.getRemainingTokens()).thenReturn(3L);

        when(bucketResolver.estaLimiteSolicitudesHabilitado()).thenReturn(true);
        when(bucketResolver.consumir(anyString(), eq(true))).thenReturn(probe);

        when(request.getRequestURI()).thenReturn("/api/auth/login");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn("192.168.1.10");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(response).addHeader("X-Rate-Limit-Remaining", "3");
    }

    @Test
    void debeRetornar429_cuandoExcedeLimiteGlobal() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        ConsumptionProbe probe = mock(ConsumptionProbe.class);

        when(probe.isConsumed()).thenReturn(false);
        when(probe.getNanosToWaitForRefill()).thenReturn(60_000_000_000L);

        when(bucketResolver.estaLimiteSolicitudesHabilitado()).thenReturn(true);
        when(bucketResolver.consumir(anyString(), eq(false))).thenReturn(probe);

        when(request.getRequestURI()).thenReturn("/api/fichas-perfil");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn("192.168.1.10");

        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setStatus(429);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setCharacterEncoding(StandardCharsets.UTF_8.name());
        verify(response).addHeader("X-Rate-Limit-Retry-After-Seconds", "60");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void debeRetornar429_cuandoExcedeLimiteLogin() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        ConsumptionProbe probe = mock(ConsumptionProbe.class);

        when(probe.isConsumed()).thenReturn(false);
        when(probe.getNanosToWaitForRefill()).thenReturn(30_000_000_000L);

        when(bucketResolver.estaLimiteSolicitudesHabilitado()).thenReturn(true);
        when(bucketResolver.consumir(anyString(), eq(true))).thenReturn(probe);

        when(request.getRequestURI()).thenReturn("/api/auth/login");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn("192.168.1.10");

        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setStatus(429);
        verify(response).addHeader("X-Rate-Limit-Retry-After-Seconds", "30");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void debeExtraerIpCorrecta_cuandoRemoteAddrEsValido() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        ConsumptionProbe probe = mock(ConsumptionProbe.class);

        when(probe.isConsumed()).thenReturn(true);
        when(probe.getRemainingTokens()).thenReturn(10L);

        when(bucketResolver.estaLimiteSolicitudesHabilitado()).thenReturn(true);
        when(bucketResolver.consumir("192.168.1.100", false)).thenReturn(probe);

        when(request.getRequestURI()).thenReturn("/api/proyectos");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(bucketResolver).consumir("192.168.1.100", false);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void debeUsarInvalidIp_cuandoRemoteAddrMalformado() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        ConsumptionProbe probe = mock(ConsumptionProbe.class);

        when(probe.isConsumed()).thenReturn(true);
        when(probe.getRemainingTokens()).thenReturn(10L);

        when(bucketResolver.estaLimiteSolicitudesHabilitado()).thenReturn(true);
        when(bucketResolver.consumir("INVALID", false)).thenReturn(probe);

        when(request.getRequestURI()).thenReturn("/api/proyectos");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRemoteAddr()).thenReturn("malformed-ip-address-!@#$");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(bucketResolver).consumir("INVALID", false);
        verify(filterChain).doFilter(request, response);
    }
}
