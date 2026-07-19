PROJECT SKYBLOCK - BACKEND MILESTONE 1

This package is an overwrite update for the simulation-foundation checkpoint.

1. Close the development Minecraft client.
2. Copy the contents of the Project-Skyblock folder into your repository root.
3. Choose Replace files in the destination when Windows asks.
4. Do not delete .git or your existing src folder.
5. Run:

   gradlew.bat compileJava
   gradlew.bat runClient

This milestone adds the explicit simulation scheduler only. Existing machines
are intentionally not connected to it yet, so current gameplay should behave
exactly as before.
