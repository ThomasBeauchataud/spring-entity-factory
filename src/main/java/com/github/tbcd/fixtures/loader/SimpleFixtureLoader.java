package com.github.tbcd.fixtures.loader;

import com.github.tbcd.fixtures.DependentFixture;
import com.github.tbcd.fixtures.Fixture;
import com.github.tbcd.fixtures.OrderedFixture;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SimpleFixtureLoader implements FixtureLoader {

	private final List<? extends Fixture> fixtures;

	public SimpleFixtureLoader(List<? extends Fixture> fixtures) {
		this.fixtures = fixtures;
	}

	public void load() {
		List<Fixture> sorted = sortFixtures(fixtures);

		sorted.forEach(fixture -> {
			System.out.println("Loading fixture: " + fixture.getClass().getSimpleName());
			fixture.load();
		});
	}

	private List<Fixture> sortFixtures(List<? extends Fixture> fixtures) {
		Map<Class<?>, Fixture> fixtureByClass = new HashMap<>();
		fixtures.forEach(f -> fixtureByClass.put(f.getClass(), f));

		List<Fixture> ordered = new ArrayList<>();
		List<Fixture> unordered = new ArrayList<>();

		for (Fixture fixture : fixtures) {
			if (fixture instanceof OrderedFixture) {
				ordered.add(fixture);
			} else {
				unordered.add(fixture);
			}
		}

		ordered.sort(Comparator.comparingInt(f -> ((OrderedFixture) f).getOrder()));

		List<Fixture> combined = new ArrayList<>();
		combined.addAll(ordered);
		combined.addAll(unordered);

		return topologicalSort(combined, fixtureByClass);
	}

	private List<Fixture> topologicalSort(List<Fixture> fixtures, Map<Class<?>, Fixture> fixtureByClass) {
		Set<Fixture> visited = new HashSet<>();
		Set<Fixture> inProgress = new HashSet<>();
		LinkedList<Fixture> result = new LinkedList<>();

		for (Fixture fixture : fixtures) {
			if (!visited.contains(fixture)) {
				visit(fixture, fixtureByClass, visited, inProgress, result);
			}
		}

		return result;
	}

	private void visit(Fixture fixture, Map<Class<?>, Fixture> fixtureByClass, Set<Fixture> visited, Set<Fixture> inProgress, LinkedList<Fixture> result) {

		if (inProgress.contains(fixture)) {
			throw new IllegalStateException(
					"Dépendance circulaire détectée pour: " + fixture.getClass().getSimpleName()
			);
		}

		if (visited.contains(fixture)) {
			return;
		}

		inProgress.add(fixture);

		if (fixture instanceof DependentFixture dependent) {
			List<Class<? extends Fixture>> dependencies = dependent.getDependencies();
			if (dependencies != null) {
				for (Class<? extends Fixture> depClass : dependencies) {
					Fixture depFixture = fixtureByClass.get(depClass);
					if (depFixture != null) {
						visit(depFixture, fixtureByClass, visited, inProgress, result);
					} else {
						throw new IllegalStateException(
								"Dépendance non trouvée: " + depClass.getSimpleName() +
										" pour " + fixture.getClass().getSimpleName()
						);
					}
				}
			}
		}

		inProgress.remove(fixture);
		visited.add(fixture);
		result.add(fixture);
	}
}
