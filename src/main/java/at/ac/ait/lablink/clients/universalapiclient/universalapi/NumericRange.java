package at.ac.ait.lablink.clients.universalapiclient.universalapi;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NumericRange<T> {
  
  public NumericRange( T min, T max ) {
    this.min = min;
    this.max = max;
  }

  public T min;

  public T max;

}
