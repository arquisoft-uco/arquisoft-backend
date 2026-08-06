package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.adapter.in.web;

import com.arquisoft.shared.message.FichasApiKeys;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.interactor.RegistrarEvaluacionFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.adapter.in.web.dto.RegistrarEvaluacionFichaPerfilResponseDTO;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.infrastructure.web.FichasRoutes;
import com.arquisoft.shared.web.openapi.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(FichasRoutes.FICHAS_PERFIL)
@RequiredArgsConstructor
@Tag(name = FichasApiKeys.EvaluacionFichaPerfil.TAG_NAME,
        description = FichasApiKeys.EvaluacionFichaPerfil.TAG_DESCRIPTION)
public class RegistrarEvaluacionFichaPerfilInputAdapter {

    private final RegistrarEvaluacionFichaPerfilInteractor registrarEvaluacionFichaPerfilInteractor;

    @PostMapping("/{fichaId}/evaluaciones")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_EVALUACION_FICHA_PERFIL_CREATE)
    @Operation(
            summary = FichasApiKeys.EvaluacionFichaPerfil.REGISTRAR_SUMMARY,
            description = FichasApiKeys.EvaluacionFichaPerfil.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = FichasApiKeys.EvaluacionFichaPerfil.REGISTRAR_RESP_201),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiKeys.EvaluacionFichaPerfil.REGISTRAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiKeys.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiKeys.Comun.RESP_403)
    })
    public ResponseEntity<RegistrarEvaluacionFichaPerfilResponseDTO> registrarEvaluacion(
            @PathVariable UUID fichaId,
            @AuthenticationPrincipal Jwt jwt) {

        UUID representanteComiteId = UUID.fromString(jwt.getSubject());

        var command = RegistrarEvaluacionFichaPerfilCommand.crear(
                fichaId,
                representanteComiteId);

        UUID evaluacionId = registrarEvaluacionFichaPerfilInteractor.ejecutar(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrarEvaluacionFichaPerfilResponseDTO(evaluacionId));
    }
}
