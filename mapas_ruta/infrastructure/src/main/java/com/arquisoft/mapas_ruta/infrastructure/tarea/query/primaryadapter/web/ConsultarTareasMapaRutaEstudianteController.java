package com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor.ConsultarTareasMapaRutaEstudianteInteractor;
import com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.dto.TareaResponseDTO;
import com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.mapper.ConsultarTareasMapaRutaEstudianteRequestMapper;
import com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.mapper.TareaResponseMapper;
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
@Tag(name = MapasRutaApiMessages.Tarea.TAG_NAME, description = MapasRutaApiMessages.Tarea.TAG_DESCRIPTION)
public class ConsultarTareasMapaRutaEstudianteController {

    private final ConsultarTareasMapaRutaEstudianteInteractor consultarTareasMapaRutaEstudianteInteractor;

    @PostMapping("${rutas.mapas-ruta.mapas-ruta.tareas-estudiante:/{mapaRutaId}/tareas/estudiante}")
    @Operation(
            summary = MapasRutaApiMessages.Tarea.CONSULTAR_POR_MAPA_RUTA_SUMMARY,
            description = MapasRutaApiMessages.Tarea.CONSULTAR_POR_MAPA_RUTA_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = MapasRutaApiMessages.Tarea.CONSULTAR_POR_MAPA_RUTA_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = MapasRutaApiMessages.Tarea.CONSULTAR_POR_MAPA_RUTA_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<PageResponseDTO<TareaResponseDTO>> consultarTareasPorMapaRuta(
            @PathVariable UUID mapaRutaId,
            @RequestBody(required = false) QueryCriteriaRequestDTO request) {

        var resultado = consultarTareasMapaRutaEstudianteInteractor.ejecutar(
                ConsultarTareasMapaRutaEstudianteRequestMapper.toQuery(request, mapaRutaId));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(TareaResponseMapper::toResponse)));
    }
}
