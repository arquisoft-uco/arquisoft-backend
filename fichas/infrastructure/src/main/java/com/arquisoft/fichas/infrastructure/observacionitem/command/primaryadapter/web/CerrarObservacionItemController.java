package com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.CerrarObservacionItemInteractor;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.CerrarObservacionItemCommand;
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
public class CerrarObservacionItemController {

    private final CerrarObservacionItemInteractor cerrarObservacionItemInteractor;

    @PatchMapping("${rutas.fichas.fichas-perfil.observacion-item-cierre:/observaciones-item/{observacionItemId}/cierre}")
    @Operation(
            summary = FichasApiMessages.ObservacionItem.CERRAR_SUMMARY,
            description = FichasApiMessages.ObservacionItem.CERRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiMessages.ObservacionItem.CERRAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.ObservacionItem.CERRAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.ObservacionItem.CERRAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.ObservacionItem.CERRAR_RESP_422)
    })
    public ResponseEntity<Void> cerrar(
            @PathVariable UUID observacionItemId,
            IdentidadActual identidad) {
        var command = CerrarObservacionItemCommand.crear(observacionItemId, identidad.id());
        cerrarObservacionItemInteractor.ejecutar(command);
        return ResponseEntity.noContent().build();
    }
}
