package com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.primaryadapter.web;

import com.arquisoft.artefactos.application.estadorevisionasesor.query.primaryport.interactor.ConsultarEstadosRevisionAsesorInteractor;
import com.arquisoft.artefactos.infrastructure.security.ArtefactosAuthorities;
import com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.primaryadapter.web.dto.EstadoRevisionAsesorResponseDTO;
import com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.primaryadapter.web.mapper.EstadoRevisionAsesorResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ArtefactosApiMessages;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.artefactos.estados-revision-asesor.base:/artefactos/estados-revision-asesor}")
@RequiredArgsConstructor
@Tag(
        name = ArtefactosApiMessages.EstadoRevisionAsesor.TAG_NAME,
        description = ArtefactosApiMessages.EstadoRevisionAsesor.TAG_DESCRIPTION)
public class ConsultarEstadosRevisionAsesorController {

    private final ConsultarEstadosRevisionAsesorInteractor interactor;

    @GetMapping
    @PreAuthorize(ArtefactosAuthorities.Expresiones.HAS_ESTADO_REVISION_ASESOR_VIEW)
    @Operation(
            summary = ArtefactosApiMessages.EstadoRevisionAsesor.CONSULTAR_SUMMARY,
            description = ArtefactosApiMessages.EstadoRevisionAsesor.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = ArtefactosApiMessages.EstadoRevisionAsesor.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstadoRevisionAsesorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<List<EstadoRevisionAsesorResponseDTO>> consultarEstadosRevisionAsesor() {
        var estados = interactor.ejecutar();

        return ResponseEntity.ok(estados.stream()
                .map(EstadoRevisionAsesorResponseMapper::toResponse)
                .toList());
    }
}
