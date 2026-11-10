package com.arquisoft.artefactos.application.versionartefacto.query.validator.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.DescargaVersionArtefactoCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.validator.DescargarArchivoVersionArtefactoValidator;
import com.arquisoft.artefactos.domain.versionartefacto.ArchivoVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.AccesoVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.model.DisponibilidadArchivoVersion;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaVersionArtefactoConsultada;
import com.arquisoft.artefactos.domain.versionartefacto.rules.ActorAccedeVersionArtefactoRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.ArchivoVersionNoPurgadoRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoConsultadaExisteRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.ActorAccedeVersionArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.ArchivoVersionNoPurgadoRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.VersionArtefactoConsultadaExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class DescargarArchivoVersionArtefactoValidatorImpl implements DescargarArchivoVersionArtefactoValidator {

    private final VersionArtefactoConsultadaExisteRule versionArtefactoConsultadaExisteRule;
    private final ActorAccedeVersionArtefactoRule actorAccedeVersionArtefactoRule;
    private final ArchivoVersionNoPurgadoRule archivoVersionNoPurgadoRule;

    public DescargarArchivoVersionArtefactoValidatorImpl() {
        this.versionArtefactoConsultadaExisteRule = new VersionArtefactoConsultadaExisteRuleImpl();
        this.actorAccedeVersionArtefactoRule = new ActorAccedeVersionArtefactoRuleImpl();
        this.archivoVersionNoPurgadoRule = new ArchivoVersionNoPurgadoRuleImpl();
    }

    @Override
    public void validar(DescargaVersionArtefactoCriteria criteria, ArchivoVersionArtefactoDomain archivo,
                        boolean participa) {
        versionArtefactoConsultadaExisteRule.validar(
                new ExistenciaVersionArtefactoConsultada(criteria.versionArtefacto(), archivo.getAsesor()));
        actorAccedeVersionArtefactoRule.validar(new AccesoVersionArtefacto(
                criteria.versionArtefacto(), criteria.actor(), criteria.comoAsesor(), criteria.comoEstudiante(),
                archivo.getAsesor(), participa));
        archivoVersionNoPurgadoRule.validar(new DisponibilidadArchivoVersion(
                criteria.versionArtefacto(), archivo.getNombreProyecto(), archivo.isProyectoFinalizado()));
    }
}
