package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.interactor.ConsultarPlanesEstimadosTareaEstudianteInteractor;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.dto.PlanEstimadoResponseDTO;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.mapper.ConsultarPlanesEstimadosTareaEstudianteRequestMapper;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.mapper.PlanEstimadoResponseMapper;
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
@RequestMapping("${rutas.mapas-ruta.tareas.base:/tareas}")
@RequiredArgsConstructor
@Tag(name = MapasRutaApiMessages.PlanEstimado.TAG_NAME,
        description = MapasRutaApiMessages.PlanEstimado.TAG_DESCRIPTION)
public class ConsultarPlanesEstimadosTareaEstudianteController {

    private final ConsultarPlanesEstimadosTareaEstudianteInteractor consultarPlanesEstimadosTareaEstudianteInteractor;

    @PostMapping("${rutas.mapas-ruta.tareas.planes-estimados-estudiante:/{tareaId}/planes-estimados/estudiante}")
    @Operation(
            summary = MapasRutaApiMessages.PlanEstimado.CONSULTAR_SUMMARY,
            description = MapasRutaApiMessages.PlanEstimado.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = MapasRutaApiMessages.PlanEstimado.CONSULTAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = MapasRutaApiMessages.PlanEstimado.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<PageResponseDTO<PlanEstimadoResponseDTO>> consultarPlanesEstimadosDeLaTarea(
            @PathVariable UUID tareaId,
            @RequestBody(required = false) QueryCriteriaRequestDTO request) {

        var resultado = consultarPlanesEstimadosTareaEstudianteInteractor.ejecutar(
                ConsultarPlanesEstimadosTareaEstudianteRequestMapper.toQuery(request, tareaId));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(PlanEstimadoResponseMapper::toResponse)));
    }
}
