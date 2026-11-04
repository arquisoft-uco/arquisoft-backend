package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.CambiarEstadoEntregableProyectoGradoInteractor;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.dto.CambiarEstadoEntregableProyectoGradoRequestDTO;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.mapper.CambiarEstadoEntregableProyectoGradoRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EntregablesApiMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.entregables.entregables-proyecto-grado.base:/entregables-proyecto-grado}")
@RequiredArgsConstructor
@Tag(name = EntregablesApiMessages.EntregableProyectoGrado.TAG_NAME,
        description = EntregablesApiMessages.EntregableProyectoGrado.TAG_DESCRIPTION)
public class CambiarEstadoEntregableProyectoGradoController {

    private final CambiarEstadoEntregableProyectoGradoInteractor cambiarEstadoEntregableProyectoGradoInteractor;

    @PatchMapping("${rutas.entregables.entregables-proyecto-grado.estado:/{entregableProyectoGradoId}/estado}")
    @Operation(
            summary = EntregablesApiMessages.EntregableProyectoGrado.CAMBIAR_ESTADO_SUMMARY,
            description = EntregablesApiMessages.EntregableProyectoGrado.CAMBIAR_ESTADO_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = EntregablesApiMessages.EntregableProyectoGrado.CAMBIAR_ESTADO_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = EntregablesApiMessages.EntregableProyectoGrado.CAMBIAR_ESTADO_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = EntregablesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = EntregablesApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = EntregablesApiMessages.EntregableProyectoGrado.CAMBIAR_ESTADO_RESP_422)
    })
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable UUID entregableProyectoGradoId,
            @RequestBody CambiarEstadoEntregableProyectoGradoRequestDTO request) {

        cambiarEstadoEntregableProyectoGradoInteractor.ejecutar(
                CambiarEstadoEntregableProyectoGradoRequestMapper.toCommand(entregableProyectoGradoId, request));
        return ResponseEntity.noContent().build();
    }
}
