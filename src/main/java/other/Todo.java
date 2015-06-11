package other;

/**
 * Since i like having annotations and commenting //TOOD can get messy here is a better way...
 * 
 * Example:
 * ---- > @Todo("Nothing, this class is finished")
 * 
 * @author Goblom
 */

public @interface Todo {
    String value() default "";
}
