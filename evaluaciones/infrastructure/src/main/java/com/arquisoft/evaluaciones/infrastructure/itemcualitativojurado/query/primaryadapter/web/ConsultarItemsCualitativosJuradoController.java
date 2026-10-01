package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.primaryport.interactor.ConsultarItemsCualitativosJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.primaryadapter.web.dto.ItemCualitativoJuradoResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.primaryadapter.web.mapper.ItemCualitativoJuradoResponseMapper;
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
@RequestMapping("${rutas.evaluaciones.items-cualitativos-jurado.base:/evaluaciones/items-cualitativos-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ItemCualitativoJurado.TAG_NAME,
        description = EvaluacionesApiMessages.ItemCualitativoJurado.TAG_DESCRIPTION)
public class ConsultarItemsCualitativosJuradoController {

    private final ConsultarItemsCualitativosJuradoInteractor interactor;

    @GetMapping
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_ITEM_CUALITATIVO_JURADO_VIEW)
    @Operation(
            summary = EvaluacionesApiMessages.ItemCualitativoJurado.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.ItemCualitativoJurado.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.ItemCualitativoJurado.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemCualitativoJuradoResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<List<ItemCualitativoJuradoResponseDTO>> consultarItemsCualitativosJurado() {
        List<ItemCualitativoJuradoReadModel> items = interactor.ejecutar();

        return ResponseEntity.ok(items.stream()
                .map(ItemCualitativoJuradoResponseMapper::toResponse)
                .toList());
    }
}
