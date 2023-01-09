Maven project dependency
========================

The Lablink Universal API client's compiled Java package is available on the |MCR|_.
Use it in your local Maven_ setup by including the following dependency into your *pom.xml*:

.. code-block:: xml

   <dependency>
     <groupId>at.ac.ait.lablink.clients</groupId>
     <artifactId>universalapiclient</artifactId>
     <version>0.1.0</version>
   </dependency>

.. note:: You may have to adapt this snippet to use the latest version, please check the |MCR|_.

Installation from source
========================

Installation from source requires a local installation of the **Java Development Kit**, for instance the `Oracle Java SE Development Kit 13 <https://www.oracle.com/technetwork/java/javase/downloads/index.html>`_ or the `OpenJDK <https://openjdk.java.net/>`_.

Then make sure that the ``JAVA_HOME`` environment variable is set and points to your JDK installation.

.. seealso:: **Windows only**: Add the JDK installation directory to your ``JAVA_HOME`` user environment variable:

  #. open the system properties (``WinKey`` + ``Pause`` or go to *Settings* |arrow| *System* |arrow| *About* |arrow| *System Info* |arrow| *Advanced System Settings*)
  #. select the *Advanced* tab, then the *Environment Variables* button
  #. select and edit the ``JAVA_HOME`` variable in the user variables, e.g., adding *C:\\Program Files\\Java\\jdk-13.0.2*.

.. |arrow| unicode:: U+2192 .. rightwards arrow

Check out the project and compile it with Maven_:

.. code-block:: winbatch

   git clone https://github.com/ait-lablink/lablink-universal-api-client
   cd lablink-universal-api-client
   mvnw clean package

This will create JAR file *universalapiclient-<VERSION>-jar-with-dependencies.jar* in subdirectory *target/assembly*.
Furthermore, all required JAR files for running the example will be copied to subdirectory *target/dependency/*.

Troubleshooting the installation
================================

Nothing yet ...


.. |MCR| replace:: Maven Central Repository
.. _MCR: https://search.maven.org/artifact/at.ac.ait.lablink.clients/universalapiclient
.. _Maven: https://maven.apache.org
