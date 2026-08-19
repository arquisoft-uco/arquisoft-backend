package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.interactor.RemoverItemFichaPerfilInteractor;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.ItemFichaPerfil.TAG_NAME,
        description = FichasApiMessages.ItemFichaPerfil.TAG_DESCRIPTION)
public class RemoverItemFichaPerfilController {

    private final RemoverItemFichaPerfilInteractor removerItemFichaPerfilInteractor;

    @DeleteMapping("${rutas.fichas.fichas-perfil.item-por-id:/items/{itemId}}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ITEM_FICHA_PERFIL_DELETE)
    @Operation(
            summary = FichasApiMessages.ItemFichaPerfil.REMOVER_SUMMARY,
            description = FichasApiMessages.ItemFichaPerfil.REMOVER_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiMessages.ItemFichaPerfil.REMOVER_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.ItemFichaPerfil.REMOVER_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.ItemFichaPerfil.REMOVER_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.ItemFichaPerfil.REMOVER_RESP_422)
    })
    public ResponseEntity<Void> remover(
            @PathVariable UUID itemId,
            @AuthenticationPrincipal Jwt jwt) {
        var estudianteId = UUID.fromString(jwt.getSubject());
        var command = RemoverItemFichaPerfilCommand.crear(itemId, estudianteId);
        removerItemFichaPerfilInteractor.ejecutar(command);
        return ResponseEntity.noContent().build();
    }
}
