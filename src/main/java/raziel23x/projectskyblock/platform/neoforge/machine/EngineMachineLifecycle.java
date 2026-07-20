package raziel23x.projectskyblock.platform.neoforge.machine;

/** Observable lifecycle of the Minecraft adapter, not simulation activity state. */
public enum EngineMachineLifecycle {
    CONSTRUCTED,
    LOADED,
    ACTIVE,
    UNLOADED,
    REMOVED
}
