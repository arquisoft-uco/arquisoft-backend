package com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.EnviarCorreccionesRevisionItemInteractor;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.EnviarCorreccionesRevisionItemCommand;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
public class EnviarCorreccionesRevisionItemController {

    private final EnviarCorreccionesRevisionItemInteractor enviarCorreccionesRevisionItemInteractor;

    @PatchMapping("${rutas.fichas.fichas-perfil.revision-correcciones:/revisiones/{revisionItemId}/correcciones}")
    @Operation(
            summary = FichasApiMessages.RevisionItem.ENVIAR_CORRECCIONES_SUMMARY,
            description = FichasApiMessages.RevisionItem.ENVIAR_CORRECCIONES_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiMessages.RevisionItem.ENVIAR_CORRECCIONES_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.RevisionItem.ENVIAR_CORRECCIONES_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.RevisionItem.ENVIAR_CORRECCIONES_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.RevisionItem.ENVIAR_CORRECCIONES_RESP_422)
    })
    public ResponseEntity<Void> enviarCorrecciones(
            @PathVariable UUID revisionItemId,
            IdentidadActual identidad) {
        var estudianteId = identidad.id();
        var command = EnviarCorreccionesRevisionItemCommand.crear(revisionItemId, estudianteId);
        enviarCorreccionesRevisionItemInteractor.ejecutar(command);
        return ResponseEntity.noContent().build();
    }
}
