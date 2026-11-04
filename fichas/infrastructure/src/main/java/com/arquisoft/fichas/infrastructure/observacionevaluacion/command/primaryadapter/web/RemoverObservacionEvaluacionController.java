package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.RemoverObservacionEvaluacionInteractor;
import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.RemoverObservacionEvaluacionCommand;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.ObservacionEvaluacion.TAG_NAME,
        description = FichasApiMessages.ObservacionEvaluacion.TAG_DESCRIPTION)
public class RemoverObservacionEvaluacionController {

    private final RemoverObservacionEvaluacionInteractor removerObservacionEvaluacionInteractor;

    @DeleteMapping("${rutas.fichas.fichas-perfil.observacion-evaluacion-por-id:/observaciones-evaluacion/{observacionEvaluacionId}}")
    @Operation(
            summary = FichasApiMessages.ObservacionEvaluacion.REMOVER_SUMMARY,
            description = FichasApiMessages.ObservacionEvaluacion.REMOVER_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiMessages.ObservacionEvaluacion.REMOVER_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.ObservacionEvaluacion.REMOVER_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.ObservacionEvaluacion.REMOVER_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.ObservacionEvaluacion.REMOVER_RESP_422)
    })
    public ResponseEntity<Void> remover(
            @PathVariable UUID observacionEvaluacionId,
            IdentidadActual identidad) {

        var representanteComiteId = identidad.id();

        removerObservacionEvaluacionInteractor.ejecutar(
                RemoverObservacionEvaluacionCommand.crear(observacionEvaluacionId, representanteComiteId));

        return ResponseEntity.noContent().build();
    }
}
