package org.tuml.runtime.restlet;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.tuml.framework.ModelLoader;

public class TumlGuiServerResource extends ServerResource {

    public TumlGuiServerResource() {
        setNegotiated(false);
    }

    @Override
    protected Representation get() throws ResourceException {
        System.out.println("getHostRef = " + getHostRef());
        System.out.println("getLocationRef = " + getLocationRef());
        System.out.println("getReference = " + getReference());
        System.out.println("getReferrerRef = " + getReferrerRef());
        System.out.println("getOriginalRef = " + getOriginalRef());
        System.out.println("getRootRef = " + getRootRef());
        System.out.println("getResourceRef = " + getRequest().getResourceRef());
        Map<String, Object> requestAttr = getRequestAttributes();
        for (String requestKey : requestAttr.keySet()) {
            System.out.println(requestKey + " : " + requestAttr.get(requestKey));
        }

        Map<String, Object> dataModel = new HashMap<String, Object>();
        String withHostRef = getOriginalRef().toString().replace(getHostRef().toString(), "");
        String uri;
        if (withHostRef.endsWith("/ui2/")) {
            uri = withHostRef.replace("/ui2/", "");
        } else {
            uri = withHostRef.replace("/ui2", "");
        }

        //TODO work this hard coding out
        dataModel.put("app", new App().setRootUrl(ModelLoader.getModel().getName()).setUri(uri));

        Representation tumlUiFtl = new ClientResource("clap:///org/tuml/ui/tumlui2.html").get();
        return new TemplateRepresentation(tumlUiFtl, dataModel, MediaType.TEXT_HTML);
    }

    public class App {
        private String rootUrl;
        private String uri;

        public String getUri() {
            return uri;
        }

        public App setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public String getRootUrl() {
            return rootUrl;
        }

        public App setRootUrl(String rootUrl) {
            this.rootUrl = rootUrl;
            return this;
        }

    }

}
