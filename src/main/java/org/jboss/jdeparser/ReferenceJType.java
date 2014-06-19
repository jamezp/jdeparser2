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
class ReferenceJType extends AbstractJType {
    private final ReferenceJType enclosingType;
    private final PrimitiveJType unboxed;
    private final String packageName;
    private final String simpleName;

    ReferenceJType(final ReferenceJType enclosingType, final String packageName, final String simpleName) {
        this.enclosingType = enclosingType;
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.unboxed = null;
    }

    ReferenceJType(final ReferenceJType enclosingType, final String packageName, final String simpleName, final PrimitiveJType unboxed) {
        this.enclosingType = enclosingType;
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.unboxed = unboxed;
    }

    static ReferenceJType of(JType type) {
        final AbstractJType type1 = AbstractJType.of(type);
        if (type1 instanceof ReferenceJType) {
            return (ReferenceJType) type1;
        }
        throw new IllegalArgumentException("Expected a reference type");
    }

    public String simpleName() {
        return simpleName;
    }

    public String binaryName() {
        return enclosingType == null ? simpleName : enclosingType.binaryName() + "$" + simpleName;
    }

    public String qualifiedName() {
        return enclosingType == null ? packageName == null || packageName.isEmpty() ? binaryName() : packageName + "." + binaryName() : enclosingType.qualifiedName() + "$" + simpleName;
    }

    public JExpr _class() {
        return new StaticRefJExpr(this, "class");
    }

    public JExpr _this() {
        return new StaticRefJExpr(this, "this");
    }

    public JExpr _super() {
        return new StaticRefJExpr(this, "super");
    }

    public JCall _new(final JExpr dim) {
        return new NewJCall(this);
    }

    public JType unbox() {
        return unboxed == null ? this : unboxed;
    }

    void writeDirect(final SourceFileWriter sourceFileWriter) throws IOException {
        final ImplJClassFile cf = sourceFileWriter.getClassFile();
        final String currentPackageName = cf.getPackageName();
        final boolean packageMatches = currentPackageName.equals(packageName);
        if (packageMatches && cf.hasImport(simpleName())) {
            // an explicit import masks the implicit import
            sourceFileWriter.writeClass(qualifiedName());
        } else if (packageName.equals("java.lang") && ! sourceFileWriter.getClassFile().getSources().hasClass(currentPackageName + "." + simpleName()) || packageMatches) {
            // implicit import
            sourceFileWriter.writeClass(simpleName());
        } else {
            sourceFileWriter.writeClass(qualifiedName());
        }
    }

    public JType typeArg(final JType... args) {
        if (unboxed != null) return super.typeArg(args);
        return new NarrowedJType(this, args);
    }

    JType nestedClass(final String name) {
        return new ReferenceJType(this, packageName, name);
    }

    public String toString() {
        return qualifiedName();
    }
}
