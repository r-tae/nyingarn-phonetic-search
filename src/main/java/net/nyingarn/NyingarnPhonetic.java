/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.nyingarn;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import java.util.LinkedHashMap;


/**
 * <p>Algorithm description: (not comprehensive)
 * <pre>
 * 1. replace unicode diacritics with ASCII versions
 * 2. remove macron from vowels
 * 3. replace diphthong combinations with `-y-`/`-w-`and initial i/u-&gt;yi/wu
 * 4. `-ow/-aw` -&gt; AWU
 * 5. `ah` `er` `uh` `ar` -&gt; A
 * 6. EN/EL -&gt; IN/IL
 * 7. long vowel -&gt; short vowel
 * 8. UA/UI/OA -&gt; UWA/UWI
 * 9. remove double letters (except for `rr`)
 * 10. enye -&gt; NG
 * 11. YNY/YLY/YN/YL -&gt; NY/LY
 * 12. double digraphs -&gt; single digraph (e.g., NGNG -&gt; NG)
 * 13. final Y -&gt; AYI
 * 14. initial G -&gt; K
 * 15. single letter replacements
 *   i. B -&gt; P
 *   ii. D -&gt; T
 *   iii. O -&gt; U
 *   iv. E -&gt; I
 * 16. G -&gt; K (except for `NG`)
 * 17. di- and tri-graphs -&gt; J
 * 18. S -&gt; J
 * 19. C -&gt; K, WH -&gt; W
 * 20. WU -&gt; U (except initially or after `A `I` or `U`)
 * 21. WA/WI/WU -&gt; AWA/AWI/AWU (except initially or after `AIU`)
 * 22. RA/RI/RU -&gt; URA/URI/URU (except initially or after `AIUR`)
 * 23. Y -&gt; AYI (except next to AIU, or after `N` or `L`)
 * 24. AY -&gt; AYI (except before AIU)
 * 25. Y -&gt; I (except after AIUNLT)
 * 26. remove special characters and digits
 * </pre>
 */
public class NyingarnPhonetic implements StringEncoder {
    /**
     * Creates an instance of the {@link NyingarnPhonetic} encoder with strict mode
     * (original form), i.e. encoded strings have a maximum length of 6.
     */
    public NyingarnPhonetic() {
    }

    /**
     * Encodes an Object using the NyingarnPhonetic algorithm. This method is provided in
     * order to satisfy the requirements of the Encoder interface, and will
     * throw an {@link EncoderException} if the supplied object is not of type
     * {@link String}.
     *
     * @param obj Object to encode
     * @return An object (or a {@link String}) containing the NyingarnPhonetic code which
     * corresponds to the given String.
     * @throws EncoderException if the parameter supplied is not of a {@link String}
     * @throws IllegalArgumentException if a character is not mapped
     */
    @Override
    public Object encode(Object obj) throws EncoderException {
        if (!(obj instanceof String)) {
            throw new EncoderException("Parameter supplied to NyingarnPhonetic encode is not of type java.lang.String");
        }
        return this.NyingarnPhonetic((String) obj);
    }

    /**
     * Encodes a String using the NyingarnPhonetic algorithm.
     *
     * @param str A String object to encode
     * @return A NyingarnPhonetic code corresponding to the String supplied
     * @throws IllegalArgumentException if a character is not mapped
     */
    @Override
    public String encode(String str) {
        return this.NyingarnPhonetic(str);
    }

    /**
     * Retrieves the NyingarnPhonetic code for a given String object.
     *
     * @param str String to encode using the NyingarnPhonetic algorithm
     * @return A NyingarnPhonetic code for the String supplied
     */
    public String NyingarnPhonetic(String str) {
        if (str == null) {
            return null;
        }

        // Use the same clean rules as Soundex
        str = clean(str);

        if (str.isEmpty()) {
            return str;
        }

        var next_pass = standardise(str);
        while (!str.equals(next_pass)) {
            str = next_pass;
            next_pass = standardise(str);
        }

        return str;
    }

