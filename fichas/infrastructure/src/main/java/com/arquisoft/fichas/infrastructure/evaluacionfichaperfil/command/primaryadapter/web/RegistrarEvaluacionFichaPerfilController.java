package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.interactor.RegistrarEvaluacionFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.primaryadapter.web.dto.RegistrarEvaluacionFichaPerfilResponseDTO;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ApiCodes;
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
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.EvaluacionFichaPerfil.TAG_NAME,
        description = FichasApiMessages.EvaluacionFichaPerfil.TAG_DESCRIPTION)
public class RegistrarEvaluacionFichaPerfilController {

    private final RegistrarEvaluacionFichaPerfilInteractor registrarEvaluacionFichaPerfilInteractor;

    @PostMapping("${rutas.fichas.fichas-perfil.evaluaciones:/{fichaId}/evaluaciones}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_EVALUACION_FICHA_PERFIL_CREATE)
    @Operation(
            summary = FichasApiMessages.EvaluacionFichaPerfil.REGISTRAR_SUMMARY,
            description = FichasApiMessages.EvaluacionFichaPerfil.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = FichasApiMessages.EvaluacionFichaPerfil.REGISTRAR_RESP_201),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.EvaluacionFichaPerfil.REGISTRAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<RegistrarEvaluacionFichaPerfilResponseDTO> registrarEvaluacion(
            @PathVariable UUID fichaId,
            @AuthenticationPrincipal Jwt jwt) {

        var representanteComiteId = UUID.fromString(jwt.getSubject());

        var command = RegistrarEvaluacionFichaPerfilCommand.crear(
                fichaId,
                representanteComiteId);

        UUID evaluacionId = registrarEvaluacionFichaPerfilInteractor.ejecutar(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrarEvaluacionFichaPerfilResponseDTO(evaluacionId));
    }
}
