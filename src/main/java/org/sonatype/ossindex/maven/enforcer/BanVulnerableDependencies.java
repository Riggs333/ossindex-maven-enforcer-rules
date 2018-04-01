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

import org.apache.maven.enforcer.rule.api.EnforcerRule2;
import org.apache.maven.enforcer.rule.api.EnforcerLevel;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ???
 *
 * @since ???
 */
public class BanVulnerableDependencies
    implements EnforcerRule2
{
    private EnforcerLevel level = EnforcerLevel.ERROR;

    public void setLevel(final EnforcerLevel level) {
        this.level = level;
    }

    @Nonnull
    @Override
    public EnforcerLevel getLevel() {
        return level;
    }

    @Override
    public void execute(@Nonnull EnforcerRuleHelper helper) throws EnforcerRuleException {
        // TODO:
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    @Nullable
    @Override
    public String getCacheId() {
        return "0";
    }

    @Override
    public boolean isResultValid(@Nonnull EnforcerRule rule) {
        return false;
    }
}
