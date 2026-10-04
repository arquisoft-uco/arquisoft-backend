package com.arquisoft.fichas.application.representantecomite.command.usecase.impl;

import com.arquisoft.fichas.application.representantecomite.command.finder.RepresentanteComitePorIdFinder;
import com.arquisoft.fichas.application.representantecomite.command.result.RemocionRepresentanteComiteResult;
import com.arquisoft.fichas.application.representantecomite.command.result.mapper.RemocionRepresentanteComiteResultMapper;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.mapper.RepresentanteComiteMapper;
import com.arquisoft.fichas.application.representantecomite.command.usecase.RemoverRepresentanteComiteUseCase;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverRepresentanteComiteUseCaseImpl implements RemoverRepresentanteComiteUseCase {

    private final RepresentanteComiteOutputPort representanteComiteOutputPort;
    private final RepresentanteComitePorIdFinder representanteComitePorIdFinder;
    private final AppLogger logger;

    @Override
    public RemocionRepresentanteComiteResult ejecutar(RepresentanteComiteDomain entrada) {
        var vigente = representanteComitePorIdFinder.obtener(entrada.getId());
        logger.debug(RepresentanteComiteKey.LOG_VERIFICACION_REMOVER, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            entrada.remover(entrada.getOcurridoEn());
            representanteComiteOutputPort.guardar(RepresentanteComiteMapper.toEntity(entrada));
            return RemocionRepresentanteComiteResultMapper.toResultLapida(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return RemocionRepresentanteComiteResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.remover(entrada.getOcurridoEn());
        representanteComiteOutputPort.eliminarLogica(vigente.getId(), vigente.getEliminadoEn());
        return RemocionRepresentanteComiteResultMapper.toResultRemovida(vigente);
    }
}
