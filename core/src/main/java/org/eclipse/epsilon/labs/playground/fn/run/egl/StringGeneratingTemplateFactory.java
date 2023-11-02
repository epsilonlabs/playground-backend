package org.eclipse.epsilon.labs.playground.fn.run.egl;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.epsilon.egl.EglTemplate;
import org.eclipse.epsilon.egl.EglTemplateFactory;
import org.eclipse.epsilon.egl.exceptions.EglRuntimeException;

public class StringGeneratingTemplateFactory extends EglTemplateFactory {

	protected Map<String, String> results = new LinkedHashMap<String, String>();
	protected String templateCode = "";
	
	@Override
	public EglTemplate load(URI resource) throws EglRuntimeException {
		try {
			return new StringGeneratingTemplate(new StringGeneratingTemplateSpecification(templateCode), context, resource, results, templateCode);
		} catch (Exception e) {
			throw new EglRuntimeException(e);
		}
	}
	
	public Map<String, String> getResults() {
		return results;
	}

	public String getTemplateCode() {
		return templateCode;
	}
	
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
}