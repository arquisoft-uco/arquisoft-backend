package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.primaryport.interactor.ConsultarItemsCuantitativosJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.primaryadapter.web.dto.ItemCuantitativoJuradoResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.primaryadapter.web.mapper.ItemCuantitativoJuradoResponseMapper;
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
@RequestMapping("${rutas.evaluaciones.items-cuantitativos-jurado.base:/evaluaciones/items-cuantitativos-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ItemCuantitativoJurado.TAG_NAME,
        description = EvaluacionesApiMessages.ItemCuantitativoJurado.TAG_DESCRIPTION)
public class ConsultarItemsCuantitativosJuradoController {

    private final ConsultarItemsCuantitativosJuradoInteractor interactor;

    @GetMapping
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_ITEM_CUANTITATIVO_JURADO_VIEW)
    @Operation(
            summary = EvaluacionesApiMessages.ItemCuantitativoJurado.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.ItemCuantitativoJurado.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.ItemCuantitativoJurado.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemCuantitativoJuradoResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<List<ItemCuantitativoJuradoResponseDTO>> consultarItemsCuantitativosJurado() {
        List<ItemCuantitativoJuradoReadModel> items = interactor.ejecutar();

        return ResponseEntity.ok(items.stream()
                .map(ItemCuantitativoJuradoResponseMapper::toResponse)
                .toList());
    }
}
