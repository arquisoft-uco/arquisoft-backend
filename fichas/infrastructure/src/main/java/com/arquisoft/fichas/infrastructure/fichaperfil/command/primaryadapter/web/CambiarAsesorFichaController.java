package com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web;

import com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.mapper.CambiarAsesorFichaRequestMapper;
import com.arquisoft.shared.message.annotation.FichasApiKeys;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.CambiarAsesorFichaInteractor;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.dto.CambiarAsesorFichaRequestDTO;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.infrastructure.web.FichasRoutes;
import com.arquisoft.shared.web.openapi.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiKeys.FichaPerfil.TAG_NAME, description = FichasApiKeys.FichaPerfil.TAG_DESCRIPTION)
public class CambiarAsesorFichaController {

    private final CambiarAsesorFichaInteractor cambiarAsesorFichaInteractor;

    @PatchMapping("${rutas.fichas.fichas-perfil.asesor-ficha:/{id}/asesor-ficha}")
    @Operation(
            summary = FichasApiKeys.FichaPerfil.CAMBIAR_ASESOR_SUMMARY,
            description = FichasApiKeys.FichaPerfil.CAMBIAR_ASESOR_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiKeys.FichaPerfil.CAMBIAR_ASESOR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiKeys.FichaPerfil.CAMBIAR_ASESOR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiKeys.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiKeys.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiKeys.FichaPerfil.CAMBIAR_ASESOR_RESP_422)
    })
    @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_FICHA_PERFIL_UPDATE_ASESOR)
    public ResponseEntity<Void> cambiarAsesor(
            @PathVariable UUID id,
            @RequestBody CambiarAsesorFichaRequestDTO request
    ) {
        cambiarAsesorFichaInteractor.ejecutar(CambiarAsesorFichaRequestMapper.toCommand(request, id));
        return ResponseEntity.noContent().build();
    }
}
