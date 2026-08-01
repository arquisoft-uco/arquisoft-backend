package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.AgregarItemFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto.AgregarItemFichaPerfilRequestDTO;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto.AgregarItemFichaPerfilResponseDTO;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.infrastructure.web.FichasRoutes;
import com.arquisoft.shared.message.FichasApiDocs;
import com.arquisoft.shared.web.openapi.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping(FichasRoutes.FICHAS_PERFIL)
@RequiredArgsConstructor
@Tag(name = FichasApiDocs.ItemFichaPerfil.TAG_NAME,
        description = FichasApiDocs.ItemFichaPerfil.TAG_DESCRIPTION)
public class AgregarItemFichaPerfilInputAdapter {

    private final AgregarItemFichaPerfilInteractor agregarItemFichaPerfilInteractor;

    @PostMapping("/{fichaPerfilId}/items")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ITEM_FICHA_PERFIL_CREATE)
    @Operation(
            summary = FichasApiDocs.ItemFichaPerfil.AGREGAR_SUMMARY,
            description = FichasApiDocs.ItemFichaPerfil.AGREGAR_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = FichasApiDocs.ItemFichaPerfil.AGREGAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = AgregarItemFichaPerfilResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiDocs.ItemFichaPerfil.AGREGAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiDocs.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiDocs.ItemFichaPerfil.AGREGAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiDocs.ItemFichaPerfil.AGREGAR_RESP_422)
    })
    public ResponseEntity<AgregarItemFichaPerfilResponseDTO> agregarItem(
            @PathVariable UUID fichaPerfilId,
            @Valid @RequestBody AgregarItemFichaPerfilRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        UUID estudianteId = UUID.fromString(jwt.getSubject());

        UUID itemId = agregarItemFichaPerfilInteractor.ejecutar(dto.toCommand(fichaPerfilId, estudianteId));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AgregarItemFichaPerfilResponseDTO(itemId));
    }
}
