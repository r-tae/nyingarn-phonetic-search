/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package net.nyingarn.plugin.analysis.phonetic;

import net.nyingarn.PhoneticTokenFilterFactory;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.elasticsearch.plugin.analysis.TokenFilterFactory;
import org.elasticsearch.test.ESTestCase;
import org.hamcrest.MatcherAssert;
import org.junit.Before;

import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.Matchers.instanceOf;

public class SimplePhoneticAnalysisTests extends ESTestCase {

    private TokenFilterFactory tff;

    @Before
    public void setup() throws IOException {
        var settings = new PhoneticTokenFilterFactory.Settings() {
            @Override
            public boolean replace() {
                // TODO: test replace setting, or just remove it entirely
                return false;
            }
        };

        this.tff = new PhoneticTokenFilterFactory(settings);
    }

    public void testPhoneticTokenFilterFactory() throws IOException {
        MatcherAssert.assertThat(tff, instanceOf(PhoneticTokenFilterFactory.class));
    }

    public void testPhoneticTokenFilter() throws IOException {
        Tokenizer tokenizer = new WhitespaceTokenizer();
        tokenizer.setReader(new StringReader("A"));
        String[] expected = new String[] {
            "A" };
        // TODO: implement tests based on python script outputs

        BaseTokenStreamTestCase.assertTokenStreamContents(tff.create(tokenizer), expected);
    }
}
