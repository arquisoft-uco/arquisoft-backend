package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.model.RemoverEstudianteFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.interactor.RemoverEstudianteFichaPerfilInteractor;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.EstudianteFichaPerfil.TAG_NAME,
        description = FichasApiMessages.EstudianteFichaPerfil.TAG_DESCRIPTION)
public class RemoverEstudianteFichaPerfilController {

    private final RemoverEstudianteFichaPerfilInteractor removerEstudianteFichaPerfilInteractor;

    @DeleteMapping("${rutas.fichas.fichas-perfil.estudiante-por-id:/{fichaPerfilId}/estudiantes/{estudianteId}}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ESTUDIANTE_FICHA_PERFIL_DELETE)
    @Operation(
            summary = FichasApiMessages.EstudianteFichaPerfil.REMOVER_SUMMARY,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiMessages.EstudianteFichaPerfil.REMOVER_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.EstudianteFichaPerfil.REMOVER_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.EstudianteFichaPerfil.REMOVER_RESP_403)
    })
    public ResponseEntity<Void> remover(
            @PathVariable UUID fichaPerfilId,
            @PathVariable UUID estudianteId
    ) {
        var command = RemoverEstudianteFichaPerfilCommand.crear(fichaPerfilId, estudianteId);
        removerEstudianteFichaPerfilInteractor.ejecutar(command);
        return ResponseEntity.noContent().build();
    }
}
