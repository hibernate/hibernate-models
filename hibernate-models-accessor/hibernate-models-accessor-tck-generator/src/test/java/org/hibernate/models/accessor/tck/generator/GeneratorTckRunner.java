/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.generator;

import org.junit.platform.suite.api.ExcludeClassNamePatterns;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName( "Build-time Generator TCK tests Runner" )
@SelectPackages( "org.hibernate.models.accessor.tck" )
@IncludeClassNamePatterns( { ".*Test" } )
@ExcludeClassNamePatterns( { ".*MultiValue.*", ".*InterfaceMethod.*", ".*MemberValidation.*" } )
public class GeneratorTckRunner {
}
