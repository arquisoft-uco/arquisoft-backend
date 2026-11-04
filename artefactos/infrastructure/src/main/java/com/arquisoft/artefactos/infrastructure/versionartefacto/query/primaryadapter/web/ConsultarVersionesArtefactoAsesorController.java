package com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.ConsultarVersionesArtefactoAsesorInteractor;
import com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.dto.VersionArtefactoResponseDTO;
import com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.mapper.ConsultarVersionesArtefactoAsesorRequestMapper;
import com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.mapper.VersionArtefactoResponseMapper;
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
@RequestMapping("${rutas.artefactos.versiones-artefacto.base:/versiones-artefacto}")
@RequiredArgsConstructor
@Tag(name = ArtefactosApiMessages.VersionArtefacto.TAG_NAME, description = ArtefactosApiMessages.VersionArtefacto.TAG_DESCRIPTION)
public class ConsultarVersionesArtefactoAsesorController {

    private final ConsultarVersionesArtefactoAsesorInteractor consultarVersionesArtefactoAsesorInteractor;

    @PostMapping("${rutas.artefactos.versiones-artefacto.asesor:/asesor}")
    @Operation(
            summary = ArtefactosApiMessages.VersionArtefacto.CONSULTAR_ASESOR_SUMMARY,
            description = ArtefactosApiMessages.VersionArtefacto.CONSULTAR_ASESOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = ArtefactosApiMessages.VersionArtefacto.CONSULTAR_ASESOR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.VersionArtefacto.CONSULTAR_ASESOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.VersionArtefacto.CONSULTAR_ASESOR_RESP_403)
    })
    public ResponseEntity<PageResponseDTO<VersionArtefactoResponseDTO>> consultarVersionesAsesor(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            IdentidadActual identidad) {

        var asesor = identidad.id();
        var resultado = consultarVersionesArtefactoAsesorInteractor.ejecutar(
                ConsultarVersionesArtefactoAsesorRequestMapper.toQuery(request, asesor));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(VersionArtefactoResponseMapper::toResponse)));
    }
}
