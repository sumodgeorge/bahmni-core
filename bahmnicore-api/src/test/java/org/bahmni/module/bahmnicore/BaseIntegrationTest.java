package org.bahmni.module.bahmnicore;

//import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;
import org.junit.Ignore;
import org.springframework.test.context.ContextConfiguration;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

@Ignore
@ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BaseIntegrationTest extends BaseModuleWebContextSensitiveTest {
}
