/*
 * Unit.java
 *
 * Created on 25. januar 2002, 20:20
 */

package neqsim.util.unit;

/**
 * <p>
 * Unit interface.
 * </p>
 *
 * @author esol
 * @version $Id: $Id
 */
public interface Unit {
  /**
   * <p>
   * getSIvalue.
   * </p>
   *
   * @return a double
   */
  double getSIvalue();

  /**
   * <p>
   * Get process value in specified unit.
   * </p>
   *
   * @param tounit Unit to get process value in.
   * @return Value converted to the specified unit.
   */
  double getValue(String tounit);

  /**
   * <p>
   * getValue.
   * </p>
   *
   * @param val a double
   * @param fromunit a {@link java.lang.String} object
   * @param tounit a {@link java.lang.String} object
   * @return a double
   */
  double getValue(double val, String fromunit, String tounit);
}
