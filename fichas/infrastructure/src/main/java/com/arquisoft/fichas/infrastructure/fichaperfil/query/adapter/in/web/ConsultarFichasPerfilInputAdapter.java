package com.arquisoft.fichas.infrastructure.fichaperfil.query.adapter.in.web;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.in.ConsultarFichasPerfilInputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.web.dto.PageResponseDTO;
import com.arquisoft.shared.web.dto.query.QueryCriteriaRequestDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fichas Perfil", description = "Gestión de fichas de perfil de proyectos de grado")
public class ConsultarFichasPerfilInputAdapter {

    private final ConsultarFichasPerfilInputPort consultarFichasPerfilInputPort;

    @PostMapping("/coordinador")
    @PreAuthorize("hasAuthority('ficha:ficha:view')")
    @Operation(
            summary = "Consultar fichas de perfil con filtros dinámicos",
            description = """
                    Retorna el listado paginado de fichas de perfil. Soporta filtros dinámicos
                    con agrupación booleana (AND/OR anidados), ordenamiento multi-campo y paginación.
                    El body es opcional: sin body devuelve todos los registros paginados.
                    Acceso exclusivo para el rol coordinador.
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Filtro, operador, campo o valor inválido",
                    content = @Content(schema = @Schema(implementation = com.arquisoft.shared.web.dto.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos — se requiere rol coordinador")
    })
    public ResponseEntity<PageResponseDTO<FichaPerfilReadModel>> consultarFichasCoordinador(
            @RequestBody(required = false) QueryCriteriaRequestDTO request) {

        QueryCriteriaRequestDTO req = request != null ? request : new QueryCriteriaRequestDTO();
        log.debug("POST /fichas-perfil/coordinador — pagina={}, tamanio={}", req.getPagina(), req.getTamanio());

        FichaPerfilCriteria criteria = FichaPerfilCriteria.builder()
                .pagina(req.getPagina())
                .tamanio(req.getTamanio())
                .ordenamiento(req.parsearOrdenamiento())
                .raiz(req.parsearFiltros())
                .build();

        PaginatedResult<FichaPerfilReadModel> resultado = consultarFichasPerfilInputPort.ejecutar(criteria);

        return ResponseEntity.ok(PageResponseDTO.from(resultado));
    }
}
