package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.web;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.CancelarProyectoGradoInteractor;
import com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.web.dto.CancelarProyectoGradoRequestDTO;
import com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.web.mapper.CancelarProyectoGradoRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ProyectosApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("${rutas.proyectos.proyectos-grado.base:/proyectos-grado}")
@RequiredArgsConstructor
@Tag(name = ProyectosApiMessages.CancelacionProyectoGrado.TAG_NAME,
        description = ProyectosApiMessages.CancelacionProyectoGrado.TAG_DESCRIPTION)
public class CancelarProyectoGradoController {

    private final CancelarProyectoGradoInteractor cancelarProyectoGradoInteractor;

    @PatchMapping("${rutas.proyectos.proyectos-grado.cancelacion:/{proyectoGradoId}/cancelacion}")
    @Operation(
            summary = ProyectosApiMessages.CancelacionProyectoGrado.CANCELAR_SUMMARY,
            description = ProyectosApiMessages.CancelacionProyectoGrado.CANCELAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = ProyectosApiMessages.CancelacionProyectoGrado.CANCELAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ProyectosApiMessages.CancelacionProyectoGrado.CANCELAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = ProyectosApiMessages.CancelacionProyectoGrado.CANCELAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ProyectosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ProyectosApiMessages.CancelacionProyectoGrado.CANCELAR_RESP_403)
    })
    public ResponseEntity<Void> cancelar(
            @PathVariable UUID proyectoGradoId,
            @RequestBody CancelarProyectoGradoRequestDTO dto,
            IdentidadActual identidad) {

        cancelarProyectoGradoInteractor.ejecutar(
                CancelarProyectoGradoRequestMapper.toCommand(dto, proyectoGradoId, identidad.id()));

        return ResponseEntity.noContent().build();
    }
}
