package com.github.tbcd.fixtures.listener;

import com.github.tbcd.fixtures.loader.FixtureLoader;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunListener implements ApplicationListener<ApplicationReadyEvent> {

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		FixtureLoader fixtureLoader = event.getApplicationContext().getBean(FixtureLoader.class);
		fixtureLoader.load();
	}
}
