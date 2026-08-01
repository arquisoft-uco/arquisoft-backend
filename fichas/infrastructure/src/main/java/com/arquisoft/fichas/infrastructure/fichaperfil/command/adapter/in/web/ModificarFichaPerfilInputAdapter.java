package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.fichaperfil.command.port.in.ModificarFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto.ModificarFichaPerfilRequestDTO;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.infrastructure.web.FichasRoutes;
import com.arquisoft.shared.message.FichasApiDocs;
import com.arquisoft.shared.web.openapi.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping(FichasRoutes.FICHAS_PERFIL)
@RequiredArgsConstructor
@Tag(name = FichasApiDocs.FichaPerfil.TAG_NAME, description = FichasApiDocs.FichaPerfil.TAG_DESCRIPTION)
public class ModificarFichaPerfilInputAdapter {

    private final ModificarFichaPerfilInteractor modificarFichaPerfilInteractor;

    @PatchMapping("/{id}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_FICHA_PERFIL_UPDATE)
    @Operation(
            summary = FichasApiDocs.FichaPerfil.MODIFICAR_SUMMARY,
            description = FichasApiDocs.FichaPerfil.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiDocs.FichaPerfil.MODIFICAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiDocs.FichaPerfil.MODIFICAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiDocs.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiDocs.FichaPerfil.MODIFICAR_RESP_403)
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID id,
            @Valid @RequestBody ModificarFichaPerfilRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID estudianteId = UUID.fromString(jwt.getSubject());
        modificarFichaPerfilInteractor.ejecutar(request.toCommand(id, estudianteId));

        return ResponseEntity.noContent().build();
    }
}
