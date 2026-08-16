package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.primaryadapter.web;

import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.primaryadapter.web.mapper.AgregarItemFichaPerfilRequestMapper;
import com.arquisoft.shared.message.annotation.FichasApiKeys;
import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.interactor.AgregarItemFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.primaryadapter.web.dto.AgregarItemFichaPerfilRequestDTO;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.primaryadapter.web.dto.AgregarItemFichaPerfilResponseDTO;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.infrastructure.web.FichasRoutes;
import com.arquisoft.shared.web.openapi.ApiCodes;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiKeys.ItemFichaPerfil.TAG_NAME,
        description = FichasApiKeys.ItemFichaPerfil.TAG_DESCRIPTION)
public class AgregarItemFichaPerfilController {

    private final AgregarItemFichaPerfilInteractor agregarItemFichaPerfilInteractor;

    @PostMapping("${rutas.fichas.fichas-perfil.items:/{fichaPerfilId}/items}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ITEM_FICHA_PERFIL_CREATE)
    @Operation(
            summary = FichasApiKeys.ItemFichaPerfil.AGREGAR_SUMMARY,
            description = FichasApiKeys.ItemFichaPerfil.AGREGAR_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = FichasApiKeys.ItemFichaPerfil.AGREGAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = AgregarItemFichaPerfilResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiKeys.ItemFichaPerfil.AGREGAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiKeys.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiKeys.ItemFichaPerfil.AGREGAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiKeys.ItemFichaPerfil.AGREGAR_RESP_422)
    })
    public ResponseEntity<AgregarItemFichaPerfilResponseDTO> agregarItem(
            @PathVariable UUID fichaPerfilId,
            @RequestBody AgregarItemFichaPerfilRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        var estudianteId = UUID.fromString(jwt.getSubject());

        UUID itemId = agregarItemFichaPerfilInteractor.ejecutar(AgregarItemFichaPerfilRequestMapper.toCommand(dto, fichaPerfilId, estudianteId));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AgregarItemFichaPerfilResponseDTO(itemId));
    }
}
