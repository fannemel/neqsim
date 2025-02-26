/*
 * GasScrubberSimple.java
 *
 * Created on 12. mars 2001, 19:48
 */

package neqsim.processSimulation.processEquipment.separator;

import java.util.UUID;
import neqsim.processSimulation.mechanicalDesign.separator.GasScrubberMechanicalDesign;
import neqsim.processSimulation.processEquipment.stream.Stream;
import neqsim.processSimulation.processEquipment.stream.StreamInterface;
import neqsim.thermo.system.SystemInterface;
import neqsim.thermodynamicOperations.ThermodynamicOperations;

/**
 * <p>
 * GasScrubberSimple class.
 * </p>
 *
 * @author Even Solbraa
 * @version $Id: $Id
 */
public class GasScrubberSimple extends Separator {
  private static final long serialVersionUID = 1000;

  SystemInterface gasSystem;
  SystemInterface waterSystem;
  SystemInterface liquidSystem;
  SystemInterface thermoSystemCloned;

  StreamInterface inletStream;
  StreamInterface gasOutStream;
  StreamInterface liquidOutStream;

  /**
   * <p>
   * Constructor for GasScrubberSimple.
   * </p>
   */
  @Deprecated
  public GasScrubberSimple() {
    this("GasScrubberSimple");
  }

  /**
   * <p>
   * Constructor for GasScrubberSimple.
   * </p>
   *
   * @param inletStream a {@link neqsim.processSimulation.processEquipment.stream.Stream} object
   */
  @Deprecated
  public GasScrubberSimple(StreamInterface inletStream) {
    this("GasScrubberSimple", inletStream);
  }

  /**
   * Constructor for GasScrubberSimple.
   *
   * @param name name of scrubber
   */
  public GasScrubberSimple(String name) {
    super(name);
    this.setOrientation("vertical");
  }

  /**
   * <p>
   * Constructor for GasScrubberSimple.
   * </p>
   *
   * @param name a {@link java.lang.String} object
   * @param inletStream a {@link neqsim.processSimulation.processEquipment.stream.Stream} object
   */
  public GasScrubberSimple(String name, StreamInterface inletStream) {
    super(name, inletStream);
    this.setOrientation("vertical");
  }

  /**
   * {@inheritDoc}
   *
   * @return a
   *         {@link neqsim.processSimulation.mechanicalDesign.separator.GasScrubberMechanicalDesign}
   *         object
   */
  @Override
  public GasScrubberMechanicalDesign getMechanicalDesign() {
    return new GasScrubberMechanicalDesign(this);
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * Setter for the field <code>inletStream</code>.
   * </p>
   */
  public void setInletStream(StreamInterface inletStream) {
    this.inletStream = inletStream;

    thermoSystem = inletStream.getThermoSystem().clone();
    gasSystem = thermoSystem.phaseToSystem(0);
    gasOutStream = new Stream("gasOutStream", gasSystem);

    thermoSystem = inletStream.getThermoSystem().clone();
    liquidSystem = thermoSystem.phaseToSystem(1);
    liquidOutStream = new Stream("liquidOutStream", liquidSystem);
  }

  /** {@inheritDoc} */
  @Override
  public StreamInterface getLiquidOutStream() {
    return liquidOutStream;
  }

  /** {@inheritDoc} */
  @Override
  public StreamInterface getGasOutStream() {
    return gasOutStream;
  }

  /** {@inheritDoc} */
  @Override
  public StreamInterface getGas() {
    return getGasOutStream();
  }

  /** {@inheritDoc} */
  @Override
  public StreamInterface getLiquid() {
    return getLiquidOutStream();
  }

  /** {@inheritDoc} */
  @Override
  public void run(UUID id) {
    thermoSystem = inletStream.getThermoSystem().clone();
    ThermodynamicOperations thermoOps = new ThermodynamicOperations(thermoSystem);
    thermoOps.TPflash();
    if (separatorSection.size() > 0) {
      calcLiquidCarryoverFraction();
      thermoSystem.addLiquidToGas(getLiquidCarryoverFraction());
    }
    gasSystem = thermoSystem.phaseToSystem(0);
    gasSystem.setNumberOfPhases(1);
    gasOutStream.setThermoSystem(gasSystem);

    thermoSystem = inletStream.getThermoSystem().clone();
    if (separatorSection.size() > 0) {
      thermoSystem.addGasToLiquid(getGasCarryunderFraction());
      liquidSystem = thermoSystem.phaseToSystem(1);
    }
    liquidSystem.setNumberOfPhases(1);
    liquidOutStream.setThermoSystem(liquidSystem);
    setCalculationIdentifier(id);
  }

  /**
   * <p>
   * calcLiquidCarryoverFraction.
   * </p>
   *
   * @return a double
   */
  public double calcLiquidCarryoverFraction() {
    double ktotal = 1.0;

    for (int i = 0; i < separatorSection.size(); i++) {
      ktotal *= (1.0 - separatorSection.get(i).getEfficiency());
    }
    System.out.println("Ktot " + (1.0 - ktotal));
    double area = getInternalDiameter() * getInternalDiameter() / 4.0 * 3.14;
    double gasVel =
        thermoSystem.getTotalNumberOfMoles() * thermoSystem.getMolarVolume() / 1e5 / area;
    setLiquidCarryoverFraction(ktotal);
    return gasVel;
  }
}
