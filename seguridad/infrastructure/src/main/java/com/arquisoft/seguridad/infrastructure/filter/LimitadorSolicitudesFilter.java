package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.message.key.seguridad.LimiteSolicitudesKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.seguridad.infrastructure.config.ratelimit.BucketResolver;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.filter.RutasTecnicas;
import tools.jackson.databind.ObjectMapper;
import io.github.bucket4j.ConsumptionProbe;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
@Order(-200)
@RequiredArgsConstructor
public class LimitadorSolicitudesFilter extends OncePerRequestFilter {

    // Oculta deliberadamente el campo heredado GenericFilterBean.logger (Commons Logging):
    // el proyecto registra a traves del puerto AppLogger, no del logger del framework.
    private final AppLogger logger;

    private final GestorTraza gestorTraza;

    private static final Pattern IP_PATTERN = Pattern.compile("^[\\d.:a-fA-F]{3,45}$");

    // Ruta de login: lleva su propia cuota, mucho mas estricta que la global, para frenar
    // fuerza bruta sin castigar al resto de la API. Incluye el context-path porque se compara
    // contra getRequestURI().
    private static final String RUTA_LOGIN = "/api/auth/login";

    // Cabeceras de respuesta del limitador: nombres de protocolo que el cliente lee tal cual.
    private static final String CABECERA_CUOTA_RESTANTE = "X-Rate-Limit-Remaining";
    private static final String CABECERA_REINTENTAR_TRAS = "X-Rate-Limit-Retry-After-Seconds";

    // Centinela para una IP que no encaja en IP_PATTERN: agrupa todas las peticiones con
    // origen ilegible en un unico bucket en vez de crear uno por cada valor manipulado.
    private static final String IP_NO_RECONOCIDA = "INVALID";

    private final BucketResolver bucketResolver;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return RutasTecnicas.esRutaTecnica(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        if (!bucketResolver.estaLimiteSolicitudesHabilitado()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Los preflight OPTIONS no consumen cuota — son generados por el browser, no por el usuario
        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);

        boolean isLoginEndpoint = RUTA_LOGIN.equals(request.getRequestURI());

        // Sin try/catch: el resolver es quien consume, y por tanto quien ve el fallo de Redis y
        // degrada a la cuota local. Aqui una excepcion escapando seguiria siendo grave —Tomcat
        // despacharia a /error y la respuesta perderia su ruta y su traceId— pero ya no puede
        // originarse en el limite, solo en un defecto del propio resolver.
        ConsumptionProbe probe = bucketResolver.consumir(clientIp, isLoginEndpoint);

        if (probe.isConsumed()) {
            response.addHeader(CABECERA_CUOTA_RESTANTE, String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitForRefill = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.addHeader(CABECERA_REINTENTAR_TRAS, String.valueOf(waitForRefill));

            var body = ErrorResponseDTO.builder()
                    .error(Mensajes.obtener(LimiteSolicitudesKey.ERROR_HTTP_DEMASIADAS_SOLICITUDES))
                    .errorCode(SeguridadCodes.LimiteSolicitudes.LIMITE_SOLICITUDES_EXCEDIDO)
                    .message(Mensajes.formatear(LimiteSolicitudesKey.ERROR_LIMITE_EXCEDIDO, waitForRefill))
                    .status(HttpStatus.TOO_MANY_REQUESTS.value())
                    .path(request.getRequestURI())
                    .traceId(gestorTraza.correlacionActual())
                    .transaccionId(gestorTraza.transaccionActual())
                    .build();
            objectMapper.writeValue(response.getWriter(), body);

            logger.warn(Mensajes.obtener(LimiteSolicitudesKey.LOG_LIMITE_EXCEDIDO), clientIp, request.getRequestURI());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return IP_PATTERN.matcher(ip).matches() ? ip : IP_NO_RECONOCIDA;
    }
}
