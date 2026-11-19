package com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.ReabrirObservacionItemInteractor;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.ReabrirObservacionItemCommand;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.ObservacionItem.TAG_NAME,
        description = FichasApiMessages.ObservacionItem.TAG_DESCRIPTION)
public class ReabrirObservacionItemController {

    private final ReabrirObservacionItemInteractor reabrirObservacionItemInteractor;

    @PatchMapping("${rutas.fichas.fichas-perfil.observacion-item-reapertura:/observaciones-item/{observacionItemId}/reapertura}")
    @Operation(
            summary = FichasApiMessages.ObservacionItem.REABRIR_SUMMARY,
            description = FichasApiMessages.ObservacionItem.REABRIR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiMessages.ObservacionItem.REABRIR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.ObservacionItem.REABRIR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.ObservacionItem.REABRIR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.ObservacionItem.REABRIR_RESP_422)
    })
    public ResponseEntity<Void> reabrir(
            @PathVariable UUID observacionItemId,
            IdentidadActual identidad) {
        var command = ReabrirObservacionItemCommand.crear(observacionItemId, identidad.id());
        reabrirObservacionItemInteractor.ejecutar(command);
        return ResponseEntity.noContent().build();
    }
}
