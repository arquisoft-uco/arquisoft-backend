package com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web;

import com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.mapper.ModificarFichaPerfilRequestMapper;
import com.arquisoft.shared.message.annotation.FichasApiKeys;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.ModificarFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.dto.ModificarFichaPerfilRequestDTO;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.infrastructure.web.FichasRoutes;
import com.arquisoft.shared.web.openapi.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiKeys.FichaPerfil.TAG_NAME, description = FichasApiKeys.FichaPerfil.TAG_DESCRIPTION)
public class ModificarFichaPerfilController {

    private final ModificarFichaPerfilInteractor modificarFichaPerfilInteractor;

    @PatchMapping("${rutas.fichas.fichas-perfil.por-id:/{id}}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_FICHA_PERFIL_UPDATE)
    @Operation(
            summary = FichasApiKeys.FichaPerfil.MODIFICAR_SUMMARY,
            description = FichasApiKeys.FichaPerfil.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiKeys.FichaPerfil.MODIFICAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiKeys.FichaPerfil.MODIFICAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiKeys.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiKeys.FichaPerfil.MODIFICAR_RESP_403)
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID id,
            @RequestBody ModificarFichaPerfilRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        var estudianteId = UUID.fromString(jwt.getSubject());
        modificarFichaPerfilInteractor.ejecutar(ModificarFichaPerfilRequestMapper.toCommand(request, id, estudianteId));

        return ResponseEntity.noContent().build();
    }
}
