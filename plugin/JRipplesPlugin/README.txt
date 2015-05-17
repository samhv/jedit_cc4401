REQUISITOS:

Fuera de lo ya incluido en git para ocupar este esqueleto de plugin debes asegurarte que este ubicado en un directorio
tal que si navego a-> ..\JEdit\jedit_cc4401-master\src\build encuentre el directorio donde tienes el file jEdit.jar.
Si deseas modificar esto, debes cambiar en el archivo plugin-build.xml, este se encuentra en <root del git>\buildsupport. Se debe
modificar la propiedad <property name="jedit.install.dir"  value="..\JEdit\jedit_cc4401-master\src\build" /> en la linea 124 con el directorio donde se encuentra jEdit.jar

BUILD:

Para hacer build del plugin, debes ejecutar ant build en el directorio del plugin, el jar se creara en el directorio ..\

INSTALACION:

Para instalar el plugin, debes agarrar el jar construido con build y colocarlo en tu directorio de jars dentro del directorio de settings de jEdit. En mi caso este es:
C:\Users\Javier\AppData\Roaming\jEdit\jars. Puedes encontrar el tuyo buscando en la barra de menu de jedit (Donde esta file, edit, etc.) vas a Utilities->Settings Directory y
al directorio que veas ahi agregas \jars. Luego (despues de haber reiniciado jEdit) puedes ir a plugins, plugin manager hacer uncheck de hide libraries, y tickear JRipples

QUE HACE?:

Nada mucho, si haces click en test abre un window. La gracia es que es facil extenderlo.