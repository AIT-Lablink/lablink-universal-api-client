//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class Configuration.
 *
 * <p>This class implements the Lablink-specific configuration returned by the REST interface
 * of the <a href="https://github.com/ERIGrid2/JRA-3.1-api">ERIGrid Universal API</a>.
 * It can be translated to/from JSON for use with the REST interface.
 */
@XmlRootElement
public class Configuration {

  /** Lablink client description. */
  private String clientDescription;

  /** Lablink group name. */
  private String groupName;

  /** Lablink client name. */
  private String clientName;

  /** Lablink scenario name. */
  private String scenarioName;

  /** Lablink client properties URL. */
  private String lablinkPropertiesUrl;

  /** Lablink sync host properties URL. */
  private String syncHostPropertiesUrl;

  /**
   * Constructor.
   *
   * @param clientDescription  Lablink client description
   * @param clientName  Lablink client name
   * @param groupName  Lablink group name
   * @param scenarioName  Lablink scenario name
   * @param lablinkPropertiesUrl  Lablink client properties URL
   * @param syncHostPropertiesUrl  Lablink sync host properties URL
   */
  public Configuration(
      String clientDescription,
      String clientName,
      String groupName,
      String scenarioName,
      String lablinkPropertiesUrl,
      String syncHostPropertiesUrl ) {
    this.clientDescription = clientDescription;
    this.groupName = groupName;
    this.clientName = clientName;
    this.scenarioName = scenarioName;
    this.lablinkPropertiesUrl = lablinkPropertiesUrl;
    this.syncHostPropertiesUrl = syncHostPropertiesUrl;
  }

  /**
   * Retrieve Lablink client description.
   *
   * @return client description
   */
  public String getClientDescription() {
    return this.clientDescription;
  }

  /**
   * Retrieve Lablink group name.
   *
   * @return group name
   */
  public String getGroupName() {
    return this.groupName;
  }

  /**
   * Retrieve Lablink client name.
   *
   * @return client name
   */
  public String getClientName() {
    return this.clientName;
  }

  /**
   * Retrieve Lablink scenario name.
   *
   * @return scenario name
   */
  public String getScenarioName() {
    return this.scenarioName;
  }

  /**
   * Retrieve Lablink client properties URL.
   *
   * @return lablink properties URL
   */
  public String getLablinkPropertiesUrl() {
    return this.lablinkPropertiesUrl;
  }

  /**
   * Retrieve Lablink sync host properties URL.
   *
   * @return sync host properties URL
   */
  public String getSyncHostPropertiesUrl() {
    return this.syncHostPropertiesUrl;
  }

}
