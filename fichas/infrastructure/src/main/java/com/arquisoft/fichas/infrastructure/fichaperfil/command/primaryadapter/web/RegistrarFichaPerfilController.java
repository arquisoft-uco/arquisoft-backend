package com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web;

import com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.mapper.RegistrarFichaPerfilRequestMapper;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.RegistrarFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.dto.RegistrarFichaPerfilRequestDTO;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.dto.RegistrarFichaPerfilResponseDTO;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.infrastructure.web.FichasRoutes;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.openapi.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.FichaPerfil.TAG_NAME, description = FichasApiMessages.FichaPerfil.TAG_DESCRIPTION)
public class RegistrarFichaPerfilController {

    private final RegistrarFichaPerfilInteractor registrarFichaPerfilInteractor;

    @PostMapping
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_FICHA_PERFIL_CREATE)
    @Operation(
            summary = FichasApiMessages.FichaPerfil.REGISTRAR_SUMMARY,
            description = FichasApiMessages.FichaPerfil.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = FichasApiMessages.FichaPerfil.REGISTRAR_RESP_201),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.FichaPerfil.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<RegistrarFichaPerfilResponseDTO> registrar(
            @RequestBody RegistrarFichaPerfilRequestDTO request) {

        UUID id = registrarFichaPerfilInteractor.ejecutar(RegistrarFichaPerfilRequestMapper.toCommand(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegistrarFichaPerfilResponseDTO(id));
    }
}
