package com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.MarcarRevisionItemComoVisualizadaInteractor;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.MarcarRevisionItemComoVisualizadaCommand;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.RevisionItem.TAG_NAME,
        description = FichasApiMessages.RevisionItem.TAG_DESCRIPTION)
public class MarcarRevisionItemComoVisualizadaController {

    private final MarcarRevisionItemComoVisualizadaInteractor marcarRevisionItemComoVisualizadaInteractor;

    @PatchMapping("${rutas.fichas.fichas-perfil.revision-visualizada:/revisiones/{revisionItemId}/visualizada}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_REVISION_ITEM_VISUALIZADA_UPDATE)
    @Operation(
            summary = FichasApiMessages.RevisionItem.VISUALIZAR_SUMMARY,
            description = FichasApiMessages.RevisionItem.VISUALIZAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiMessages.RevisionItem.VISUALIZAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.RevisionItem.VISUALIZAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.RevisionItem.VISUALIZAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.RevisionItem.VISUALIZAR_RESP_422)
    })
    public ResponseEntity<Void> marcarVisualizada(
            @PathVariable UUID revisionItemId,
            @AuthenticationPrincipal Jwt jwt) {
        var estudianteId = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        var command = MarcarRevisionItemComoVisualizadaCommand.crear(revisionItemId, estudianteId);
        marcarRevisionItemComoVisualizadaInteractor.ejecutar(command);
        return ResponseEntity.noContent().build();
    }
}
