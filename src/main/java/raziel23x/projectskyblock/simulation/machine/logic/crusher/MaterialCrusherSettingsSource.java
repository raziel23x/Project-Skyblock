package raziel23x.projectskyblock.simulation.machine.logic.crusher;

/** Supplies current crusher settings without exposing a platform configuration API to the engine. */
@FunctionalInterface
public interface MaterialCrusherSettingsSource {
    MaterialCrusherSettings currentSettings();
}
