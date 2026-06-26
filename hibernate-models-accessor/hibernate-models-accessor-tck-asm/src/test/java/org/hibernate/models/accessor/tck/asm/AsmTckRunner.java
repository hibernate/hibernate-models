package org.hibernate.models.accessor.tck.asm;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("ASM TCK tests Runner")
@SelectPackages("org.hibernate.models.accessor.tck")
@IncludeClassNamePatterns({".*Test"})
public class AsmTckRunner {
}
