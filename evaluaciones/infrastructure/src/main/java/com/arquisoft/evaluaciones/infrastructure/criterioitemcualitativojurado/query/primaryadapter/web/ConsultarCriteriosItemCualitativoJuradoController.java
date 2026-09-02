package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.primaryport.interactor.ConsultarCriteriosItemCualitativoJuradoInteractor;
import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.query.primaryadapter.web.dto.CriterioItemCualitativoJuradoResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.query.primaryadapter.web.mapper.CriterioItemCualitativoJuradoResponseMapper;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.evaluaciones.criterios-item-cualitativo-jurado.base:/evaluaciones/criterios-item-cualitativo-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.CriterioItemCualitativoJurado.TAG_NAME,
        description = EvaluacionesApiMessages.CriterioItemCualitativoJurado.TAG_DESCRIPTION)
public class ConsultarCriteriosItemCualitativoJuradoController {

    private final ConsultarCriteriosItemCualitativoJuradoInteractor interactor;

    @GetMapping
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_CRITERIO_ITEM_CUALITATIVO_JURADO_VIEW)
    @Operation(
            summary = EvaluacionesApiMessages.CriterioItemCualitativoJurado.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.CriterioItemCualitativoJurado.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.CriterioItemCualitativoJurado.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CriterioItemCualitativoJuradoResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<List<CriterioItemCualitativoJuradoResponseDTO>> consultarCriteriosItemCualitativoJurado() {
        List<CriterioItemCualitativoJuradoReadModel> criterios = interactor.ejecutar(null);

        return ResponseEntity.ok(criterios.stream()
                .map(CriterioItemCualitativoJuradoResponseMapper::toResponse)
                .toList());
    }
}
