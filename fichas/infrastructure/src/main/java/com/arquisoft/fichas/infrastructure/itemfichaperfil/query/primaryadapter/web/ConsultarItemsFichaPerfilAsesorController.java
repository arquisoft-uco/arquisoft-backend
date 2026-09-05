package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor.ConsultarItemsFichaPerfilAsesorInteractor;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.dto.ItemFichaPerfilResponseDTO;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.mapper.ConsultarItemsFichaPerfilAsesorRequestMapper;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.mapper.ItemFichaPerfilResponseMapper;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.ItemFichaPerfil.TAG_NAME,
        description = FichasApiMessages.ItemFichaPerfil.TAG_DESCRIPTION)
public class ConsultarItemsFichaPerfilAsesorController {

    private final ConsultarItemsFichaPerfilAsesorInteractor consultarItemsFichaPerfilAsesorInteractor;

    @GetMapping("${rutas.fichas.fichas-perfil.items:/{fichaPerfilId}/items}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ITEM_FICHA_PERFIL_ASESOR_VIEW)
    @Operation(
            summary = FichasApiMessages.ItemFichaPerfil.CONSULTAR_ASESOR_SUMMARY,
            description = FichasApiMessages.ItemFichaPerfil.CONSULTAR_ASESOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.ItemFichaPerfil.CONSULTAR_ASESOR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ItemFichaPerfilResponseDTO.class)))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.ItemFichaPerfil.CONSULTAR_ASESOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.ItemFichaPerfil.CONSULTAR_ASESOR_RESP_403)
    })
    public ResponseEntity<List<ItemFichaPerfilResponseDTO>> consultarItems(
            @PathVariable UUID fichaPerfilId,
            @AuthenticationPrincipal Jwt jwt) {

        var asesorFicha = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());

        var items = consultarItemsFichaPerfilAsesorInteractor.ejecutar(
                ConsultarItemsFichaPerfilAsesorRequestMapper.toQuery(fichaPerfilId, asesorFicha));

        return ResponseEntity.ok(items.stream()
                .map(ItemFichaPerfilResponseMapper::toResponse)
                .toList());
    }
}
