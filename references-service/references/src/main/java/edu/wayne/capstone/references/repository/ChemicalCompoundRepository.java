package edu.wayne.capstone.references.repository;

import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import edu.wayne.capstone.references.data.ChemicalCompound;
import edu.wayne.capstone.references.data.QChemicalCompound;

@RepositoryRestResource
public interface ChemicalCompoundRepository extends ReadOnlyJpaRepository<ChemicalCompound, String>, ReadOnlyQueryDslExecuter<ChemicalCompound, String, QChemicalCompound> {
	
	@Override
	default void repositorySpecificCustomize(QuerydslBindings bindings, QChemicalCompound root) {
		
		bindings.bind(root.chemicalCompoundId).all((path, values) -> path.in(sanitizeStringCollection(values)));
	}
}