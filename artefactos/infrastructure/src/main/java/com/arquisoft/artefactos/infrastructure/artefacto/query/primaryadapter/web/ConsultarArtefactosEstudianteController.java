package com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web;

import com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor.ConsultarArtefactosEstudianteInteractor;
import com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.dto.ArtefactoEstudianteResponseDTO;
import com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.mapper.ArtefactoEstudianteResponseMapper;
import com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.mapper.ConsultarArtefactosEstudianteRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ArtefactosApiMessages;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.dto.PageResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.artefactos.artefactos.base:/artefactos}")
@RequiredArgsConstructor
@Tag(name = ArtefactosApiMessages.Artefacto.TAG_NAME, description = ArtefactosApiMessages.Artefacto.TAG_DESCRIPTION)
public class ConsultarArtefactosEstudianteController {

    private final ConsultarArtefactosEstudianteInteractor consultarArtefactosEstudianteInteractor;

    @PostMapping("${rutas.artefactos.artefactos.estudiante:/estudiante}")
    @Operation(
            summary = ArtefactosApiMessages.Artefacto.CONSULTAR_ESTUDIANTE_SUMMARY,
            description = ArtefactosApiMessages.Artefacto.CONSULTAR_ESTUDIANTE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = ArtefactosApiMessages.Artefacto.CONSULTAR_ESTUDIANTE_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.Artefacto.CONSULTAR_ESTUDIANTE_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Artefacto.CONSULTAR_ESTUDIANTE_RESP_403)
    })
    public ResponseEntity<PageResponseDTO<ArtefactoEstudianteResponseDTO>> consultarArtefactosEstudiante(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            IdentidadActual identidad) {

        var estudiante = identidad.id();
        var resultado = consultarArtefactosEstudianteInteractor.ejecutar(
                ConsultarArtefactosEstudianteRequestMapper.toQuery(request, estudiante));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(ArtefactoEstudianteResponseMapper::toResponse)));
    }
}
