package com.arquisoft.fichas.application.revisionitem.command.validator;

import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.revisionitem.EnvioCorreccionesRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.model.ObservacionesRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.model.PertenenciaRevisionItem;

public interface EnviarCorreccionesRevisionItemValidator {

    void validar(EnvioCorreccionesRevisionItemDomain entrada, PertenenciaRevisionItem pertenencia,
                 FichaPerfilDomain ficha, AsesorFichaDomain asesor, ObservacionesRevisionItem observaciones);
}
