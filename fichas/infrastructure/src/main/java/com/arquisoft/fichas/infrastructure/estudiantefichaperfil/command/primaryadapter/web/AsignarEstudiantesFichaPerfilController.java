package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.primaryadapter.web;

import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.primaryadapter.web.mapper.AsignarEstudiantesFichaPerfilRequestMapper;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.interactor.AsignarEstudiantesFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.primaryadapter.web.dto.AsignarEstudiantesFichaPerfilRequestDTO;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.EstudianteFichaPerfil.TAG_NAME,
        description = FichasApiMessages.EstudianteFichaPerfil.TAG_DESCRIPTION)
public class AsignarEstudiantesFichaPerfilController {

    private final AsignarEstudiantesFichaPerfilInteractor asignarEstudiantesFichaPerfilInteractor;

    @PostMapping("${rutas.fichas.fichas-perfil.estudiantes:/{fichaPerfilId}/estudiantes}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ESTUDIANTE_FICHA_PERFIL_CREATE)
    @Operation(
            summary = FichasApiMessages.EstudianteFichaPerfil.ASIGNAR_SUMMARY,
            description = FichasApiMessages.EstudianteFichaPerfil.ASIGNAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = FichasApiMessages.EstudianteFichaPerfil.ASIGNAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.EstudianteFichaPerfil.ASIGNAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.EstudianteFichaPerfil.ASIGNAR_RESP_422),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.EstudianteFichaPerfil.ASIGNAR_RESP_403)
    })
    public ResponseEntity<Void> asignarEstudiantes(
            @PathVariable UUID fichaPerfilId,
            @RequestBody AsignarEstudiantesFichaPerfilRequestDTO dto) {

        asignarEstudiantesFichaPerfilInteractor.ejecutar(AsignarEstudiantesFichaPerfilRequestMapper.toCommand(dto, fichaPerfilId));

        return ResponseEntity.noContent().build();
    }
}
