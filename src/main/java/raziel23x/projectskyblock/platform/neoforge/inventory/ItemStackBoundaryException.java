package raziel23x.projectskyblock.platform.neoforge.inventory;

/** Indicates that a Minecraft ItemStack could not safely cross the simulation boundary. */
public final class ItemStackBoundaryException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ItemStackBoundaryException(String message) {
        super(message);
    }

    public ItemStackBoundaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
