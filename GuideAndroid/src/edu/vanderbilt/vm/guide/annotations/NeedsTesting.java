
package edu.vanderbilt.vm.guide.annotations;

/**
 * Put this annotation above any method or class that has not yet been
 * thoroughly tested. If this annotation is for a class, then it means that the
 * entire class needs to be tested. If it is for a particular method, then it
 * means that only that particular method needs testing.
 * 
 * @author nick
 */
public @interface NeedsTesting {

    String lastTestDate() default "none";

    String lastModifiedDate();

}
