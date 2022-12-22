package at.ac.ait.lablink.clients.universalapiclient.universalapi;

import at.ac.ait.lablink.core.service.types.Complex;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Class ComplexSerializer.
 *
 * <p>This class implements a JSON serializer for data of type Complex.
 */
public class ComplexSerializer extends StdSerializer<Complex> {

  /**
   * Default constructor.
   */
  public ComplexSerializer() {
    this( null );
  }

  /**
   * Alternate constructor that is needed to work around kinks of generic type handling.
   *
   * @param clazz class type
   */
  @SuppressWarnings( "unchecked" )
  public ComplexSerializer( Class clazz ) {
    super( clazz );
  }

  /**
   * Serialize values of type Complex.
   *
   * @param value value to serialize (cannot be null)
   * @param jsonGenerator generator used to output resulting JSON content
   * @param provider can be used to get serializers for serializing objects contained by the value
   * @throws IOException IO exception
   */
  @Override
  public void serialize( Complex value, JsonGenerator jsonGenerator, SerializerProvider provider )
      throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeNumberField( "real", value.re() );
    jsonGenerator.writeNumberField( "imag", value.im() );
    jsonGenerator.writeEndObject();
  }
}
