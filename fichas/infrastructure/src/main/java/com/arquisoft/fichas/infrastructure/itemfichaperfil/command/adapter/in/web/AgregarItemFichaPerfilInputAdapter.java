package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.AgregarItemFichaPerfilInputPort;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto.AgregarItemFichaPerfilRequestDTO;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto.AgregarItemFichaPerfilResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Tag(name = "Fichas Perfil", description = "Gestión de fichas de perfil de proyectos de grado")
public class AgregarItemFichaPerfilInputAdapter {

    private final AgregarItemFichaPerfilInputPort agregarItemFichaPerfilInputPort;

    @PostMapping("/{fichaPerfilId}/items")
    @PreAuthorize("hasAuthority('fichas:item-ficha-perfil:create')")
    @Operation(
            summary = "Agregar ítem a ficha de perfil",
            description = "Permite a un estudiante agregar un ítem de contenido (objetivo, estado del arte, etc.) a su propia ficha de perfil",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Ítem agregado exitosamente",
                    content = @Content(schema = @Schema(implementation = AgregarItemFichaPerfilResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Tipo duplicado o ficha no encontrada"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Sin permiso o ficha no propia"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Datos inválidos o tipo de ítem inválido — fieldErrors"
            )
    })
    public ResponseEntity<AgregarItemFichaPerfilResponseDTO> agregarItem(
            @PathVariable UUID fichaPerfilId,
            @Valid @RequestBody AgregarItemFichaPerfilRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        UUID estudianteId = UUID.fromString(jwt.getSubject());

        UUID itemId = agregarItemFichaPerfilInputPort.ejecutar(dto.toCommand(fichaPerfilId, estudianteId));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AgregarItemFichaPerfilResponseDTO(itemId));
    }
}
