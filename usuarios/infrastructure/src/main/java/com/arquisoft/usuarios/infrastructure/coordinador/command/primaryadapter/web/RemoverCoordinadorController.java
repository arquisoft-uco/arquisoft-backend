package com.arquisoft.usuarios.infrastructure.coordinador.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.UsuariosApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.usuarios.application.coordinador.command.primaryport.interactor.RemoverCoordinadorInteractor;
import com.arquisoft.usuarios.infrastructure.coordinador.command.primaryadapter.web.mapper.RemoverCoordinadorRequestMapper;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.usuarios.usuarios.base:/usuarios}")
@RequiredArgsConstructor
@Tag(name = UsuariosApiMessages.Usuario.TAG_NAME, description = UsuariosApiMessages.Usuario.TAG_DESCRIPTION)
public class RemoverCoordinadorController {

    private final RemoverCoordinadorInteractor removerCoordinadorInteractor;

    @DeleteMapping("${rutas.usuarios.usuarios.coordinador:/{usuarioId}/coordinador}")
    @PreAuthorize(UsuariosAuthorities.Expresiones.HAS_COORDINADOR_DELETE)
    @Operation(
            summary = UsuariosApiMessages.Coordinador.REMOVER_SUMMARY,
            description = UsuariosApiMessages.Coordinador.REMOVER_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = UsuariosApiMessages.Coordinador.REMOVER_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = UsuariosApiMessages.Coordinador.REMOVER_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = UsuariosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = UsuariosApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = UsuariosApiMessages.Coordinador.REMOVER_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.SERVICE_UNAVAILABLE,
                    description = UsuariosApiMessages.Coordinador.REMOVER_RESP_503,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> remover(@PathVariable UUID usuarioId) {
        removerCoordinadorInteractor.ejecutar(RemoverCoordinadorRequestMapper.toCommand(usuarioId));
        return ResponseEntity.noContent().build();
    }
}
