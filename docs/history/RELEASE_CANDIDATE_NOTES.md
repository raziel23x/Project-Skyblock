# First Git Push Release Candidate

This build intentionally freezes the working machine logic and regular Basic Energy Cable behavior.

## Final changes in this candidate

- Structural Energy Frame rebuilt with graphite rails and recessed cyan channels.
- Unique texture filenames prevent legacy resource collisions.
- Material Crusher inventory label no longer overlaps the machine panel or slots.

## Test checklist

1. Place isolated and connected Structural Energy Frames.
2. Confirm graphite dominates the frame and cyan is limited to the internal channel.
3. Connect the frame to Basic Energy Cable, Thermal Generator, Material Crusher, and Creative Energy Cell.
4. Confirm FE transfer still works across mixed cable/frame networks.
5. Open both machine GUIs and verify slots, labels, progress, energy, and status text.
6. Restart the client once to confirm no stale resource cache remains.
