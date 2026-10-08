package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.ModificarObservacionEvaluacionInteractor;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web.dto.ModificarObservacionEvaluacionRequestDTO;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web.mapper.ModificarObservacionEvaluacionRequestMapper;
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
@Tag(name = FichasApiMessages.ObservacionEvaluacion.TAG_NAME,
        description = FichasApiMessages.ObservacionEvaluacion.TAG_DESCRIPTION)
public class ModificarObservacionEvaluacionController {

    private final ModificarObservacionEvaluacionInteractor modificarObservacionEvaluacionInteractor;

    @PatchMapping("${rutas.fichas.fichas-perfil.observacion-evaluacion-por-id:/observaciones-evaluacion/{observacionEvaluacionId}}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_OBSERVACION_EVALUACION_UPDATE)
    @Operation(
            summary = FichasApiMessages.ObservacionEvaluacion.MODIFICAR_SUMMARY,
            description = FichasApiMessages.ObservacionEvaluacion.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiMessages.ObservacionEvaluacion.MODIFICAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.ObservacionEvaluacion.MODIFICAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.ObservacionEvaluacion.MODIFICAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.ObservacionEvaluacion.MODIFICAR_RESP_422)
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID observacionEvaluacionId,
            @RequestBody ModificarObservacionEvaluacionRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        var representanteComiteId = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());

        modificarObservacionEvaluacionInteractor.ejecutar(
                ModificarObservacionEvaluacionRequestMapper.toCommand(dto, observacionEvaluacionId, representanteComiteId));

        return ResponseEntity.noContent().build();
    }
}
