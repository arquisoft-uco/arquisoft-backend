package com.arquisoft.fichas.application.representantecomite.command.usecase.impl;

import com.arquisoft.fichas.application.representantecomite.command.finder.RepresentanteComitePorIdFinder;
import com.arquisoft.fichas.application.representantecomite.command.result.ActualizacionRepresentanteComiteResult;
import com.arquisoft.fichas.application.representantecomite.command.result.mapper.ActualizacionRepresentanteComiteResultMapper;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.mapper.RepresentanteComiteMapper;
import com.arquisoft.fichas.application.representantecomite.command.usecase.ActualizarRepresentanteComiteUseCase;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActualizarRepresentanteComiteUseCaseImpl implements ActualizarRepresentanteComiteUseCase {

    private final RepresentanteComiteOutputPort representanteComiteOutputPort;
    private final RepresentanteComitePorIdFinder representanteComitePorIdFinder;
    private final AppLogger logger;

    @Override
    public ActualizacionRepresentanteComiteResult ejecutar(RepresentanteComiteDomain entrada) {
        var vigente = representanteComitePorIdFinder.obtener(entrada.getId());
        logger.debug(RepresentanteComiteKey.LOG_VERIFICACION_ACTUALIZAR, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            return ActualizacionRepresentanteComiteResultMapper.toResultNoReplicado(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return ActualizacionRepresentanteComiteResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.actualizar(entrada.getIdentificador(), entrada.getNombre(), entrada.getEmail(),
                entrada.getOcurridoEn());
        representanteComiteOutputPort.actualizar(RepresentanteComiteMapper.toEntity(vigente));
        return ActualizacionRepresentanteComiteResultMapper.toResultActualizada(vigente);
    }
}
