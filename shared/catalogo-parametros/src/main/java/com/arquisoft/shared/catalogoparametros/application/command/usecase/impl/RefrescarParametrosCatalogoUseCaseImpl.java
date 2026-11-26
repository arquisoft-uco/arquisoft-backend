package com.arquisoft.shared.catalogoparametros.application.command.usecase.impl;

import com.arquisoft.shared.catalogoparametros.CatalogoParametrosEncendido;
import com.arquisoft.shared.catalogoparametros.application.command.result.RefrescoParametrosCatalogoResult;
import com.arquisoft.shared.catalogoparametros.application.command.result.mapper.RefrescoParametrosCatalogoResultMapper;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.FuenteParametrosCatalogoOutputPort;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.ParametrosCatalogoOutputPort;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.model.ObtencionParametrosCatalogo;
import com.arquisoft.shared.catalogoparametros.application.command.usecase.RefrescarParametrosCatalogoUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.app.CatalogoParametrosKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@CatalogoParametrosEncendido
@RequiredArgsConstructor
public class RefrescarParametrosCatalogoUseCaseImpl implements RefrescarParametrosCatalogoUseCase {

    private final FuenteParametrosCatalogoOutputPort fuenteParametrosCatalogoOutputPort;
    private final ParametrosCatalogoOutputPort parametrosCatalogoOutputPort;
    private final AppLogger logger;

    @Override
    public RefrescoParametrosCatalogoResult ejecutar() {
        if (parametrosCatalogoOutputPort.suspendido()) {
            logger.debug(CatalogoParametrosKey.LOG_SUSPENDIDO);
            return RefrescoParametrosCatalogoResultMapper.toResultSuspendido();
        }

        logger.debug(CatalogoParametrosKey.LOG_REFRESCANDO);
        var obtencion = fuenteParametrosCatalogoOutputPort.obtener();

        return switch (obtencion) {
            case ObtencionParametrosCatalogo.Obtenida obtenida -> reemplazar(obtenida);
            case ObtencionParametrosCatalogo.NoDisponible noDisponible -> conservar(noDisponible);
            case ObtencionParametrosCatalogo.Rechazada rechazada -> suspender(rechazada);
            case ObtencionParametrosCatalogo.SinConfigurar sinConfigurar ->
                    RefrescoParametrosCatalogoResultMapper.toResultSinConfigurar();
        };
    }

    private RefrescoParametrosCatalogoResult reemplazar(ObtencionParametrosCatalogo.Obtenida obtenida) {
        parametrosCatalogoOutputPort.reemplazar(obtenida.entity());

        var resultado = RefrescoParametrosCatalogoResultMapper.toResultRefrescado(obtenida.entity());
        logger.debug(CatalogoParametrosKey.LOG_REFRESCADO, resultado.cantidad());
        return resultado;
    }

    private RefrescoParametrosCatalogoResult conservar(ObtencionParametrosCatalogo.NoDisponible noDisponible) {
        logger.warn(CatalogoParametrosKey.LOG_CONSERVADO, noDisponible.motivo());
        return RefrescoParametrosCatalogoResultMapper.toResultConservado(noDisponible.motivo());
    }

    private RefrescoParametrosCatalogoResult suspender(ObtencionParametrosCatalogo.Rechazada rechazada) {
        parametrosCatalogoOutputPort.suspender();
        logger.error(CatalogoParametrosKey.LOG_RECHAZADO, rechazada.estado());
        return RefrescoParametrosCatalogoResultMapper.toResultRechazado(rechazada.estado());
    }
}
