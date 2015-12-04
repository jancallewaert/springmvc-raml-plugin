/*
 * Copyright 2002-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlapisync.plugin;

import java.io.IOException;
import java.lang.annotation.Annotation;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;

/**
 * Maven Plugin MOJO specific to Spring MVC Projects.
 * 
 * @author Kurt Paris
 * @author Micheal Schembri Wismayer
 * @since 0.0.1
 */
@Mojo(name = "generate-springmvc-api-docs", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class SpringMvcRamlApiSyncMojo extends CommonApiSyncMojo {

	/**
	 * IF this is set to true, we will only parse methods that consume, produce or accept the requested defaultMediaType
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean restrictOnMediaType;

	@SuppressWarnings("unchecked")
	@Override
	protected Class<? extends Annotation>[] getSupportedClassAnnotations() {
		return new Class[] { Controller.class, RestController.class };
	}

	protected void generateRaml() throws MojoExecutionException, MojoFailureException, IOException {

		super.generateRaml();

		Class<?>[] classArray = new Class[annotatedClasses.size()];
		classArray = this.annotatedClasses.toArray(classArray);
		ResourceParser scanner = new SpringMvcResourceParser(project.getBasedir().getParentFile() != null ? project
				.getBasedir().getParentFile() : project.getBasedir(), version, defaultMediaType, restrictOnMediaType);
		RamlGenerator ramlGenerator = new RamlGenerator(scanner);
		// Process the classes selected and build Raml model
		ramlGenerator
				.generateRamlForClasses(project.getArtifactId(), version, restBasePath, classArray, this.documents);

		// Extract RAML as a string and save to file
		ramlGenerator.outputRamlToFile(this.getFullRamlOutputPath());
	}

}
