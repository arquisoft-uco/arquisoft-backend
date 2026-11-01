package com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web;

import com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor.ConsultarArtefactosSinAvanceInteractor;
import com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.dto.ArtefactoSinAvanceResponseDTO;
import com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.dto.ConsultarArtefactosSinAvanceRequestDTO;
import com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.mapper.ArtefactoSinAvanceResponseMapper;
import com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.mapper.ConsultarArtefactosSinAvanceRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ArtefactosApiMessages;
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
public class ConsultarArtefactosSinAvanceController {

    private final ConsultarArtefactosSinAvanceInteractor consultarArtefactosSinAvanceInteractor;

    @PostMapping("${rutas.artefactos.artefactos.asesor-sin-avance:/asesor/sin-avance}")
    @Operation(
            summary = ArtefactosApiMessages.Artefacto.SIN_AVANCE_SUMMARY,
            description = ArtefactosApiMessages.Artefacto.SIN_AVANCE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = ArtefactosApiMessages.Artefacto.SIN_AVANCE_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.Artefacto.SIN_AVANCE_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Artefacto.SIN_AVANCE_RESP_403)
    })
    public ResponseEntity<PageResponseDTO<ArtefactoSinAvanceResponseDTO>> consultarArtefactosSinAvance(
            @RequestBody(required = false) ConsultarArtefactosSinAvanceRequestDTO request,
            IdentidadActual identidad) {

        var asesor = identidad.id();
        var resultado = consultarArtefactosSinAvanceInteractor.ejecutar(
                ConsultarArtefactosSinAvanceRequestMapper.toQuery(request, asesor));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(ArtefactoSinAvanceResponseMapper::toResponse)));
    }
}
