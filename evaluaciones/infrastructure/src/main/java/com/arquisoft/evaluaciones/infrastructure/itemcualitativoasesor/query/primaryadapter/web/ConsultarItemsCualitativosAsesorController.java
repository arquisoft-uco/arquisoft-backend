package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.primaryport.interactor.ConsultarItemsCualitativosAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.primaryadapter.web.dto.ItemCualitativoAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.primaryadapter.web.mapper.ItemCualitativoAsesorResponseMapper;
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
@RequestMapping("${rutas.evaluaciones.items-cualitativos-asesor.base:/evaluaciones/items-cualitativos-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ItemCualitativoAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.ItemCualitativoAsesor.TAG_DESCRIPTION)
public class ConsultarItemsCualitativosAsesorController {

    private final ConsultarItemsCualitativosAsesorInteractor interactor;

    @GetMapping
    @Operation(
            summary = EvaluacionesApiMessages.ItemCualitativoAsesor.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.ItemCualitativoAsesor.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.ItemCualitativoAsesor.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemCualitativoAsesorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<List<ItemCualitativoAsesorResponseDTO>> consultarItemsCualitativosAsesor() {
        var items = interactor.ejecutar();

        return ResponseEntity.ok(items.stream()
                .map(ItemCualitativoAsesorResponseMapper::toResponse)
                .toList());
    }
}
