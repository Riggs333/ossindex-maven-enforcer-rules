/*
 * Copyright (c) 2018-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.ossindex.maven.enforcer;

import org.sonatype.goodies.testsupport.TestSupport;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link BanVulnerableDependencies}.
 */
public class BanVulnerableDependenciesTest
    extends TestSupport
{
  private BanVulnerableDependencies underTest;

  @Before
  public void setUp() throws Exception {
    underTest = new BanVulnerableDependencies();
  }

  @Test
  public void dummy() {
    // FIXME: implement sane tests; here to prevent CI from complaining
  }
}
