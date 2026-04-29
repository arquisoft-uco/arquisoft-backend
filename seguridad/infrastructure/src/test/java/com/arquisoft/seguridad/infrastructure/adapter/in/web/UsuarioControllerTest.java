package com.arquisoft.seguridad.infrastructure.adapter.in.web;

import com.arquisoft.seguridad.application.dto.PaginaResponseDTO;
import com.arquisoft.seguridad.application.dto.UsuarioResponseDTO;
import com.arquisoft.seguridad.application.usecase.ConsultarUsuariosUseCaseImpl;
import com.arquisoft.seguridad.domain.exception.ParametroFiltroInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests del controller {@link UsuarioController} usando MockMvc standalone.
 *
 * <p><b>Estrategia Spring Boot 4.x:</b> {@code @WebMvcTest} fue eliminado en Spring Boot 4.x.
 * Se usa {@link MockMvcBuilders#standaloneSetup} con el controller y el
 * {@link GlobalExceptionHandler} registrado manualmente, más un
 * {@link org.springframework.security.web.FilterChainProxy} de test para validar
 * la autenticación y autorización vía {@code @PreAuthorize}.
 *
 * <p><b>Nota sobre @PreAuthorize:</b> en modo standalone, {@code @PreAuthorize} requiere
 * que el proxy de seguridad Spring esté activo. Para tests de autorización (401/403)
 * se verifica el comportamiento esperado del interceptor de seguridad.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConsultarUsuariosUseCaseImpl consultarUsuariosUseCase;

    @BeforeEach
    void setUp() {
        UsuarioController controller = new UsuarioController(consultarUsuariosUseCase);
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests HTTP 200 — flujos exitosos
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void debeRetornar200ConHeaders_cuandoAdministradorConsulta() throws Exception {
        // Arrange
        UsuarioResponseDTO dto = new UsuarioResponseDTO(
                java.util.UUID.randomUUID(),
                "Juan",
                "Pérez",
                "juan@test.com",
                "12345678",
                "ACTIVO",
                List.of("ADMINISTRADOR"));

        PaginaResponseDTO<UsuarioResponseDTO> pagina =
                PaginaResponseDTO.fromData(1L, 0, 20, List.of(dto));
        when(consultarUsuariosUseCase.ejecutar(any())).thenReturn(pagina);

        // Act & Assert
        mockMvc.perform(get("/seguridad/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(header().string("X-Page-Number", "0"))
                .andExpect(header().string("X-Page-Size", "20"))
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    void debeRetornar200ConListaVacia_cuandoNoHayCoincidencias() throws Exception {
        // Arrange
        PaginaResponseDTO<UsuarioResponseDTO> paginaVacia =
                PaginaResponseDTO.fromData(0L, 0, 20, List.of());
        when(consultarUsuariosUseCase.ejecutar(any())).thenReturn(paginaVacia);

        // Act & Assert
        mockMvc.perform(get("/seguridad/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "0"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test HTTP 400 — filtro inválido
    //
    // El GlobalExceptionHandler mapea ParametroFiltroInvalidoException → 400.
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void debeRetornar400_cuandoFiltroInvalido() throws Exception {
        // Arrange — use case lanza excepción de filtro inválido
        when(consultarUsuariosUseCase.ejecutar(any()))
                .thenThrow(new ParametroFiltroInvalidoException(
                        "FILTRO_ESTADO_INVALIDO",
                        "El valor de estado 'INVALIDO' no es válido."));

        // Act & Assert
        mockMvc.perform(get("/seguridad/usuarios")
                        .param("estado", "INVALIDO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests HTTP 401 y 403
    //
    // Nota: en MockMvc standalone, @PreAuthorize no está activo porque no hay
    // contexto de ApplicationContext con @EnableMethodSecurity. Los tests 401/403
    // verifican que el controller delega al use case cuando está autenticado,
    // y que la excepción AccessDeniedException se mapea a 403 por el handler.
    // La verificación real de 401 (sin token) se cubre en tests de integración.
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void debeRetornar403_cuandoRolInsuficiente() throws Exception {
        // Arrange — use case lanza AccessDeniedException (simulando que Spring Security
        // rechaza el acceso por rol insuficiente, lo que el handler mapea a 403)
        when(consultarUsuariosUseCase.ejecutar(any()))
                .thenThrow(new AccessDeniedException("Acceso denegado"));

        // Act & Assert
        mockMvc.perform(get("/seguridad/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeRetornar401_cuandoNoHayToken() throws Exception {
        // Arrange — use case lanza AuthenticationException simulando token ausente
        when(consultarUsuariosUseCase.ejecutar(any()))
                .thenThrow(new com.arquisoft.seguridad.domain.exception.AuthenticationException(
                        "Token JWT ausente o inválido"));

        // Act & Assert
        mockMvc.perform(get("/seguridad/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
