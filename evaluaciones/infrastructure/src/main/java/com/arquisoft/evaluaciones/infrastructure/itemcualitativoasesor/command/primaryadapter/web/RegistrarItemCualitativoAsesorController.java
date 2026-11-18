package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.RegistrarItemCualitativoAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web.dto.RegistrarItemCualitativoAsesorRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web.dto.RegistrarItemCualitativoAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web.mapper.RegistrarItemCualitativoAsesorRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.evaluaciones.items-cualitativos-asesor.base:/evaluaciones/items-cualitativos-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ItemCualitativoAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.ItemCualitativoAsesor.TAG_DESCRIPTION)
public class RegistrarItemCualitativoAsesorController {

    private final RegistrarItemCualitativoAsesorInteractor interactor;

    @PostMapping
    @Operation(
            summary = EvaluacionesApiMessages.ItemCualitativoAsesor.REGISTRAR_SUMMARY,
            description = EvaluacionesApiMessages.ItemCualitativoAsesor.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = EvaluacionesApiMessages.ItemCualitativoAsesor.REGISTRAR_RESP_201,
                    content = @Content(schema = @Schema(
                            implementation = RegistrarItemCualitativoAsesorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.ItemCualitativoAsesor.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.ItemCualitativoAsesor.REGISTRAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<RegistrarItemCualitativoAsesorResponseDTO> registrar(
            @RequestBody RegistrarItemCualitativoAsesorRequestDTO request) {
        var id = interactor.ejecutar(RegistrarItemCualitativoAsesorRequestMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrarItemCualitativoAsesorResponseDTO(id));
    }
}
