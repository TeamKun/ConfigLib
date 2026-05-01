package net.kunmc.lab.configlib.processor;

import net.kunmc.lab.configlib.Value;
import net.kunmc.lab.configlib.annotation.ConfigNullable;
import net.kunmc.lab.configlib.annotation.Description;
import net.kunmc.lab.configlib.annotation.Masked;
import net.kunmc.lab.configlib.annotation.Range;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes({"net.kunmc.lab.configlib.annotation.Range", "net.kunmc.lab.configlib.annotation.ConfigNullable", "net.kunmc.lab.configlib.annotation.Description", "net.kunmc.lab.configlib.annotation.Masked"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public final class ConfigLibAnnotationProcessor extends AbstractProcessor {
    private Types types;
    private Elements elements;
    private Messager messager;
    private TypeMirror numberType;
    private TypeMirror valueType;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.types = processingEnv.getTypeUtils();
        this.elements = processingEnv.getElementUtils();
        this.messager = processingEnv.getMessager();
        this.numberType = elements.getTypeElement(Number.class.getCanonicalName())
                                  .asType();
        TypeElement valueElement = elements.getTypeElement(Value.class.getCanonicalName());
        this.valueType = valueElement == null ? null : valueElement.asType();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        validateRanges(roundEnv);
        validateConfigNullable(roundEnv);
        warnPojoAnnotationsOnValueFields(roundEnv);
        warnMaskedOnIgnoredFields(roundEnv);
        return false;
    }

    private void validateRanges(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Range.class)) {
            if (!isFieldLike(element)) {
                error(element, "@Range can only be used on numeric POJO leaf fields.");
                continue;
            }

            Range range = element.getAnnotation(Range.class);
            if (range.min() > range.max()) {
                error(element, "@Range min must be less than or equal to max.");
            }
            if (!isNumeric(element.asType())) {
                error(element,
                      "@Range can only be used on numeric POJO leaf fields. Put it on a numeric field inside a section/object field.");
            }
            if (isValueField(element)) {
                error(element, "Value fields must use constructor bounds or addValidator(...) instead of @Range.");
            }
        }
    }

    private void validateConfigNullable(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(ConfigNullable.class)) {
            if (!isFieldLike(element)) {
                error(element, "@ConfigNullable can only be used on POJO fields.");
                continue;
            }
            if (element.asType()
                       .getKind()
                       .isPrimitive()) {
                error(element, "@ConfigNullable cannot be used on primitive fields.");
            }
            if (isValueField(element)) {
                error(element,
                      "Value fields define nullability through their type and validators instead of @ConfigNullable.");
            }
        }
    }

    private void warnPojoAnnotationsOnValueFields(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Description.class)) {
            if (isValueField(element)) {
                error(element, "Value fields should use description(...) instead of @Description.");
            }
        }
    }

    private void warnMaskedOnIgnoredFields(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Masked.class)) {
            if (!isFieldLike(element)) {
                error(element, "@Masked can only be used on config fields.");
                continue;
            }
            Set<Modifier> modifiers = element.getModifiers();
            if (modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.TRANSIENT)) {
                warning(element, "@Masked has no effect on static or transient fields because ConfigLib ignores them.");
            }
        }
    }

    private boolean isFieldLike(Element element) {
        return element.getKind() == ElementKind.FIELD || element.getKind()
                                                                .name()
                                                                .equals("RECORD_COMPONENT");
    }

    private boolean isNumeric(TypeMirror type) {
        TypeKind kind = type.getKind();
        if (kind.isPrimitive()) {
            return kind == TypeKind.BYTE || kind == TypeKind.SHORT || kind == TypeKind.INT || kind == TypeKind.LONG || kind == TypeKind.FLOAT || kind == TypeKind.DOUBLE;
        }
        return types.isAssignable(types.erasure(type), types.erasure(numberType));
    }

    private boolean isValueField(Element element) {
        if (valueType == null || !isFieldLike(element)) {
            return false;
        }
        return types.isAssignable(types.erasure(element.asType()), types.erasure(valueType));
    }

    private void error(Element element, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }

    private void warning(Element element, String message) {
        messager.printMessage(Diagnostic.Kind.WARNING, message, element);
    }
}
