package com.arquisoft.artefactos.infrastructure.revisionasesor.query.primaryadapter.web;

import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.interactor.ConsultarRevisionesAsesorEstudianteInteractor;
import com.arquisoft.artefactos.infrastructure.revisionasesor.query.primaryadapter.web.dto.RevisionAsesorResponseDTO;
import com.arquisoft.artefactos.infrastructure.revisionasesor.query.primaryadapter.web.mapper.ConsultarRevisionesAsesorEstudianteRequestMapper;
import com.arquisoft.artefactos.infrastructure.revisionasesor.query.primaryadapter.web.mapper.RevisionAsesorResponseMapper;
import com.arquisoft.artefactos.infrastructure.security.ArtefactosAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ArtefactosApiMessages;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.artefactos.revisiones-asesor.base:/artefactos/revisiones-asesor}")
@RequiredArgsConstructor
@Tag(name = ArtefactosApiMessages.RevisionAsesor.TAG_NAME, description = ArtefactosApiMessages.RevisionAsesor.TAG_DESCRIPTION)
public class ConsultarRevisionesAsesorEstudianteController {

    private final ConsultarRevisionesAsesorEstudianteInteractor consultarRevisionesAsesorEstudianteInteractor;

    @PostMapping("${rutas.artefactos.revisiones-asesor.estudiante:/estudiante}")
    @PreAuthorize(ArtefactosAuthorities.Expresiones.HAS_REVISION_ASESOR_ESTUDIANTE_VIEW)
    @Operation(
            summary = ArtefactosApiMessages.RevisionAsesor.CONSULTAR_ESTUDIANTE_SUMMARY,
            description = ArtefactosApiMessages.RevisionAsesor.CONSULTAR_ESTUDIANTE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = ArtefactosApiMessages.RevisionAsesor.CONSULTAR_ESTUDIANTE_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.RevisionAsesor.CONSULTAR_ESTUDIANTE_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.RevisionAsesor.CONSULTAR_ESTUDIANTE_RESP_403)
    })
    public ResponseEntity<PageResponseDTO<RevisionAsesorResponseDTO>> consultarRevisionesDeSusArtefactos(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        var estudiante = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        var resultado = consultarRevisionesAsesorEstudianteInteractor.ejecutar(
                ConsultarRevisionesAsesorEstudianteRequestMapper.toQuery(request, estudiante));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(RevisionAsesorResponseMapper::toResponse)));
    }
}
