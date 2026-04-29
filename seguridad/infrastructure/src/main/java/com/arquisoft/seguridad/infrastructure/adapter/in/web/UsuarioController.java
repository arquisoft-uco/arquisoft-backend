package com.arquisoft.seguridad.infrastructure.adapter.in.web;

import com.arquisoft.seguridad.application.dto.PaginaResponseDTO;
import com.arquisoft.seguridad.application.dto.UsuarioFiltroDTO;
import com.arquisoft.seguridad.application.dto.UsuarioResponseDTO;
import com.arquisoft.seguridad.application.usecase.ConsultarUsuariosUseCaseImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para la gestión de usuarios del sistema.
 *
 * <p>Expone el endpoint {@code GET /seguridad/usuarios} que permite al Administrador
 * consultar la lista de usuarios con filtros opcionales y paginación obligatoria.
 *
 * <p>Escribe los headers de paginación en la respuesta HTTP:
 * {@code X-Total-Count}, {@code X-Page-Number}, {@code X-Page-Size}.
 *
 * <p>Acceso restringido al rol {@code ADMINISTRADOR} (ADR-003).
 */
@Slf4j
@RestController
@RequestMapping("/seguridad/usuarios")
@RequiredArgsConstructor
@Tag(name = "Seguridad - Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioController {

    private final ConsultarUsuariosUseCaseImpl consultarUsuariosUseCase;

    /**
     * Consulta la lista de usuarios con filtros opcionales y paginación.
     *
     * <p>Todos los parámetros de filtro son opcionales. Si no se envía ninguno,
     * se retornan todos los usuarios paginados con los valores por defecto
     * ({@code pagina=0}, {@code tamano=20}).
     *
     * @param nombreOEmail búsqueda parcial (case-insensitive) sobre nombre, apellido o email
     * @param estado       filtro por estado: {@code ACTIVO} o {@code INACTIVO}
     * @param rol          filtro por rol contextual (ej. {@code ESTUDIANTE}, {@code COORDINADOR})
     * @param pagina       número de página (0-indexed, default: 0)
     * @param tamano       tamaño de página (1-100, default: 20)
     * @param response     respuesta HTTP para escribir los headers de paginación
     * @return lista de usuarios de la página solicitada con HTTP 200
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(
            summary = "Consultar usuarios con filtros y paginación",
            description = "Retorna la lista paginada de usuarios del sistema. "
                    + "Todos los filtros son opcionales y combinables. "
                    + "Solo accesible para el rol ADMINISTRADOR.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta exitosa — lista paginada de usuarios",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetro de filtro inválido (estado o rol desconocido)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado — token JWT ausente o inválido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Sin permisos — se requiere rol ADMINISTRADOR",
                    content = @Content
            )
    })
    public ResponseEntity<List<UsuarioResponseDTO>> consultarUsuarios(
            @RequestParam(required = false) String nombreOEmail,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String rol,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamano,
            HttpServletResponse response) {

        log.debug("GET /seguridad/usuarios — nombreOEmail={}, estado={}, rol={}, pagina={}, tamano={}",
                nombreOEmail, estado, rol, pagina, tamano);

        UsuarioFiltroDTO filtro = new UsuarioFiltroDTO(nombreOEmail, estado, rol, pagina, tamano);
        PaginaResponseDTO<UsuarioResponseDTO> pagina_resultado =
                consultarUsuariosUseCase.ejecutar(filtro);

        response.setHeader("X-Total-Count",  String.valueOf(pagina_resultado.totalElementos()));
        response.setHeader("X-Page-Number",  String.valueOf(pagina_resultado.numeroPagina()));
        response.setHeader("X-Page-Size",    String.valueOf(pagina_resultado.tamanoPagina()));

        return ResponseEntity.ok(pagina_resultado.contenido());
    }
}
