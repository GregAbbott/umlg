package org.tuml.test.restlet;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public interface Finger_ring_ServerResource {
	@Get(	"json")
	public Representation get();


}