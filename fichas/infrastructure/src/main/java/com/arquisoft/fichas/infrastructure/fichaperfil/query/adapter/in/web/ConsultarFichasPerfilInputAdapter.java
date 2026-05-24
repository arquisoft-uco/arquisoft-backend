package com.arquisoft.fichas.infrastructure.fichaperfil.query.adapter.in.web;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.in.ConsultarFichasPerfilInputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.pagination.PaginatedResult;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fichas Perfil", description = "Gestión de fichas de perfil de proyectos de grado")
public class ConsultarFichasPerfilInputAdapter {

    private final ConsultarFichasPerfilInputPort consultarFichasPerfilInputPort;

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
    public ResponseEntity<PageResponseDTO<FichaPerfilReadModel>> consultarFichasCoordinador(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) UUID asesorId) {
        log.debug("GET /fichas-perfil/coordinador — page={}, size={}", page, size);

        FichaPerfilCriteria criteria = FichaPerfilCriteria.builder()
                .pagina(page)
                .tamanio(size)
                .ordenarPor(sort)
                .tituloProyecto(titulo)
                .asesorId(asesorId)
                .build();

        PaginatedResult<FichaPerfilReadModel> resultado = consultarFichasPerfilInputPort.ejecutar(criteria);

        return ResponseEntity.ok(PageResponseDTO.from(resultado));
    }
}
