package com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.AgregarObservacionItemInteractor;
import com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web.dto.AgregarObservacionItemRequestDTO;
import com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web.dto.AgregarObservacionItemResponseDTO;
import com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web.mapper.AgregarObservacionItemRequestMapper;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.shared.util.UtilUUID;
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
@Tag(name = FichasApiMessages.ObservacionItem.TAG_NAME,
        description = FichasApiMessages.ObservacionItem.TAG_DESCRIPTION)
public class AgregarObservacionItemController {

    private final AgregarObservacionItemInteractor agregarObservacionItemInteractor;

    @PostMapping("${rutas.fichas.fichas-perfil.revision-observaciones:/revisiones/{revisionItemId}/observaciones}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_OBSERVACION_ITEM_CREATE)
    @Operation(
            summary = FichasApiMessages.ObservacionItem.AGREGAR_SUMMARY,
            description = FichasApiMessages.ObservacionItem.AGREGAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = FichasApiMessages.ObservacionItem.AGREGAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = AgregarObservacionItemResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.ObservacionItem.AGREGAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.ObservacionItem.AGREGAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.ObservacionItem.AGREGAR_RESP_422)
    })
    public ResponseEntity<AgregarObservacionItemResponseDTO> agregar(
            @PathVariable UUID revisionItemId,
            @RequestBody AgregarObservacionItemRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        var asesorFichaId = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());

        var id = agregarObservacionItemInteractor.ejecutar(
                AgregarObservacionItemRequestMapper.toCommand(dto, revisionItemId, asesorFichaId));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AgregarObservacionItemResponseDTO(id));
    }
}
