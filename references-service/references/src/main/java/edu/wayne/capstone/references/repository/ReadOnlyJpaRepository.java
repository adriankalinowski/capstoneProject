package edu.wayne.capstone.references.repository;
import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.rest.core.annotation.RestResource;

//This interface is used to disable all the unwanted(non-get) methods inherited from JpaRepository. The end points receiving read-only data will extend this interface.

@NoRepositoryBean
public interface ReadOnlyJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
	
	@Override
	@RestResource(exported=false)
	public void delete(T entity);
	
	@Override
	@RestResource(exported=false)
	public void delete(ID id);
	
	@Override
	@RestResource(exported=false)
	public void delete(Iterable<? extends T> entities);
	
	@Override
	@RestResource(exported=false)
	public void deleteInBatch(Iterable<T> entities);
	
	@Override
    @RestResource(exported=false)
	public void deleteAllInBatch();
	
	@Override
	@RestResource(exported=false)
	public void deleteAll();
	
    @Override
	@RestResource(exported=false)
	public <S extends T> S save(S entity);
	
	@Override
	@RestResource(exported=false)
	public <S extends T> List<S> save(Iterable<S> entities);
	
	@Override
	@RestResource(exported=false)
	public <S extends T> S saveAndFlush(S entity);
	
	@Override
	@RestResource(exported=false)
	public void flush();
	
	
}
