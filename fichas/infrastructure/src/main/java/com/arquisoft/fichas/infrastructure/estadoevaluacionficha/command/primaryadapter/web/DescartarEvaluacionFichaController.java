package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.primaryadapter.web;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.interactor.DescartarEvaluacionFichaInteractor;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.model.DescartarEvaluacionFichaCommand;
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
@Tag(name = FichasApiMessages.EstadoEvaluacionFicha.TAG_NAME,
        description = FichasApiMessages.EstadoEvaluacionFicha.TAG_DESCRIPTION)
public class DescartarEvaluacionFichaController {

    private final DescartarEvaluacionFichaInteractor descartarEvaluacionFichaInteractor;

    @PatchMapping("${rutas.fichas.fichas-perfil.evaluacion-descarte:/evaluaciones/{evaluacionFichaPerfilId}/descarte}")
    @Operation(
            summary = FichasApiMessages.EstadoEvaluacionFicha.DESCARTAR_SUMMARY,
            description = FichasApiMessages.EstadoEvaluacionFicha.DESCARTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiMessages.EstadoEvaluacionFicha.DESCARTAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.EstadoEvaluacionFicha.DESCARTAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.EstadoEvaluacionFicha.DESCARTAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.EstadoEvaluacionFicha.DESCARTAR_RESP_422)
    })
    public ResponseEntity<Void> descartar(
            @PathVariable UUID evaluacionFichaPerfilId,
            IdentidadActual identidad) {

        var representanteComiteId = identidad.id();

        descartarEvaluacionFichaInteractor.ejecutar(DescartarEvaluacionFichaCommand.crear(evaluacionFichaPerfilId, representanteComiteId));

        return ResponseEntity.noContent().build();
    }
}
