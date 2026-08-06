package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.in.web;

import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.in.web.mapper.AgregarEstadoEvaluacionFichaRequestMapper;
import com.arquisoft.shared.message.annotation.FichasApiKeys;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.interactor.AgregarEstadoEvaluacionFichaInteractor;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.in.web.dto.AgregarEstadoEvaluacionFichaRequestDTO;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.in.web.dto.AgregarEstadoEvaluacionFichaResponseDTO;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.infrastructure.web.FichasRoutes;
import com.arquisoft.shared.web.openapi.ApiCodes;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.estado-evaluacion-ficha.base:/fichas-perfil/estado-evaluacion-ficha}")
@RequiredArgsConstructor
@Tag(name = FichasApiKeys.EstadoEvaluacionFicha.TAG_NAME,
        description = FichasApiKeys.EstadoEvaluacionFicha.TAG_DESCRIPTION)
public class AgregarEstadoEvaluacionFichaControlador {

    private final AgregarEstadoEvaluacionFichaInteractor agregarEstadoEvaluacionFichaInteractor;

    @PostMapping
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ESTADO_EVALUACION_FICHA_CREATE)
    @Operation(
            summary = FichasApiKeys.EstadoEvaluacionFicha.AGREGAR_SUMMARY,
            description = FichasApiKeys.EstadoEvaluacionFicha.AGREGAR_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = FichasApiKeys.EstadoEvaluacionFicha.AGREGAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = AgregarEstadoEvaluacionFichaResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiKeys.EstadoEvaluacionFicha.AGREGAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiKeys.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiKeys.EstadoEvaluacionFicha.AGREGAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiKeys.EstadoEvaluacionFicha.AGREGAR_RESP_422)
    })
    public ResponseEntity<AgregarEstadoEvaluacionFichaResponseDTO> agregar(
            @RequestBody AgregarEstadoEvaluacionFichaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID representanteComiteId = UUID.fromString(jwt.getSubject());
        var id = agregarEstadoEvaluacionFichaInteractor.ejecutar(AgregarEstadoEvaluacionFichaRequestMapper.toCommand(request, representanteComiteId));

        return ResponseEntity.status(HttpStatus.CREATED).body(new AgregarEstadoEvaluacionFichaResponseDTO(id));
    }
}
