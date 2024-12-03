package org.eclipse.epsilon.labs.playground.standalone;

import java.util.ArrayList;
import java.util.List;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class BackendConfiguration {
    private List<BackendService> services = new ArrayList<>();

	public List<BackendService> getServices() {
		return services;
	}

	public void setServices(List<BackendService> services) {
		this.services = services;
	}

}
