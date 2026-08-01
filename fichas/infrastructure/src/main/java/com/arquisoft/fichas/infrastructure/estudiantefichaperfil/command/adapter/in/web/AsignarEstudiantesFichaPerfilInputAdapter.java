package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in.AsignarEstudiantesFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web.dto.AsignarEstudiantesFichaPerfilRequestDTO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(FichasRoutes.FICHAS_PERFIL)
@RequiredArgsConstructor
@Tag(name = FichasApiDocs.EstudianteFichaPerfil.TAG_NAME,
        description = FichasApiDocs.EstudianteFichaPerfil.TAG_DESCRIPTION)
public class AsignarEstudiantesFichaPerfilInputAdapter {

    private final AsignarEstudiantesFichaPerfilInteractor asignarEstudiantesFichaPerfilInteractor;

    @PostMapping("/{fichaPerfilId}/estudiantes")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ESTUDIANTE_FICHA_PERFIL_CREATE)
    @Operation(
            summary = FichasApiDocs.EstudianteFichaPerfil.ASIGNAR_SUMMARY,
            description = FichasApiDocs.EstudianteFichaPerfil.ASIGNAR_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiDocs.EstudianteFichaPerfil.ASIGNAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiDocs.EstudianteFichaPerfil.ASIGNAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiDocs.EstudianteFichaPerfil.ASIGNAR_RESP_422),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiDocs.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiDocs.EstudianteFichaPerfil.ASIGNAR_RESP_403)
    })
    public ResponseEntity<Void> asignarEstudiantes(
            @PathVariable UUID fichaPerfilId,
            @Valid @RequestBody AsignarEstudiantesFichaPerfilRequestDTO dto) {

        asignarEstudiantesFichaPerfilInteractor.ejecutar(dto.toCommand(fichaPerfilId));

        return ResponseEntity.noContent().build();
    }
}
