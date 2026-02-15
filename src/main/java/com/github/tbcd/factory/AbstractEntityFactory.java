package com.github.tbcd.factory;

import org.instancio.Instancio;
import org.instancio.Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractEntityFactory<T> implements EntityFactory<T> {

	private final JpaRepository<T, ?> jpaRepository;

	public AbstractEntityFactory(JpaRepository<T, ?> jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	protected Model<T> getModel() {
		return Instancio.of(getClassz())
				.toModel();
	}

	protected abstract Class<T> getClassz();

	public T save(T entity) {
		return jpaRepository.save(entity);
	}

	public T create() {
		T instance = Instancio.of(getModel()).create();
		return save(instance);
	}

	@Override
	public T createWith(Consumer<T> customizer) {
		T instance = this.create();
		customizer.accept(instance);
		return save(instance);
	}

	public List<T> many(int count) {
		return Instancio.ofList(getModel()).size(count).create();
	}

	public T random() {
		return jpaRepository.findAll().getFirst();
	}
}
