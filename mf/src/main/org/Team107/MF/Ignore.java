package org.Team107.MF;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Java annotation for JSON (de-)serialization;
 * Variables annotated with this are ignored during reconstruction and serialization of objects.
 * Variables that are important for the consistency of an object must not be annotated using
 * \@org.Team107.MF.Ignore because this will leave a reconstructed object in an illegal inconsistent state.
 *
 * @author Fabian Schneider
 */
@Retention(RetentionPolicy.RUNTIME)     // must me available at runtime for (de-)serialization
@Target(ElementType.FIELD)
// can only be applied to variables not to methods as only variables are (de-)serialized
public @interface Ignore {
}
