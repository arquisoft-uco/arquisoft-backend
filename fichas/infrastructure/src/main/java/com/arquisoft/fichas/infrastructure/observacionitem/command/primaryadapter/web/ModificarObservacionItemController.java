package com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.ModificarObservacionItemInteractor;
import com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web.dto.ModificarObservacionItemRequestDTO;
import com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web.mapper.ModificarObservacionItemRequestMapper;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.shared.util.UtilUUID;
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
@Tag(name = FichasApiMessages.ObservacionItem.TAG_NAME,
        description = FichasApiMessages.ObservacionItem.TAG_DESCRIPTION)
public class ModificarObservacionItemController {

    private final ModificarObservacionItemInteractor modificarObservacionItemInteractor;

    @PatchMapping("${rutas.fichas.fichas-perfil.observacion-item-por-id:/observaciones-item/{observacionItemId}}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_OBSERVACION_ITEM_UPDATE)
    @Operation(
            summary = FichasApiMessages.ObservacionItem.MODIFICAR_SUMMARY,
            description = FichasApiMessages.ObservacionItem.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiMessages.ObservacionItem.MODIFICAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.ObservacionItem.MODIFICAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.ObservacionItem.MODIFICAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.ObservacionItem.MODIFICAR_RESP_422)
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID observacionItemId,
            @RequestBody ModificarObservacionItemRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        var asesorFichaId = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());

        modificarObservacionItemInteractor.ejecutar(
                ModificarObservacionItemRequestMapper.toCommand(dto, observacionItemId, asesorFichaId));

        return ResponseEntity.noContent().build();
    }
}
