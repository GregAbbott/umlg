package org.tuml.test.restlet;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.tuml.runtime.adaptor.GraphDb;
import org.tuml.test.Ring;
import org.tuml.test.Ring.RingRuntimePropertyEnum;

public class RingServerResourceImpl extends ServerResource implements RingServerResource {
	private int ringId;

	/**
	 * default constructor for RingServerResourceImpl
	 */
	public RingServerResourceImpl() {
		setNegotiated(false);
	}

	@Override
	public Representation get() throws ResourceException {
		this.ringId= Integer.parseInt((String)getRequestAttributes().get("ringId"));;
		Ring c = new Ring(GraphDb.getDb().getVertex(this.ringId));
		StringBuilder json = new StringBuilder();
		json.append("[");
		json.append(c.toJson());
		json.append(",");
		json.append(" {\"meta\" : ");
		json.append(RingRuntimePropertyEnum.asJson());
		json.append("}]");
		return new JsonRepresentation(json.toString());
	}


}