package com.github.tbcd.fixtures;

import org.springframework.stereotype.Component;

@Component
public interface OrderedFixture extends Fixture {

	int getOrder();

}
