/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.sleuth.autoconfig;

import java.security.SecureRandom;

import brave.Tracing;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TraceAutoConfigurationWithDisabledSleuthTests.Config.class,
		properties = "spring.sleuth.enabled=false",
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("disabled")
@DirtiesContext
@ExtendWith(OutputCaptureExtension.class)
public class TraceAutoConfigurationWithDisabledSleuthTests {

	private static final Log log = LogFactory
			.getLog(TraceAutoConfigurationWithDisabledSleuthTests.class);

	@Autowired(required = false)
	Tracing tracing;

	@Test
	public void shouldStartContext() {
		BDDAssertions.then(this.tracing).isNull();
	}

	@Test
	public void shouldNotContainAnyTracingInfoInTheLogs(CapturedOutput capture) {
		log.info("hello");

		BDDAssertions.then(capture.toString()).doesNotContain("[foo");
	}

	@EnableAutoConfiguration
	@Configuration
	static class Config {

		@Bean
		public FactoryBean<SecureRandom> secureRandom() {
			return new FactoryBean<SecureRandom>() {

				@Override
				public SecureRandom getObject() throws Exception {
					return new SecureRandom();
				}

				@Override
				public Class<?> getObjectType() {
					return SecureRandom.class;
				}

				@Override
				public boolean isSingleton() {
					return true;
				}
			};
		}

	}

}
