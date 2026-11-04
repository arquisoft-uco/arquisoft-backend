package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.primaryport.interactor.ConsultarItemsCuantitativosAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.primaryadapter.web.dto.ItemCuantitativoAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.primaryadapter.web.mapper.ItemCuantitativoAsesorResponseMapper;
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
@RequestMapping("${rutas.evaluaciones.items-cuantitativos-asesor.base:/evaluaciones/items-cuantitativos-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ItemCuantitativoAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.ItemCuantitativoAsesor.TAG_DESCRIPTION)
public class ConsultarItemsCuantitativosAsesorController {

    private final ConsultarItemsCuantitativosAsesorInteractor interactor;

    @GetMapping
    @Operation(
            summary = EvaluacionesApiMessages.ItemCuantitativoAsesor.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.ItemCuantitativoAsesor.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.ItemCuantitativoAsesor.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemCuantitativoAsesorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<List<ItemCuantitativoAsesorResponseDTO>> consultarItemsCuantitativosAsesor() {
        var items = interactor.ejecutar();

        return ResponseEntity.ok(items.stream()
                .map(ItemCuantitativoAsesorResponseMapper::toResponse)
                .toList());
    }
}
