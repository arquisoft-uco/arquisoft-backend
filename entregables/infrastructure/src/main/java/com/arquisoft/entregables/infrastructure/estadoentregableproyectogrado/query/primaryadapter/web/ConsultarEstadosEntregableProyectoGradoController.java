package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.primaryadapter.web;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.interactor.ConsultarEstadosEntregableProyectoGradoInteractor;
import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.primaryadapter.web.dto.EstadoEntregableProyectoGradoResponseDTO;
import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.primaryadapter.web.mapper.ConsultarEstadosEntregableProyectoGradoRequestMapper;
import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.primaryadapter.web.mapper.EstadoEntregableProyectoGradoResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EntregablesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.entregables.entregables.base:/entregables}")
@RequiredArgsConstructor
@Tag(name = EntregablesApiMessages.EstadoEntregableProyectoGrado.TAG_NAME,
        description = EntregablesApiMessages.EstadoEntregableProyectoGrado.TAG_DESCRIPTION)
public class ConsultarEstadosEntregableProyectoGradoController {

    private final ConsultarEstadosEntregableProyectoGradoInteractor consultarEstadosEntregableProyectoGradoInteractor;

    @GetMapping("${rutas.entregables.entregables.estados:/{entregableProyectoGradoId}/estados}")
    @Operation(
            summary = EntregablesApiMessages.EstadoEntregableProyectoGrado.CONSULTAR_SUMMARY,
            description = EntregablesApiMessages.EstadoEntregableProyectoGrado.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = EntregablesApiMessages.EstadoEntregableProyectoGrado.CONSULTAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = EstadoEntregableProyectoGradoResponseDTO.class)))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = EntregablesApiMessages.EstadoEntregableProyectoGrado.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = EntregablesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = EntregablesApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = EntregablesApiMessages.EstadoEntregableProyectoGrado.CONSULTAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<EstadoEntregableProyectoGradoResponseDTO>> consultarEstadosEntregableProyectoGrado(
            @PathVariable UUID entregableProyectoGradoId,
            IdentidadActual identidad) {

        var estados = consultarEstadosEntregableProyectoGradoInteractor.ejecutar(
                ConsultarEstadosEntregableProyectoGradoRequestMapper.toQuery(entregableProyectoGradoId, identidad));

        return ResponseEntity.ok(estados.stream()
                .map(EstadoEntregableProyectoGradoResponseMapper::toResponse)
                .toList());
    }
}
