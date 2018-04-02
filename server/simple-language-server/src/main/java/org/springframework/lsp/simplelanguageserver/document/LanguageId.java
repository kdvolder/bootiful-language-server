/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.lsp.simplelanguageserver.document;

/**
 * Central place to define constants for the known language-ids that we care
 * about.
 *
 * @author Kris De Volder
 */
public class LanguageId {

	public static final LanguageId PLAINTEXT = of("plaintext");
	public static final LanguageId CONCOURSE_TASK = of("concourse-task-yaml");
	public static final LanguageId CONCOURSE_PIPELINE = of("concourse-pipeline-yaml");
	public static final LanguageId CF_MANIFEST = of("manifest-yaml");
	public static final LanguageId JAVA = of("java");
	public static final LanguageId YAML = of("yaml");
	
	public static final LanguageId BOSH_DEPLOYMENT = of("bosh-deployment-manifest");
	public static final LanguageId BOSH_CLOUD_CONFIG = of("bosh-cloud-config");
	public static final LanguageId BOOT_PROPERTIES = of("spring-boot-properties");
	public static final LanguageId BOOT_PROPERTIES_YAML = of("spring-boot-properties-yaml");

	private final String id;

	private LanguageId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LanguageId other = (LanguageId) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LanguageId ["+ id + "]";
	}

	public String getId() {
		return id;
	}

	public static LanguageId of(String languageId) {
		return new LanguageId(languageId);
	}
}