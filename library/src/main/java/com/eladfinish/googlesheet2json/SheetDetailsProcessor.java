package com.eladfinish.googlesheet2json;


import com.eladfinish.googlesheet2json.annotations.SheetDetails;
import com.eladfinish.googlesheet2json.misc.JavaKeywords;
import com.eladfinish.googlesheet2json.misc.TextUtils;
import com.eladfinish.googlesheet2json.model.BaseSheetEntry;
import com.eladfinish.googlesheet2json.model.Sheet;
import com.eladfinish.googlesheet2json.model.SheetEntryInterface;
import com.google.gson.annotations.SerializedName;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;


@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SheetDetailsProcessor extends AbstractProcessor {

    private Filer filer;
    private Elements elementUtils;
//    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        elementUtils = processingEnvironment.getElementUtils();
//        typeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            annotations.add(annotation.getCanonicalName());
        }

        return annotations;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new HashSet<>();
        annotations.add(SheetDetails.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

//        boolean hasError = false;

        Set<? extends Element> sheetDetailsElement =
                roundEnvironment.getElementsAnnotatedWith(SheetDetails.class);
//        for (Element element : sheetDetailsElement) {
//            showMessage("element: " + element.toString());
//        }

        Map<TypeElement, Set<Element>> map = getMapElements(sheetDetailsElement);
//        showMessage("map: " + map.toString());

        processElement(map);

        return true;
    }

    private Map<TypeElement, Set<Element>> getMapElements(Set<? extends Element> elements) {
        // collecting all @... annotations.
        Map<TypeElement, Set<Element>> mapElements = new LinkedHashMap<>();
        for (Element element : elements) {
            TypeElement classType = (TypeElement) element.getEnclosingElement();
            if (!mapElements.containsKey(classType)) {
                mapElements.put(classType, new LinkedHashSet<>());
            }
            mapElements.get(classType).add(element);
        }

        return mapElements;
    }

    private void processElement(Map<TypeElement, Set<Element>> mapElements) {

        for (Map.Entry<TypeElement, Set<Element>> entry : mapElements.entrySet()) {

            TypeElement classElement = entry.getKey();
//            final String className = classElement.getSimpleName().toString();
//            showMessage("className: " + className);

            for (Element element : entry.getValue()) {

                SheetDetails annotation = element.getAnnotation(SheetDetails.class);

//                String varName = element.getSimpleName().toString();
                String entryName = annotation.entryName();
                if (!isValidClassName(entryName)) {
                    showError(classElement, "'%s' is not a valid name.", entryName);
                    break;
                }

                String[] labels = annotation.labels();
                String[] fields = annotation.fields();

                if (fields.length != labels.length) {
                    if (labels.length != 0) {
                        showError(classElement,
                                "fields.length (%s) must equal to labels.length (%s).",
                                fields.length, labels.length);
                        break;
                    } else { // if labels array is zero length, labels and fields are the same
                        labels = fields.clone();
                    }
                }

                sheetEntryGenerator(entryName, labels, fields, classElement);

            }
        }

    }

    private void sheetEntryGenerator(String entryName, String[] labels, String[] fields, TypeElement classElement) {
        int size = fields.length;

        //WordUtils.capitalizeFully
        String className = TextUtils.capitalize(entryName) + "_SheetEntry";
        className = TextUtils.capitalizeEachWord(className);
        TypeSpec.Builder sheetEntryBuilder = TypeSpec.classBuilder(className)
                .addSuperinterface(SheetEntryInterface.class)
                .superclass(BaseSheetEntry.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);


        //  expected output:
        //  private static final String p =  "gsx$";
//        FieldSpec gsxFieldSpec = FieldSpec.builder(String.class, "p")
//                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
//                .initializer("$S", "gsx$")
//                .build();
//
//        sheetEntryBuilder.addField(gsxFieldSpec);


        for (int i = 0; i < size; i++) {
            //  expected output:
            //  private static final String header1 = "question";
//            String label = labels[i].toLowerCase();
            FieldSpec headerFieldSpec = FieldSpec.builder(String.class, "header" + (i + 1))
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", labels[i].toLowerCase())
                    .build();

            sheetEntryBuilder.addField(headerFieldSpec);
        }

        for (int i = 0; i < size; i++) {
            //  expected output:
            //  @SerializedName(p + header1)
            AnnotationSpec annotationSpec = AnnotationSpec.builder(SerializedName.class)
                    .addMember("value", "p + header" + (i + 1))
                    .build();

            //  expected output:
            //  private Sheet.Text <fields[i]>;
            FieldSpec memberFieldSpec = FieldSpec.builder(Sheet.Text.class, fields[i])
                    .addModifiers(Modifier.PRIVATE)
                    .addAnnotation(annotationSpec)
                    .build();

            sheetEntryBuilder.addField(memberFieldSpec);
        }


        StringBuilder fieldToString = new StringBuilder();

        for (String field : fields) {

            if (Arrays.asList(JavaKeywords.keywords).contains(field)) {
                showError(classElement, "'%s' is a java keyword", field);
                return;
            }

            //  expected output:
            //  public String get<Field>() {
            ////        return <field>.toString();
            //        return <field> == null ? null : <field>.toString();
            //  }
            String methodName = "get" + TextUtils.capitalize(field);
            MethodSpec getter = MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(String.class)
//                    .addStatement("return " + field + ".toString()")
                    .addStatement("return " + field + " == null ? null : " + field + ".toString()")
                    .build();

            sheetEntryBuilder.addMethod(getter);

            fieldToString.append(" \"").append(field).append(" = \"")
                    .append(" + ").append(field).append(" + '\\n' \n+");
        }


        String format = "return \"{\" + $S + '\\n' \n+" +
                fieldToString.toString() +
                " '}'";

        //  expected output:
        //  public String toString() {
        //
        //  }
        MethodSpec toString = MethodSpec.methodBuilder("toString")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
//                .addAnnotation(NotNull.class)
                .addAnnotation(Override.class)
                .addStatement(format, className) //"SheetEntry: " + className
                .build();


        sheetEntryBuilder.addMethod(toString);

        StringBuilder details = new StringBuilder();
        details.append("return this.getClass().getSimpleName()").append(" + '\\n' \n");

        for (int i = 0; i < size; i++) {
            details.append("+ \"").append("header").append(i + 1).append(" = \"")
                    .append(" + ").append("header").append(i + 1).append(" + '\\n' \n");
        }
        for (int i = 0; i < size; i++) {
            details.append("+ \"").append("field").append(i + 1).append(" = \"")
                    .append(" + \"").append(fields[i]).append("\" + '\\n' \n");
        }

        MethodSpec getSheetEntryDetails = MethodSpec.methodBuilder("getSheetEntryDetails")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
//                .addAnnotation(NotNull.class)
                .addAnnotation(Override.class)
                .addStatement(details.toString())
                .build();

        sheetEntryBuilder.addMethod(getSheetEntryDetails);


        TypeSpec sheetEntryClass = sheetEntryBuilder.build();

        //"com.eladfinish.googlesheet2json"
        String packageName = elementUtils.getPackageOf(classElement).toString();
        JavaFile javaFile = JavaFile.builder(packageName, sheetEntryClass).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
            showError(classElement, "%s cannot write", classElement.getSimpleName());
        }

    }


    private boolean isValidClassName(String format) {
        return format != null && format.trim().length() > 0;
    }


    private void showError(Element element, String message, Object... args) {
        printMessage(element, message, args);
    }

    private void showMessage(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, message);
    }

    private void printMessage(Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }

}
