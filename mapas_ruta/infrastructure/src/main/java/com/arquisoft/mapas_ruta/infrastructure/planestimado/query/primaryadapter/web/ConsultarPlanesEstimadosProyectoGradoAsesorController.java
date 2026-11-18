package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.interactor.ConsultarPlanesEstimadosProyectoGradoAsesorInteractor;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.dto.PlanEstimadoProyectoResponseDTO;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.mapper.ConsultarPlanesEstimadosProyectoGradoAsesorRequestMapper;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.mapper.PlanEstimadoProyectoResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.MapasRutaApiMessages;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.dto.PageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.mapas-ruta.mapas-ruta.base:/mapas-ruta}")
@RequiredArgsConstructor
@Tag(name = MapasRutaApiMessages.PlanEstimado.TAG_NAME,
        description = MapasRutaApiMessages.PlanEstimado.TAG_DESCRIPTION)
public class ConsultarPlanesEstimadosProyectoGradoAsesorController {

    private final ConsultarPlanesEstimadosProyectoGradoAsesorInteractor consultarPlanesEstimadosProyectoGradoAsesorInteractor;

    @PostMapping("${rutas.mapas-ruta.mapas-ruta.planes-estimados-proyecto-asesor:"
            + "/proyectos-grado/{proyectoGradoId}/planes-estimados/asesor}")
    @Operation(
            summary = MapasRutaApiMessages.PlanEstimado.CONSULTAR_POR_PROYECTO_GRADO_ASESOR_SUMMARY,
            description = MapasRutaApiMessages.PlanEstimado.CONSULTAR_POR_PROYECTO_GRADO_ASESOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = MapasRutaApiMessages.PlanEstimado.CONSULTAR_POR_PROYECTO_GRADO_ASESOR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = MapasRutaApiMessages.PlanEstimado.CONSULTAR_POR_PROYECTO_GRADO_ASESOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<PageResponseDTO<PlanEstimadoProyectoResponseDTO>> consultarPlanesEstimadosDelProyectoGrado(
            @PathVariable UUID proyectoGradoId,
            @RequestBody(required = false) QueryCriteriaRequestDTO request) {

        var resultado = consultarPlanesEstimadosProyectoGradoAsesorInteractor.ejecutar(
                ConsultarPlanesEstimadosProyectoGradoAsesorRequestMapper.toQuery(request, proyectoGradoId));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(PlanEstimadoProyectoResponseMapper::toResponse)));
    }
}
