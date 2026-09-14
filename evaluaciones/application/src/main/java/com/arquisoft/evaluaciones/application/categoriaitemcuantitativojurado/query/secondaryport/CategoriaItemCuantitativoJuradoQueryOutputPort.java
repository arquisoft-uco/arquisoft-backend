package com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.secondaryport;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.readmodel.CategoriaItemCuantitativoJuradoReadModel;

import java.util.List;

public interface CategoriaItemCuantitativoJuradoQueryOutputPort {

    List<CategoriaItemCuantitativoJuradoReadModel> consultar(String nombre);
}
