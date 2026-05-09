package com.arquisoft.fichas.infrastructure.adapter.in.web;

import com.arquisoft.fichas.application.dto.FichaPerfilResumenDTO;
import com.arquisoft.fichas.domain.port.in.ConsultarFichasPerfilUseCase;
import com.arquisoft.shared.web.dto.PageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fichas Perfil", description = "Gestión de fichas de perfil de proyectos de grado")
public class FichaPerfilController {

    private final ConsultarFichasPerfilUseCase consultarFichasPerfilUseCase;

    @GetMapping("/coordinador")
    @PreAuthorize("hasAuthority('ficha:ficha:view')")
    @Operation(
            summary = "Listar fichas de perfil paginadas",
            description = "Retorna el listado paginado de todas las fichas de perfil registradas. Acceso exclusivo para el rol coordinador.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos",
                    content = @Content(schema = @Schema(implementation = com.arquisoft.shared.web.dto.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos — se requiere rol coordinador")
    })
    public ResponseEntity<PageResponseDTO<FichaPerfilResumenDTO>> consultarFichasCoordinador(
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        log.debug("GET /fichas-perfil/coordinador — pageable={}", pageable);

        Page<FichaPerfilResumenDTO> resultado = consultarFichasPerfilUseCase
                .ejecutar(pageable)
                .map(FichaPerfilResumenDTO::fromDomain);

        return ResponseEntity.ok(PageResponseDTO.from(resultado));
    }
}
