package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.interactor.RegistrarItemCuantitativoAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web.dto.RegistrarItemCuantitativoAsesorRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web.dto.RegistrarItemCuantitativoAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web.mapper.RegistrarItemCuantitativoAsesorRequestMapper;
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
@RequestMapping("${rutas.evaluaciones.items-cuantitativos-asesor.base:/evaluaciones/items-cuantitativos-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ItemCuantitativoAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.ItemCuantitativoAsesor.TAG_DESCRIPTION)
public class RegistrarItemCuantitativoAsesorController {

    private final RegistrarItemCuantitativoAsesorInteractor interactor;

    @PostMapping
    @Operation(
            summary = EvaluacionesApiMessages.ItemCuantitativoAsesor.REGISTRAR_SUMMARY,
            description = EvaluacionesApiMessages.ItemCuantitativoAsesor.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = EvaluacionesApiMessages.ItemCuantitativoAsesor.REGISTRAR_RESP_201,
                    content = @Content(schema = @Schema(
                            implementation = RegistrarItemCuantitativoAsesorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.ItemCuantitativoAsesor.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.ItemCuantitativoAsesor.REGISTRAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<RegistrarItemCuantitativoAsesorResponseDTO> registrar(
            @RequestBody RegistrarItemCuantitativoAsesorRequestDTO request) {
        var id = interactor.ejecutar(RegistrarItemCuantitativoAsesorRequestMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrarItemCuantitativoAsesorResponseDTO(id));
    }
}
