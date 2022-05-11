Maven project dependency
========================

The Lablink Universal API client's compiled Java package is available on the |MCR|_.
Use it in your local Maven_ setup by including the following dependency into your *pom.xml*:

.. code-block:: xml

   <dependency>
     <groupId>at.ac.ait.lablink.clients</groupId>
     <artifactId>universalapiclient</artifactId>
     <version>0.0.1</version>
   </dependency>

.. note:: You may have to adapt this snippet to use the latest version, please check the |MCR|_.

Installation from source
========================

Installation from source requires a local **Java Development Kit** installation, for instance the `Oracle Java SE Development Kit 13 <https://www.oracle.com/technetwork/java/javase/downloads/index.html>`_ or the `OpenJDK <https://openjdk.java.net/>`_.

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
