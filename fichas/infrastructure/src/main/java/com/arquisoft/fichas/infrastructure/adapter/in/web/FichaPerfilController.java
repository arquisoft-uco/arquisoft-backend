package com.arquisoft.fichas.infrastructure.adapter.in.web;

import com.arquisoft.fichas.application.dto.FichaPerfilResumenDTO;
import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.fichas.domain.port.in.ConsultarFichasPerfilUseCase;
import com.arquisoft.shared.domain.Page;
import com.arquisoft.shared.web.PageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Fichas Perfil", description = "Gestión de fichas de perfil de proyectos de grado")
public class FichaPerfilController {

    private final ConsultarFichasPerfilUseCase consultarFichasPerfilUseCase;

    @GetMapping("/coordinador")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(
            summary = "Listar fichas de perfil paginadas",
            description = "Retorna el listado paginado de todas las fichas de perfil registradas. Acceso exclusivo para el rol COORDINADOR.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos",
                    content = @Content(schema = @Schema(implementation = com.arquisoft.shared.web.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos — se requiere rol COORDINADOR")
    })
    public ResponseEntity<PageResponseDTO<FichaPerfilResumenDTO>> consultarFichasCoordinador(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Positive int size) {

        log.debug("GET /fichas-perfil/coordinador — page={}, size={}", page, size);

        Page<FichaPerfil> resultado = consultarFichasPerfilUseCase.ejecutar(page, size);

        List<FichaPerfilResumenDTO> content = resultado.content().stream()
                .map(FichaPerfilResumenDTO::fromDomain)
                .toList();

        Page<FichaPerfilResumenDTO> pageResumen =
                Page.of(content, resultado.page(), resultado.size(), resultado.totalElements());

        return ResponseEntity.ok(PageResponseDTO.from(pageResumen));
    }
}
