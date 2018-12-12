package com.ddd.dev.generator.codegenerator.java.service;

import com.ddd.dev.generator.codegenerator.EntityClassGenerator;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Component;

@Component
public class ServiceGenerator   extends EntityClassGenerator {
	private String templateFile="frameResource/java/Service.vm";


	@Override
	public String getTemplateFile() {
		return this.templateFile;
	}
}
