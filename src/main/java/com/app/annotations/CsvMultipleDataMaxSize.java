package com.app.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 配列またはリストの最大のサイズを指定する
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE,
    ElementType.FIELD,
    ElementType.CONSTRUCTOR,
    ElementType.METHOD
})
public @interface CsvMultipleDataMaxSize {
	int maxSize();
}
