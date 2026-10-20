package com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web;

import com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.ModificarFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.dto.ModificarFichaPerfilRequestDTO;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.mapper.ModificarFichaPerfilRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.shared.web.identidad.IdentidadActual;
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
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.FichaPerfil.TAG_NAME, description = FichasApiMessages.FichaPerfil.TAG_DESCRIPTION)
public class ModificarFichaPerfilController {

    private final ModificarFichaPerfilInteractor modificarFichaPerfilInteractor;

    @PatchMapping("${rutas.fichas.fichas-perfil.por-id:/{id}}")
    @Operation(
            summary = FichasApiMessages.FichaPerfil.MODIFICAR_SUMMARY,
            description = FichasApiMessages.FichaPerfil.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiMessages.FichaPerfil.MODIFICAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.FichaPerfil.MODIFICAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.FichaPerfil.MODIFICAR_RESP_403)
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID id,
            @RequestBody ModificarFichaPerfilRequestDTO request,
            IdentidadActual identidad) {

        var estudianteId = identidad.id();
        modificarFichaPerfilInteractor.ejecutar(ModificarFichaPerfilRequestMapper.toCommand(request, id, estudianteId));

        return ResponseEntity.noContent().build();
    }
}
