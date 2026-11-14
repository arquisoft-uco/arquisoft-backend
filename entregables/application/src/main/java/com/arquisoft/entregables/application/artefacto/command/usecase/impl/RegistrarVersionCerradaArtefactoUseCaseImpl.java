package com.arquisoft.entregables.application.artefacto.command.usecase.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ArchivoOrigenArtefactoFinder;
import com.arquisoft.entregables.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.entregables.application.artefacto.command.result.RegistroVersionCerradaArtefactoResult;
import com.arquisoft.entregables.application.artefacto.command.result.mapper.RegistroVersionCerradaArtefactoResultMapper;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.AlmacenamientoArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.mapper.ArtefactoMapper;
import com.arquisoft.entregables.application.artefacto.command.usecase.RegistrarVersionCerradaArtefactoUseCase;
import com.arquisoft.entregables.application.artefacto.command.validator.RegistrarVersionCerradaArtefactoValidator;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.entregables.domain.artefacto.RegistroVersionCerradaArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarVersionCerradaArtefactoUseCaseImpl implements RegistrarVersionCerradaArtefactoUseCase {

    private final ProyectoGradoBloqueadoPorIdFinder proyectoGradoBloqueadoPorIdFinder;
    private final ArtefactoPorIdFinder artefactoPorIdFinder;
    private final ArchivoOrigenArtefactoFinder archivoOrigenArtefactoFinder;
    private final RegistrarVersionCerradaArtefactoValidator registrarVersionCerradaArtefactoValidator;
    private final ArtefactoOutputPort artefactoOutputPort;
    private final AlmacenamientoArtefactoOutputPort almacenamientoArtefactoOutputPort;
    private final AppLogger logger;

    @Override
    public RegistroVersionCerradaArtefactoResult ejecutar(RegistroVersionCerradaArtefactoDomain registro) {
        var artefacto = registro.getArtefacto();

        var proyecto = proyectoGradoBloqueadoPorIdFinder.obtener(artefacto.getProyectoGrado());
        var guardado = artefactoPorIdFinder.obtener(artefacto.getId());
        logger.debug(ArtefactoKey.LOG_VERIFICACION_REGISTRAR, !proyecto.esVacio(), proyecto.estaCerrado(),
                guardado.getNumeroVersion());

        if (proyecto.estaCerrado()) {
            return RegistroVersionCerradaArtefactoResultMapper.toResultProyectoCerrado(registro);
        }
        if (guardado.tieneVersionIgualOPosteriorA(artefacto.getNumeroVersion())) {
            return RegistroVersionCerradaArtefactoResultMapper.toResultObsoleta(registro);
        }

        var etagOrigen = archivoOrigenArtefactoFinder.obtener(registro.getClaveOrigen());
        logger.debug(ArtefactoKey.LOG_VERIFICACION_ORIGEN, !UtilTexto.esVacioONulo(etagOrigen));

        registrarVersionCerradaArtefactoValidator.validar(registro, proyecto, etagOrigen);

        almacenamientoArtefactoOutputPort.copiar(registro.getClaveOrigen(), artefacto.getClaveObjeto(), etagOrigen);

        if (guardado.esVacio()) {
            artefactoOutputPort.insertar(ArtefactoMapper.toEntity(artefacto));
            return RegistroVersionCerradaArtefactoResultMapper.toResultRegistrada(registro);
        }

        var filas = artefactoOutputPort.avanzarVersion(ArtefactoMapper.toEntity(artefacto));
        if (filas == 0) {
            return RegistroVersionCerradaArtefactoResultMapper.toResultObsoleta(registro);
        }
        if (guardado.tieneVersion() && !guardado.getClaveObjeto().equals(artefacto.getClaveObjeto())) {
            almacenamientoArtefactoOutputPort.eliminarTrasConfirmar(guardado.getClaveObjeto());
        }
        return RegistroVersionCerradaArtefactoResultMapper.toResultRegistrada(registro);
    }
}
