package io.totokaka.strikk.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import org.junit.Test;

import java.lang.annotation.Annotation;

import static org.junit.Assert.*;

public class UtilsTest {
    @Test
    public void testArrayDeclarationInts() throws Exception {
        CodeBlock block = Utils.arrayDeclaration(1);
        assertEquals("{1}", block.toString());

        block = Utils.arrayDeclaration(1, 2);
        assertEquals("{1, 2}", block.toString());

        block = Utils.arrayDeclaration(1, 2, 3);
        assertEquals("{1, 2, 3}", block.toString());
    }

    @Test
    public void testArrayDeclarationString() throws Exception {
        CodeBlock block = Utils.arrayDeclaration("$S", "alpha", "beta", "omega");
        assertEquals("{\"alpha\", \"beta\", \"omega\"}", block.toString());
    }

    @Test
    public void testArrayDeclarationAnnotations() throws Exception {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(Test.class).build();
        CodeBlock block = Utils.arrayDeclaration(annotationSpec, annotationSpec);
        assertEquals("{@org.junit.Test, @org.junit.Test}", block.toString());
    }

}