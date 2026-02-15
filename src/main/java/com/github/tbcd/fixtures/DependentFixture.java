package com.github.tbcd.fixtures;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DependentFixture {

	List<Class<? extends Fixture>> getDependencies();

}
