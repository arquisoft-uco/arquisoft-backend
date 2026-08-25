package com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.UsuariosApiMessages;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.primaryport.interactor.AgregarRepresentanteComiteCurriculumInteractor;
import com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.primaryadapter.web.dto.AgregarRepresentanteComiteCurriculumRequestDTO;
import com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.primaryadapter.web.dto.AgregarRepresentanteComiteCurriculumResponseDTO;
import com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.primaryadapter.web.mapper.AgregarRepresentanteComiteCurriculumRequestMapper;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.arquisoft.shared.message.annotation.ApiCodes;


import java.util.UUID;

@RestController
@RequestMapping("${rutas.usuarios.representantes-comite-curriculum.base:/representantes-comite-curriculum}")
@RequiredArgsConstructor
@Tag(name = UsuariosApiMessages.RepresentanteComiteCurriculum.TAG_NAME,
     description = UsuariosApiMessages.RepresentanteComiteCurriculum.TAG_DESCRIPTION)
public class AgregarRepresentanteComiteCurriculumController {

    private final AgregarRepresentanteComiteCurriculumInteractor agregarRepresentanteComiteCurriculumInteractor;

    @PostMapping
    @PreAuthorize(UsuariosAuthorities.Expresiones.HAS_AGREGAR_REPRESENTANTE_COMITE_CURRICULUM)
    @Operation(
            summary = UsuariosApiMessages.RepresentanteComiteCurriculum.AGREGAR_SUMMARY,
            description = UsuariosApiMessages.RepresentanteComiteCurriculum.AGREGAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = UsuariosApiMessages.RepresentanteComiteCurriculum.AGREGAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = AgregarRepresentanteComiteCurriculumResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = UsuariosApiMessages.RepresentanteComiteCurriculum.AGREGAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = UsuariosApiMessages.RepresentanteComiteCurriculum.AGREGAR_RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = UsuariosApiMessages.RepresentanteComiteCurriculum.AGREGAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = UsuariosApiMessages.RepresentanteComiteCurriculum.AGREGAR_RESP_422)
    })
    public ResponseEntity<AgregarRepresentanteComiteCurriculumResponseDTO> agregar(
            @RequestBody AgregarRepresentanteComiteCurriculumRequestDTO request) {

        UUID id = agregarRepresentanteComiteCurriculumInteractor.ejecutar(AgregarRepresentanteComiteCurriculumRequestMapper.toCommand(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(new AgregarRepresentanteComiteCurriculumResponseDTO(id));
    }
}