    private static String standardise(String str) {
        var simpleReplacements = new LinkedHashMap<String,String>();
        /* The rest of this algorithm is not built for this space replacement
         * I believe its not necessary though, since elastic should tokenise words
         * to leave out punctuation.
         */
        // str = str.replace("[!,?\"]", " ");

        // This replacement specifically to turn retracted `td` (both with COMBINING-MINUS-SIGN-BELOW diacritic) into `RTD`
        str = str.replace("([TD])\\u0320([TD])\\u0320", "r$1$2");
        str = str.replace("([A-Z])\\u0320", "r$1");
        str = str.replace("Ñ", "NY");
        str = str.replace("Œ", "OE");
        str = str.replace("Û", "OOE");
        str = str.replace("Ä", "AA");

        // TODO: what do I replace schwa with: `uh` or `e`?
        // str = str.replace("ə", "e")

        // TODO: ask about doing `rr` here instead
        str = str.replace("Ʀ", "R");
        str = str.replace("Θ", "TH");
        // TODO: not sure about what to replace eth with
        str = str.replace("Ð", "TH");
        str = str.replace("RNGU","RNKU");

        str = str.replace("Ā", "A");
        str = str.replace("Ē", "I");
        str = str.replace("Ī", "I");
        str = str.replace("Ō", "O");
        str = str.replace("Ū", "U");
        str = str.replace("'", "");

        // TODO: [ui]r$ -> $1rr

        str = str.replace("AIOO", "AYU");
        str = str.replace("AIU", "AYU");
        str = str.replace("AUA", "AWA");
        str = str.replace("AUE", "AWA");
        str = str.replace("UAI", "UWAYI");
        str = str.replace("OOI", "UWI");
        str = str.replace("AUI", "AWUYI");
        str = str.replace("NGG", "NGK");
        str = str.replace("A-I", "AYI");
        str = str.replace("E-I", "AYI");

        str = str.replaceFirst("^I", "YI");
        str = str.replaceFirst("^U", "WU");

        str = str.replace("EE", "I");
        str = str.replace("AIA", "AYA");
        str = str.replace("AI", "AYI");
        str = str.replace("IA", "IYA");
        str = str.replace("EA", "IYA");
        str = str.replace("EI", "AYI");
        str = str.replace("IU", "IWU");
        str = str.replace("AU", "AWU");

        str = str.replaceFirst("OW$", "AWU");
        str = str.replaceFirst("AW$", "AWU");

        str = str.replaceFirst("[OA]W([^AEIOU])", "AWU$1");

        // TODO: this might be useless
        str = str.replace("AA", "A");
        str = str.replaceFirst("AH([^AEIOU])", "A$1");

        str = str.replaceFirst("A[HR]$", "A");
        str = str.replace("UH", "A");
        str = str.replaceFirst("ER$", "A");

        str = str.replaceFirst("E([NL])$", "I$1");

        str = str.replace("EL","AL");
        str = str.replace("OOA","UWA");
        str = str.replace("OO","U");
        // TODO: these two might be useless
        str = str.replace("UU","U");
        str = str.replace("II","I");

        str = str.replace("UA","UWA");
        str = str.replace("UI","UWI");
        str = str.replace("OA","UWA");

        str = str.replaceAll("([^R])\\1","$1");

        str = str.replace("Ŋ", "NG");

        str = str.replace("YNY","NY");
        str = str.replace("YN", "NY");
        str = str.replace("YLY", "LY");
        str = str.replace("YL", "LY");

        str = str.replace("NHNH","NH");
        str = str.replace("NGNG","NG");
        str = str.replace("NYNY","NY");
        str = str.replace("RNRN","RN");
        str = str.replace("RLRL","RL");
        str = str.replace("LHLH","LH");
        str = str.replace("LYLY","LY");
        str = str.replace("RTRT","RT");
        str = str.replace("THTH","TH");

        str = str.replaceFirst("Y$", "AYI");
        str = str.replaceFirst("^G", "K");
        str = str.replaceFirst("B", "P");
        str = str.replaceFirst("D", "T");
        str = str.replaceFirst("O", "U");
        str = str.replaceFirst("E", "I");

        str = str.replaceFirst("([^N])G", "$1K");

        str = str.replace("TCH","J");
        str = str.replace("SCH","J");
        str = str.replace("TY","J");
        str = str.replace("TJ","J");
        str = str.replace("SJ","J");
        str = str.replace("STH","J");
        str = str.replace("S","J");

        str = str.replace("C","K");
        str = str.replace("WH","W");

        str = str.replaceAll("([^AIU ])WU", "$1U");
        str = str.replaceAll("([^AIU ])W([AIU])", "$1UW$2");
        str = str.replaceAll("(^| )([^AIUR ])R([AIU])", "$1$2UR$3");
        str = str.replaceAll("([^AIUNL])Y([^IUA])", "$1AYI$2");
        str = str.replaceAll("AY([^IUA])", "AYI$1");
        str = str.replaceAll("([^NLTAIU ])Y", "$1I");

        str = str.replaceFirst("[()*+,-./:;<=>?@\\[\\]\\\\^_]", "");
        str = str.replaceFirst("[0-9]", "");

        return str;
    }

    static String clean(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        int len = str.length();
        char[] chars = new char[len];
        int count = 0;
        for (int i = 0; i < len; i++) {
            if (Character.isLetter(str.charAt(i))) {
                chars[count++] = str.charAt(i);
            }
        }
        if (count == len) {
            return str.toUpperCase(java.util.Locale.ENGLISH);
        }
        return new String(chars, 0, count).toUpperCase(java.util.Locale.ENGLISH);
    }
}
