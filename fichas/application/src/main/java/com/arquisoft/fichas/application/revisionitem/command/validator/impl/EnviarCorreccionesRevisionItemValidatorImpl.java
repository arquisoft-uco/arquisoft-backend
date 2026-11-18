package com.arquisoft.fichas.application.revisionitem.command.validator.impl;

import com.arquisoft.fichas.application.revisionitem.command.validator.EnviarCorreccionesRevisionItemValidator;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudiantePropietarioFichaRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.EnvioCorreccionesRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.model.ExistenciaRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.model.ObservacionesRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.model.PertenenciaRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemConObservacionesRule;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemEnProgresoRule;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemExisteRule;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemNoCerradaRule;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemSinObservacionesPendientesRule;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemConObservacionesRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemEnProgresoRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemExisteRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemNoCerradaRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemSinObservacionesPendientesRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class EnviarCorreccionesRevisionItemValidatorImpl implements EnviarCorreccionesRevisionItemValidator {

    private final RevisionItemExisteRule revisionItemExisteRule;
    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final AsesorFichaExisteRule asesorFichaExisteRule;
    private final EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;
    private final RevisionItemNoCerradaRule revisionItemNoCerradaRule;
    private final RevisionItemEnProgresoRule revisionItemEnProgresoRule;
    private final RevisionItemConObservacionesRule revisionItemConObservacionesRule;
    private final RevisionItemSinObservacionesPendientesRule revisionItemSinObservacionesPendientesRule;

    public EnviarCorreccionesRevisionItemValidatorImpl() {
        this.revisionItemExisteRule = new RevisionItemExisteRuleImpl();
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
        this.asesorFichaExisteRule = new AsesorFichaExisteRuleImpl();
        this.estudiantePropietarioFichaRule = new EstudiantePropietarioFichaRuleImpl();
        this.revisionItemNoCerradaRule = new RevisionItemNoCerradaRuleImpl();
        this.revisionItemEnProgresoRule = new RevisionItemEnProgresoRuleImpl();
        this.revisionItemConObservacionesRule = new RevisionItemConObservacionesRuleImpl();
        this.revisionItemSinObservacionesPendientesRule = new RevisionItemSinObservacionesPendientesRuleImpl();
    }

    @Override
    public void validar(EnvioCorreccionesRevisionItemDomain entrada, PertenenciaRevisionItem pertenencia,
                        FichaPerfilDomain ficha, AsesorFichaDomain asesor, ObservacionesRevisionItem observaciones) {

        revisionItemExisteRule.validar(
                new ExistenciaRevisionItem(entrada.getRevisionItem(), !pertenencia.esVacio()));

        fichaPerfilExisteRule.validar(
                new ExistenciaFichaPerfil(pertenencia.fichaPerfil(), !ficha.esVacio()));

        asesorFichaExisteRule.validar(
                new ExistenciaAsesorFicha(ficha.getAsesorFicha(), !asesor.esVacio()));

        estudiantePropietarioFichaRule.validar(
                new PropiedadFicha(pertenencia.fichaPerfil(), entrada.getEstudiante(), pertenencia.esPropietario()));

        var estado = new EstadoRevisionItem(entrada.getRevisionItem(), pertenencia.estadoRevision().getId());
        revisionItemNoCerradaRule.validar(estado);
        revisionItemEnProgresoRule.validar(estado);

        revisionItemConObservacionesRule.validar(observaciones);
        revisionItemSinObservacionesPendientesRule.validar(observaciones);
    }
}
