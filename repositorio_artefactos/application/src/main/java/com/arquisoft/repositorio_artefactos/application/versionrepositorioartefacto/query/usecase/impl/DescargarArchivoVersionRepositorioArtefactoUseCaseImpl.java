package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.ArchivoVersionRepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.finder.ArchivoVersionQueryFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.EnlaceDescargaVersionReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.EnlaceDescargaVersionQueryOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.DescargarArchivoVersionRepositorioArtefactoUseCase;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.validator.DescargarArchivoVersionRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DescargarArchivoVersionRepositorioArtefactoUseCaseImpl
        implements DescargarArchivoVersionRepositorioArtefactoUseCase {

    private final ArchivoVersionQueryFinder archivoVersionQueryFinder;
    private final DescargarArchivoVersionRepositorioArtefactoValidator descargarArchivoVersionRepositorioArtefactoValidator;
    private final EnlaceDescargaVersionQueryOutputPort enlaceDescargaVersionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public EnlaceDescargaVersionReadModel ejecutar(ArchivoVersionRepositorioArtefactoCriteria entrada) {
        logger.debug(VersionRepositorioArtefactoKey.LOG_DESCARGANDO, entrada.version());

        var archivo = archivoVersionQueryFinder.obtener(entrada.version());

        descargarArchivoVersionRepositorioArtefactoValidator.validar(
                new ExistenciaVersionRepositorioArtefacto(entrada.version(), archivo));

        var enlace = enlaceDescargaVersionQueryOutputPort.generar(
                entrada.version(), archivo.getUriArchivo(), archivo.getNombreArchivo());

        logger.debug(VersionRepositorioArtefactoKey.LOG_DESCARGA_GENERADA, entrada.version(), enlace.expiraEn());
        return enlace;
    }
}
