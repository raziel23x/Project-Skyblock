package raziel23x.projectskyblock.simulation.energy;

import java.util.Objects;

/** Stateless authoritative solver for deterministic energy movement. */
public final class EnergyEngine {
    private EnergyEngine() {
    }

    public static EnergyDiagnostics transfer(
            EnergyBuffer source,
            EnergyBuffer target,
            EnergyRequest request) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(request, "request");
        if (source == target) {
            throw new IllegalArgumentException("source and target must be different buffers");
        }

        long sourceBefore = source.storedEnergy();
        long targetBefore = target.storedEnergy();

        long sourceLimit = Math.min(sourceBefore, source.limits().maximumExtractPerTick());
        long targetLimit = maximumExtractForAcceptedEnergy(
                Math.min(target.availableCapacity(), target.limits().maximumReceivePerTick()),
                request.efficiencyPartsPerMillion());
        long extractedPlanned = Math.min(request.requestedEnergy(), Math.min(sourceLimit, targetLimit));
        long deliveredPlanned = applyEfficiency(extractedPlanned, request.efficiencyPartsPerMillion());

        long extracted = source.extract(extractedPlanned);
        long delivered = target.receive(deliveredPlanned);
        if (extracted != extractedPlanned || delivered != deliveredPlanned) {
            throw new IllegalStateException("energy buffer violated its advertised limits");
        }

        long lost = extracted - delivered;
        EnergyTransfer transfer = new EnergyTransfer(
                request.requestedEnergy(), extracted, delivered, lost);
        EnergyFlowResult flow = new EnergyFlowResult(
                sourceBefore, source.storedEnergy(), targetBefore, target.storedEnergy(), transfer);

        boolean sourceLimited = extractedPlanned < request.requestedEnergy() && sourceLimit <= targetLimit;
        boolean targetLimited = extractedPlanned < request.requestedEnergy() && targetLimit <= sourceLimit;
        boolean throughputLimited = extractedPlanned < request.requestedEnergy()
                && (source.limits().maximumExtractPerTick() < sourceBefore
                || target.limits().maximumReceivePerTick() < target.availableCapacity() + delivered);
        return new EnergyDiagnostics(flow, sourceLimited, targetLimited, throughputLimited);
    }

    static long applyEfficiency(long extractedEnergy, int efficiencyPartsPerMillion) {
        if (extractedEnergy == 0 || efficiencyPartsPerMillion == 0) {
            return 0;
        }
        long quotient = extractedEnergy / EnergyConstants.PARTS_PER_MILLION;
        long remainder = extractedEnergy % EnergyConstants.PARTS_PER_MILLION;
        return Math.addExact(
                Math.multiplyExact(quotient, efficiencyPartsPerMillion),
                (remainder * efficiencyPartsPerMillion) / EnergyConstants.PARTS_PER_MILLION);
    }

    private static long maximumExtractForAcceptedEnergy(long acceptedCapacity, int efficiencyPpm) {
        if (acceptedCapacity == 0 || efficiencyPpm == 0) {
            return 0;
        }
        if (efficiencyPpm == EnergyConstants.PARTS_PER_MILLION) {
            return acceptedCapacity;
        }
        long quotient = acceptedCapacity / efficiencyPpm;
        long remainder = acceptedCapacity % efficiencyPpm;
        long ceiling = Math.addExact(
                Math.multiplyExact(quotient, EnergyConstants.PARTS_PER_MILLION),
                (remainder * EnergyConstants.PARTS_PER_MILLION + efficiencyPpm - 1L) / efficiencyPpm);
        while (ceiling > 0 && applyEfficiency(ceiling, efficiencyPpm) > acceptedCapacity) {
            ceiling--;
        }
        return ceiling;
    }
}
