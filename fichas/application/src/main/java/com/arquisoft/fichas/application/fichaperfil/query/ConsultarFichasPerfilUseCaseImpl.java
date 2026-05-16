package com.arquisoft.fichas.application.fichaperfil.query;

import com.arquisoft.fichas.application.asesorficha.dto.AsesorFichaResponseDTO;
import com.arquisoft.fichas.application.fichaperfil.dto.FichaPerfilResponseDTO;
import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.fichas.domain.port.out.FichaPerfilRepositoryPort;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultarFichasPerfilUseCaseImpl implements ConsultarFichasPerfilUseCase {

    private final FichaPerfilRepositoryPort fichaPerfilRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<FichaPerfilResponseDTO> ejecutar(PaginationRequest request) {
        log.debug("Consultando fichas de perfil — page={}, size={}", request.getPage(), request.getSize());
        PaginatedResult<FichaPerfil> result = fichaPerfilRepositoryPort.consultarTodas(request);

        List<FichaPerfilResponseDTO> dtos = result.getContent().stream()
                        .map(ficha -> new FichaPerfilResponseDTO(ficha.getId(), ficha.getTituloProyecto(),
                                new AsesorFichaResponseDTO(ficha.getAsesorFicha().getId(), ficha.getAsesorFicha()
                                        .getIdentificador(), ficha.getAsesorFicha().getNombre(), ficha.getAsesorFicha()
                                        .getEmail()))).toList();


        log.info("Consulta fichas-perfil completada — total={}, page={}, size={}",
                result.getTotalElements(), request.getPage(), request.getSize());
        return new PaginatedResult<>(dtos, result.getPage(), result.getSize(), result.getTotalElements());
    }
}
