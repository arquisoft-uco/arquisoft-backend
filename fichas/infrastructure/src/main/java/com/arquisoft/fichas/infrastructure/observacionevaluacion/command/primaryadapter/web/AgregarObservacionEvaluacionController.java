package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.AgregarObservacionEvaluacionInteractor;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web.dto.AgregarObservacionEvaluacionRequestDTO;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web.dto.AgregarObservacionEvaluacionResponseDTO;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web.mapper.AgregarObservacionEvaluacionRequestMapper;
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
@Tag(name = FichasApiMessages.ObservacionEvaluacion.TAG_NAME,
        description = FichasApiMessages.ObservacionEvaluacion.TAG_DESCRIPTION)
public class AgregarObservacionEvaluacionController {

    private final AgregarObservacionEvaluacionInteractor agregarObservacionEvaluacionInteractor;

    @PostMapping("${rutas.fichas.fichas-perfil.evaluacion-observaciones:/evaluaciones/{evaluacionFichaPerfilId}/observaciones}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_OBSERVACION_EVALUACION_CREATE)
    @Operation(
            summary = FichasApiMessages.ObservacionEvaluacion.AGREGAR_SUMMARY,
            description = FichasApiMessages.ObservacionEvaluacion.AGREGAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = FichasApiMessages.ObservacionEvaluacion.AGREGAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = AgregarObservacionEvaluacionResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.ObservacionEvaluacion.AGREGAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.ObservacionEvaluacion.AGREGAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.ObservacionEvaluacion.AGREGAR_RESP_422)
    })
    public ResponseEntity<AgregarObservacionEvaluacionResponseDTO> agregar(
            @PathVariable UUID evaluacionFichaPerfilId,
            @RequestBody AgregarObservacionEvaluacionRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        var representanteComiteId = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());

        var id = agregarObservacionEvaluacionInteractor.ejecutar(
                AgregarObservacionEvaluacionRequestMapper.toCommand(dto, evaluacionFichaPerfilId, representanteComiteId));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AgregarObservacionEvaluacionResponseDTO(id));
    }
}
