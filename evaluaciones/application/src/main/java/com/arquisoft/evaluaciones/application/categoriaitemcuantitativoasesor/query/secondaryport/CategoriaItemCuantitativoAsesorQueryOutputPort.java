package com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.readmodel.CategoriaItemCuantitativoAsesorReadModel;

import java.util.List;

public interface CategoriaItemCuantitativoAsesorQueryOutputPort {

    List<CategoriaItemCuantitativoAsesorReadModel> consultar(String nombre);
}
