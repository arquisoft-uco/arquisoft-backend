package com.arquisoft.fichas.application.representantecomite.command.usecase.impl;

import com.arquisoft.fichas.application.representantecomite.command.finder.RepresentanteComitePorIdFinder;
import com.arquisoft.fichas.application.representantecomite.command.result.AgregacionRepresentanteComiteResult;
import com.arquisoft.fichas.application.representantecomite.command.result.mapper.AgregacionRepresentanteComiteResultMapper;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.mapper.RepresentanteComiteMapper;
import com.arquisoft.fichas.application.representantecomite.command.usecase.AgregarRepresentanteComiteUseCase;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarRepresentanteComiteUseCaseImpl implements AgregarRepresentanteComiteUseCase {

    private final RepresentanteComiteOutputPort representanteComiteOutputPort;
    private final RepresentanteComitePorIdFinder representanteComitePorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionRepresentanteComiteResult ejecutar(RepresentanteComiteDomain representanteComite) {
        var vigente = representanteComitePorIdFinder.obtener(representanteComite.getId());
        logger.debug(RepresentanteComiteKey.LOG_VERIFICACION_AGREGAR, representanteComite.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            representanteComiteOutputPort.guardar(RepresentanteComiteMapper.toEntity(representanteComite));
            return AgregacionRepresentanteComiteResultMapper.toResultAgregada(representanteComite);
        }

        if (!representanteComite.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return AgregacionRepresentanteComiteResultMapper.toResultDescartada(
                    representanteComite, vigente.getOcurridoEn());
        }

        if (!vigente.estaEliminado()) {
            return AgregacionRepresentanteComiteResultMapper.toResultDuplicada(representanteComite);
        }

        vigente.reactivar(representanteComite.getIdentificador(), representanteComite.getNombre(),
                representanteComite.getEmail(), representanteComite.getOcurridoEn());
        representanteComiteOutputPort.reactivar(RepresentanteComiteMapper.toEntity(vigente));
        return AgregacionRepresentanteComiteResultMapper.toResultReactivada(vigente);
    }
}
