package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.ConsultarFichaPerfilEstudianteInteractor;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.dto.FichaPerfilEstudianteResponseDTO;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper.ConsultarFichaPerfilEstudianteRequestMapper;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper.FichaPerfilEstudianteResponseMapper;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.FichaPerfil.TAG_NAME, description = FichasApiMessages.FichaPerfil.TAG_DESCRIPTION)
public class ConsultarFichaPerfilEstudianteController {

    private final ConsultarFichaPerfilEstudianteInteractor consultarFichaPerfilEstudianteInteractor;

    @GetMapping("${rutas.fichas.fichas-perfil.ficha-estudiante:/{fichaPerfilId}/estudiante}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_FICHA_PERFIL_ESTUDIANTE_VIEW)
    @Operation(
            summary = FichasApiMessages.FichaPerfil.CONSULTAR_ESTUDIANTE_SUMMARY,
            description = FichasApiMessages.FichaPerfil.CONSULTAR_ESTUDIANTE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.FichaPerfil.CONSULTAR_ESTUDIANTE_RESP_200,
                    content = @Content(schema = @Schema(implementation = FichaPerfilEstudianteResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.NOT_FOUND,
                    description = FichasApiMessages.FichaPerfil.CONSULTAR_ESTUDIANTE_RESP_404),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED, description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.FichaPerfil.CONSULTAR_ESTUDIANTE_RESP_403)
    })
    public ResponseEntity<FichaPerfilEstudianteResponseDTO> consultarFichaPerfil(
            @PathVariable UUID fichaPerfilId,
            @AuthenticationPrincipal Jwt jwt) {

        var estudiante = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());

        return consultarFichaPerfilEstudianteInteractor
                .ejecutar(ConsultarFichaPerfilEstudianteRequestMapper.toQuery(fichaPerfilId, estudiante))
                .map(FichaPerfilEstudianteResponseMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
