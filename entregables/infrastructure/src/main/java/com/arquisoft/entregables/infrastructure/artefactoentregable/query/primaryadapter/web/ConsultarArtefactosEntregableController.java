package com.arquisoft.entregables.infrastructure.artefactoentregable.query.primaryadapter.web;

import com.arquisoft.entregables.application.artefactoentregable.query.primaryport.interactor.ConsultarArtefactosEntregableInteractor;
import com.arquisoft.entregables.infrastructure.artefactoentregable.query.primaryadapter.web.dto.ArtefactoEntregableResponseDTO;
import com.arquisoft.entregables.infrastructure.artefactoentregable.query.primaryadapter.web.mapper.ArtefactoEntregableResponseMapper;
import com.arquisoft.entregables.infrastructure.artefactoentregable.query.primaryadapter.web.mapper.ConsultarArtefactosEntregableRequestMapper;
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
@RequestMapping("${rutas.entregables.entregables-proyecto-grado.base:/entregables-proyecto-grado}")
@RequiredArgsConstructor
@Tag(name = EntregablesApiMessages.ArtefactoEntregable.TAG_NAME,
        description = EntregablesApiMessages.ArtefactoEntregable.TAG_DESCRIPTION)
public class ConsultarArtefactosEntregableController {

    private final ConsultarArtefactosEntregableInteractor consultarArtefactosEntregableInteractor;

    @GetMapping("${rutas.entregables.entregables-proyecto-grado.artefactos:/{entregableProyectoGradoId}/artefactos}")
    @Operation(
            summary = EntregablesApiMessages.ArtefactoEntregable.CONSULTAR_SUMMARY,
            description = EntregablesApiMessages.ArtefactoEntregable.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = EntregablesApiMessages.ArtefactoEntregable.CONSULTAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ArtefactoEntregableResponseDTO.class)))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = EntregablesApiMessages.ArtefactoEntregable.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = EntregablesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = EntregablesApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = EntregablesApiMessages.ArtefactoEntregable.CONSULTAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<ArtefactoEntregableResponseDTO>> consultarArtefactosEntregable(
            @PathVariable UUID entregableProyectoGradoId,
            IdentidadActual identidad) {

        var artefactos = consultarArtefactosEntregableInteractor.ejecutar(
                ConsultarArtefactosEntregableRequestMapper.toQuery(entregableProyectoGradoId, identidad));

        return ResponseEntity.ok(artefactos.stream()
                .map(ArtefactoEntregableResponseMapper::toResponse)
                .toList());
    }
}
