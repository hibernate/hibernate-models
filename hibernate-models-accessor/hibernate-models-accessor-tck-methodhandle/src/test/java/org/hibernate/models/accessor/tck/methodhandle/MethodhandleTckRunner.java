package org.hibernate.models.accessor.tck.methodhandle;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Method Handle TCK tests Runner")
// Defines a "root" package, subpackages are included. Use Include/Exclude ClassNamePatterns annotations to limit the executed tests:
@SelectPackages("org.hibernate.models.accessor.tck")
// Default class pattern does not include IT tests, hence we want to customize it a bit:
@IncludeClassNamePatterns({".*Test"})
public class MethodhandleTckRunner {
}
