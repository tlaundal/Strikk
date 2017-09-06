package io.totokaka.strikk.processor.strikk;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.TypeKindVisitor7;

class GetNameVisitor extends TypeKindVisitor7<String, Void> {

    @Override
    protected String defaultAction(TypeMirror typeMirror, Void aVoid) {
        return "Uknown";
    }

    @Override
    public String visitDeclared(DeclaredType declaredType, Void aVoid) {
        return declaredType.asElement().getSimpleName().toString();
    }
}
