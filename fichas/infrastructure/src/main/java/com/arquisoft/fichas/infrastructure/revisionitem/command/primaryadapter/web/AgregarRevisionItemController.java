package com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.AgregarRevisionItemInteractor;
import com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web.dto.AgregarRevisionItemResponseDTO;
import com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web.mapper.AgregarRevisionItemRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.shared.web.identidad.IdentidadActual;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.RevisionItem.TAG_NAME,
        description = FichasApiMessages.RevisionItem.TAG_DESCRIPTION)
public class AgregarRevisionItemController {

    private final AgregarRevisionItemInteractor agregarRevisionItemInteractor;

    @PostMapping("${rutas.fichas.fichas-perfil.item-revisiones:/items/{itemId}/revisiones}")
    @Operation(
            summary = FichasApiMessages.RevisionItem.AGREGAR_SUMMARY,
            description = FichasApiMessages.RevisionItem.AGREGAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = FichasApiMessages.RevisionItem.AGREGAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = AgregarRevisionItemResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.RevisionItem.AGREGAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.RevisionItem.AGREGAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.RevisionItem.AGREGAR_RESP_422)
    })
    public ResponseEntity<AgregarRevisionItemResponseDTO> agregar(
            @PathVariable UUID itemId,
            IdentidadActual identidad) {

        var asesorFichaId = identidad.id();

        UUID id = agregarRevisionItemInteractor.ejecutar(
                AgregarRevisionItemRequestMapper.toCommand(itemId, asesorFichaId));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AgregarRevisionItemResponseDTO(id));
    }
}
