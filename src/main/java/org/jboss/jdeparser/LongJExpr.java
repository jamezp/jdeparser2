/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.jdeparser;

import java.io.IOException;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
class LongJExpr extends AbstractJExpr implements JExpr {

    private final long val;
    private final int radix;
    private final int sepInterval; // todo

    LongJExpr(final long val, final int radix, final int sepInterval) {
        super(0);
        this.val = val;
        this.radix = radix;
        this.sepInterval = sepInterval;
    }

    void writeDirect(final SourceFileWriter writer) throws IOException {
        writer.addWordSpace();
        writer.write(Tokens.$NUMBER);
        switch (radix) {
            case 2: writer.writeRaw("0b"); break;
            case 16: writer.writeRaw("0x"); break;
        }
        writer.writeRaw(Long.toString(val, radix) + "L");
    }
}
