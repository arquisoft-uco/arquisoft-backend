package com.arquisoft.artefactos.application.versionartefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.DescargaVersionArtefactoCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.finder.ArchivoVersionArtefactoQueryFinder;
import com.arquisoft.artefactos.application.versionartefacto.query.finder.EstudianteParticipaVersionArtefactoQueryFinder;
import com.arquisoft.artefactos.application.versionartefacto.query.finder.model.ParticipacionVersionArtefacto;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.EnlaceDescargaVersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.EnlaceDescargaVersionArtefactoQueryOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.query.usecase.DescargarArchivoVersionArtefactoUseCase;
import com.arquisoft.artefactos.application.versionartefacto.query.validator.DescargarArchivoVersionArtefactoValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DescargarArchivoVersionArtefactoUseCaseImpl implements DescargarArchivoVersionArtefactoUseCase {

    private final ArchivoVersionArtefactoQueryFinder archivoVersionArtefactoQueryFinder;
    private final EstudianteParticipaVersionArtefactoQueryFinder estudianteParticipaVersionArtefactoQueryFinder;
    private final DescargarArchivoVersionArtefactoValidator descargarArchivoVersionArtefactoValidator;
    private final EnlaceDescargaVersionArtefactoQueryOutputPort enlaceDescargaVersionArtefactoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public EnlaceDescargaVersionArtefactoReadModel ejecutar(DescargaVersionArtefactoCriteria entrada) {
        logger.debug(VersionArtefactoKey.LOG_DESCARGANDO, entrada.versionArtefacto(), entrada.actor());

        var archivo = archivoVersionArtefactoQueryFinder.obtener(entrada.versionArtefacto());
        var participa = estudianteParticipaVersionArtefactoQueryFinder.obtener(
                new ParticipacionVersionArtefacto(entrada.versionArtefacto(), entrada.actor()));

        logger.debug(VersionArtefactoKey.LOG_VERIFICACION_DESCARGA,
                !archivo.esVacio(), participa, archivo.isProyectoFinalizado());

        descargarArchivoVersionArtefactoValidator.validar(entrada, archivo, participa);

        var enlace = enlaceDescargaVersionArtefactoQueryOutputPort.generar(
                entrada.versionArtefacto(), archivo.getUriArchivo(), archivo.getNombreRespaldo());

        logger.debug(VersionArtefactoKey.LOG_DESCARGA_GENERADA, entrada.versionArtefacto(), enlace.expiraEn());
        return enlace;
    }
}
