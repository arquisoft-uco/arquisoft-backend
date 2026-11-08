package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.web;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.CambiarEstadoProyectoGradoInteractor;
import com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.web.dto.CambiarEstadoProyectoGradoRequestDTO;
import com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.web.mapper.CambiarEstadoProyectoGradoRequestMapper;
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
@Tag(name = ProyectosApiMessages.CambioEstadoProyectoGrado.TAG_NAME,
        description = ProyectosApiMessages.CambioEstadoProyectoGrado.TAG_DESCRIPTION)
public class CambiarEstadoProyectoGradoController {

    private final CambiarEstadoProyectoGradoInteractor cambiarEstadoProyectoGradoInteractor;

    @PatchMapping("${rutas.proyectos.proyectos-grado.estado:/{proyectoGradoId}/estado}")
    @Operation(
            summary = ProyectosApiMessages.CambioEstadoProyectoGrado.CAMBIAR_ESTADO_SUMMARY,
            description = ProyectosApiMessages.CambioEstadoProyectoGrado.CAMBIAR_ESTADO_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = ProyectosApiMessages.CambioEstadoProyectoGrado.CAMBIAR_ESTADO_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ProyectosApiMessages.CambioEstadoProyectoGrado.CAMBIAR_ESTADO_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = ProyectosApiMessages.CambioEstadoProyectoGrado.CAMBIAR_ESTADO_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ProyectosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ProyectosApiMessages.CambioEstadoProyectoGrado.CAMBIAR_ESTADO_RESP_403)
    })
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable UUID proyectoGradoId,
            @RequestBody CambiarEstadoProyectoGradoRequestDTO dto,
            IdentidadActual identidad) {

        cambiarEstadoProyectoGradoInteractor.ejecutar(
                CambiarEstadoProyectoGradoRequestMapper.toCommand(dto, proyectoGradoId, identidad.id()));

        return ResponseEntity.noContent().build();
    }
}
