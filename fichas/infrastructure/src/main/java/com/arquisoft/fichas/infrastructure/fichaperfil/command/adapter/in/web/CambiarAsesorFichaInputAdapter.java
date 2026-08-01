package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.fichaperfil.command.port.in.CambiarAsesorFichaInteractor;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto.CambiarAsesorFichaRequestDTO;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.infrastructure.web.FichasRoutes;
import com.arquisoft.shared.message.FichasApiDocs;
import com.arquisoft.shared.web.openapi.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping(FichasRoutes.FICHAS_PERFIL)
@RequiredArgsConstructor
@Tag(name = FichasApiDocs.FichaPerfil.TAG_NAME, description = FichasApiDocs.FichaPerfil.TAG_DESCRIPTION)
public class CambiarAsesorFichaInputAdapter {

    private final CambiarAsesorFichaInteractor cambiarAsesorFichaInteractor;

    @PatchMapping("/{id}/asesor-ficha")
    @Operation(
            summary = FichasApiDocs.FichaPerfil.CAMBIAR_ASESOR_SUMMARY,
            description = FichasApiDocs.FichaPerfil.CAMBIAR_ASESOR_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiDocs.FichaPerfil.CAMBIAR_ASESOR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiDocs.FichaPerfil.CAMBIAR_ASESOR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiDocs.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiDocs.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiDocs.FichaPerfil.CAMBIAR_ASESOR_RESP_422)
    })
    @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_FICHA_PERFIL_UPDATE_ASESOR)
    public ResponseEntity<Void> cambiarAsesor(
            @PathVariable UUID id,
            @Valid @RequestBody CambiarAsesorFichaRequestDTO request
    ) {
        cambiarAsesorFichaInteractor.ejecutar(request.toCommand(id));
        return ResponseEntity.noContent().build();
    }
}
