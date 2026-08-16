package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.primaryadapter.web;

import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.primaryadapter.web.mapper.ModificarItemFichaPerfilRequestMapper;
import com.arquisoft.shared.message.annotation.FichasApiKeys;
import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.interactor.ModificarItemFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.primaryadapter.web.dto.ModificarItemFichaPerfilRequestDTO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiKeys.ItemFichaPerfil.TAG_NAME,
        description = FichasApiKeys.ItemFichaPerfil.TAG_DESCRIPTION)
public class ModificarItemFichaPerfilController {

    private final ModificarItemFichaPerfilInteractor modificarItemFichaPerfilInteractor;

    @PatchMapping("${rutas.fichas.fichas-perfil.item-por-id:/items/{itemId}}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ITEM_FICHA_PERFIL_UPDATE)
    @Operation(
            summary = FichasApiKeys.ItemFichaPerfil.MODIFICAR_SUMMARY,
            description = FichasApiKeys.ItemFichaPerfil.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiKeys.ItemFichaPerfil.MODIFICAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiKeys.ItemFichaPerfil.MODIFICAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiKeys.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiKeys.ItemFichaPerfil.MODIFICAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiKeys.ItemFichaPerfil.MODIFICAR_RESP_422)
    })
    public ResponseEntity<Void> modificarItem(
            @PathVariable UUID itemId,
            @RequestBody ModificarItemFichaPerfilRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        var estudianteId = UUID.fromString(jwt.getSubject());

        modificarItemFichaPerfilInteractor.ejecutar(ModificarItemFichaPerfilRequestMapper.toCommand(dto, itemId, estudianteId));

        return ResponseEntity.noContent().build();
    }
}
