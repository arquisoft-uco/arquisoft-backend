package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.fichaperfil.command.interactor.RegistrarFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto.RegistrarFichaPerfilRequestDTO;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto.RegistrarFichaPerfilResponseDTO;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.infrastructure.web.FichasRoutes;
import com.arquisoft.shared.message.FichasApiDocs;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.openapi.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(FichasRoutes.FICHAS_PERFIL)
@RequiredArgsConstructor
@Tag(name = FichasApiDocs.FichaPerfil.TAG_NAME, description = FichasApiDocs.FichaPerfil.TAG_DESCRIPTION)
public class RegistrarFichaPerfilInputAdapter {

    private final RegistrarFichaPerfilInteractor registrarFichaPerfilInteractor;

    @PostMapping
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_FICHA_PERFIL_CREATE)
    @Operation(
            summary = FichasApiDocs.FichaPerfil.REGISTRAR_SUMMARY,
            description = FichasApiDocs.FichaPerfil.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = FichasApiDocs.FichaPerfil.REGISTRAR_RESP_201),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiDocs.FichaPerfil.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiDocs.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiDocs.Comun.RESP_403)
    })
    public ResponseEntity<RegistrarFichaPerfilResponseDTO> registrar(
            @Valid @RequestBody RegistrarFichaPerfilRequestDTO request) {

        UUID id = registrarFichaPerfilInteractor.ejecutar(request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegistrarFichaPerfilResponseDTO(id));
    }
}
