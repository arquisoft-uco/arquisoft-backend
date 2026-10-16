package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.ConsultarEvaluacionesFichaPerfilEstudianteInteractor;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.dto.EvaluacionFichaPerfilEstudianteResponseDTO;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper.ConsultarEvaluacionesFichaPerfilEstudianteRequestMapper;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper.EvaluacionFichaPerfilEstudianteResponseMapper;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.EvaluacionFichaPerfil.TAG_NAME,
        description = FichasApiMessages.EvaluacionFichaPerfil.TAG_DESCRIPTION)
public class ConsultarEvaluacionesFichaPerfilEstudianteController {

    private final ConsultarEvaluacionesFichaPerfilEstudianteInteractor consultarEvaluacionesFichaPerfilEstudianteInteractor;

    @GetMapping("${rutas.fichas.fichas-perfil.evaluaciones-estudiante:/{fichaPerfilId}/evaluaciones/estudiante}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_EVALUACION_FICHA_PERFIL_ESTUDIANTE_VIEW)
    @Operation(
            summary = FichasApiMessages.EvaluacionFichaPerfil.CONSULTAR_ESTUDIANTE_SUMMARY,
            description = FichasApiMessages.EvaluacionFichaPerfil.CONSULTAR_ESTUDIANTE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.EvaluacionFichaPerfil.CONSULTAR_ESTUDIANTE_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EvaluacionFichaPerfilEstudianteResponseDTO.class)))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.EvaluacionFichaPerfil.CONSULTAR_ESTUDIANTE_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.EvaluacionFichaPerfil.CONSULTAR_ESTUDIANTE_RESP_403)
    })
    public ResponseEntity<List<EvaluacionFichaPerfilEstudianteResponseDTO>> consultarEvaluaciones(
            @PathVariable UUID fichaPerfilId,
            @AuthenticationPrincipal Jwt jwt) {

        var estudiante = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());

        var evaluaciones = consultarEvaluacionesFichaPerfilEstudianteInteractor.ejecutar(
                ConsultarEvaluacionesFichaPerfilEstudianteRequestMapper.toQuery(fichaPerfilId, estudiante));

        return ResponseEntity.ok(evaluaciones.stream()
                .map(EvaluacionFichaPerfilEstudianteResponseMapper::toResponse)
                .toList());
    }
}
