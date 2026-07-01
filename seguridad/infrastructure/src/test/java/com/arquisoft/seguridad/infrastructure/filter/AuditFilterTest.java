package com.arquisoft.seguridad.infrastructure.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.arquisoft.shared.logger.MdcKeys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AuditFilter escribe URI saneada, IP validada y userId en el MDC solo durante la
 * escritura del log AUDIT (dentro del finally) y los limpia antes de retornar —
 * por eso las aserciones deben leer el evento de log capturado, no el MDC post-filtro.
 */
@ExtendWith(MockitoExtension.class)
class AuditFilterTest {

    @InjectMocks
    private AuditFilter filter;

    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void attachLogAppender() {
        Logger logger = (Logger) LoggerFactory.getLogger(AuditFilter.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(AuditFilter.class);
        logger.detachAppender(logAppender);
        MDC.clear();
        SecurityContextHolder.clearContext();
    }

    private HttpServletRequest requestFor(String method, String uri, String remoteAddr) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getRemoteAddr()).thenReturn(remoteAddr);
        return request;
    }

    @Test
    void debeLogearConNivelInfo_cuandoStatus2xx() throws Exception {
        // Arrange
        HttpServletRequest request = requestFor("POST", "/api/auth/login", "192.168.1.50");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(response.getStatus()).thenReturn(200);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        ILoggingEvent event = logAppender.list.get(0);
        assertThat(event.getLevel()).isEqualTo(Level.INFO);
        assertThat(event.getMDCPropertyMap()).containsEntry(MdcKeys.HTTP_STATUS, "200");
        assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
    }

    @Test
    void debeLogearConNivelWarn_cuandoStatus4xx() throws Exception {
        // Arrange
        HttpServletRequest request = requestFor("POST", "/api/auth/login", "192.168.1.50");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(response.getStatus()).thenReturn(401);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        ILoggingEvent event = logAppender.list.get(0);
        assertThat(event.getLevel()).isEqualTo(Level.WARN);
        assertThat(event.getMDCPropertyMap()).containsEntry(MdcKeys.HTTP_STATUS, "401");
    }

    @Test
    void debeLogearConNivelError_cuandoStatus5xx() throws Exception {
        // Arrange
        HttpServletRequest request = requestFor("GET", "/api/fichas-perfil", "192.168.1.100");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(response.getStatus()).thenReturn(500);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        ILoggingEvent event = logAppender.list.get(0);
        assertThat(event.getLevel()).isEqualTo(Level.ERROR);
        assertThat(event.getMDCPropertyMap()).containsEntry(MdcKeys.HTTP_STATUS, "500");
    }

    @Test
    void debeExtraerUserIdDesdeJwt_cuandoAutenticado() throws Exception {
        // Arrange
        Jwt jwt = Jwt.withTokenValue("token-prueba")
                .header("alg", "RS256")
                .subject("uuid-estudiante-123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        HttpServletRequest request = requestFor("GET", "/api/fichas-perfil", "192.168.1.100");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(response.getStatus()).thenReturn(200);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        ILoggingEvent event = logAppender.list.get(0);
        assertThat(event.getMDCPropertyMap()).containsEntry(MdcKeys.USER_ID, "uuid-estudiante-123");
    }

    @Test
    void debeUsarAnonymous_cuandoNoAutenticado() throws Exception {
        // Arrange
        SecurityContextHolder.clearContext();
        HttpServletRequest request = requestFor("GET", "/api/auth/validate", "10.0.0.1");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(response.getStatus()).thenReturn(200);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        ILoggingEvent event = logAppender.list.get(0);
        assertThat(event.getMDCPropertyMap()).containsEntry(MdcKeys.USER_ID, "ANONYMOUS");
    }

    @Test
    void debeSanitizarUri_cuandoContieneCaracteresEspeciales() throws Exception {
        // Arrange
        HttpServletRequest request = requestFor("GET", "/api/fichas\r\n\t\0/malicious", "192.168.1.100");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(response.getStatus()).thenReturn(200);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        ILoggingEvent event = logAppender.list.get(0);
        assertThat(event.getMDCPropertyMap()).containsEntry(MdcKeys.HTTP_URI, "/api/fichas____/malicious");
    }

    @Test
    void debeExtraerIpDesdeRemoteAddr_cuandoIpValida() throws Exception {
        // Arrange
        HttpServletRequest request = requestFor("POST", "/api/auth/login", "203.0.113.25");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(response.getStatus()).thenReturn(200);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        ILoggingEvent event = logAppender.list.get(0);
        assertThat(event.getMDCPropertyMap()).containsEntry(MdcKeys.CLIENT_IP, "203.0.113.25");
    }

    @Test
    void debeRetornarInvalid_cuandoIpMalformada() throws Exception {
        // Arrange
        HttpServletRequest request = requestFor("POST", "/api/auth/login", "invalid-ip-!@#$%");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(response.getStatus()).thenReturn(200);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        ILoggingEvent event = logAppender.list.get(0);
        assertThat(event.getMDCPropertyMap()).containsEntry(MdcKeys.CLIENT_IP, "INVALID");
    }
}
