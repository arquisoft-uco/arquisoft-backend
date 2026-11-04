package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.primaryport.interactor.ConsultarCriteriosItemCualitativoAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.primaryadapter.web.dto.CriterioItemCualitativoAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.primaryadapter.web.mapper.CriterioItemCualitativoAsesorResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.evaluaciones.criterios-item-cualitativo-asesor.base:/evaluaciones/criterios-item-cualitativo-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.CriterioItemCualitativoAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.CriterioItemCualitativoAsesor.TAG_DESCRIPTION)
public class ConsultarCriteriosItemCualitativoAsesorController {

    private final ConsultarCriteriosItemCualitativoAsesorInteractor interactor;

    @GetMapping
    @Operation(
            summary = EvaluacionesApiMessages.CriterioItemCualitativoAsesor.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.CriterioItemCualitativoAsesor.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.CriterioItemCualitativoAsesor.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CriterioItemCualitativoAsesorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<List<CriterioItemCualitativoAsesorResponseDTO>> consultarCriteriosItemCualitativoAsesor() {
        var items = interactor.ejecutar();

        return ResponseEntity.ok(items.stream()
                .map(CriterioItemCualitativoAsesorResponseMapper::toResponse)
                .toList());
    }
}
