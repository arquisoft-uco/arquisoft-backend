package com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.RemoverRevisionItemInteractor;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.RemoverRevisionItemCommand;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.RevisionItem.TAG_NAME,
        description = FichasApiMessages.RevisionItem.TAG_DESCRIPTION)
public class RemoverRevisionItemController {

    private final RemoverRevisionItemInteractor removerRevisionItemInteractor;

    @DeleteMapping("${rutas.fichas.fichas-perfil.revision-por-id:/revisiones/{revisionItemId}}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_REVISION_ITEM_DELETE)
    @Operation(
            summary = FichasApiMessages.RevisionItem.REMOVER_SUMMARY,
            description = FichasApiMessages.RevisionItem.REMOVER_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiMessages.RevisionItem.REMOVER_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.RevisionItem.REMOVER_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.RevisionItem.REMOVER_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.RevisionItem.REMOVER_RESP_422)
    })
    public ResponseEntity<Void> remover(
            @PathVariable UUID revisionItemId,
            @AuthenticationPrincipal Jwt jwt) {
        var asesorFichaId = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        var command = RemoverRevisionItemCommand.crear(revisionItemId, asesorFichaId);
        removerRevisionItemInteractor.ejecutar(command);
        return ResponseEntity.noContent().build();
    }
}
