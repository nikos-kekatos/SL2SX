# SL2SX

SL2SX is a tool that translates Simulink models (saved in XML format) into SpaceEx models (respecting the SX format).

- [Installation](#Installation)
- [Files](#Files)
- [Usage](#Usage)

# Installation <a name="Installation"></a>

There are two options: (i) download the files directly from the website, and (ii) clone the repository to your local files by writting the following command in the terminal/konsole/command prompt

``
$ git clone git@github.com:nikos-kekatos/SL2SX.git
``



#### Dependencies

The Java Runtime Environment should be installed (which is typically pre-installed in most systems).
To run the translator, you just need to run the *SL2SX.jar* file.  

- Double click on the jar file or 
- Use the command line `java -jar <SL2SX.jar>`.







# Files <a name="Files"></a>

The folder `Documents/` contains the documentation of this project.  The folder `src/` contains the source code written in Java. Original contibutor was Stefano Minopoli. Examples can be found in the corresponding folder.




<!--*Note:*-->




# Usage <a name="Usage"></a>

There are two ways to run the translator. The first one is to employ the standalone (SL2SX.jar) file, by double clicking on it and selecting the Simulink file (in XML format). Note that a Simulink file is typically expressed in `.slx` or `.mdl`. As such, it has to be transformed in XML format, by writting the following commands in MATLAB command window.

``load_system(model_name) % required, if the Simulink model is not loaded``

``save_system('model_name.slx','model_name.xml','ExportToXML',true)`` or
``save_system('model_name.mdl','model_name.xml','ExportToXML',true)``

The second way is to utilize the `SL2SX_terminal.jar` and perform the translation through MATLAB. The Simulink file should be saved as an XML file and passed as an input.

``load_system(model_name)``
``save_system('model_name.mdl','model_name.xml','ExportToXML',true)``
``system(sprintf('java -jar SL2SX_terminal.jar %s','model_name.xml'))``

