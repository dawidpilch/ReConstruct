package com.reconstruct.model.beam.loading;

import com.reconstruct.model.beam.loading.distributed.UniformlyDistributedLoad;
import com.reconstruct.model.beam.loading.moment.BendingMoment;
import com.reconstruct.model.beam.loading.point.HorizontalPointLoad;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class Loading {
    private final List<VerticalPointLoad> verticalPointLoads;
    private final List<HorizontalPointLoad> horizontalPointLoads;
    private final List<BendingMoment> bendingMoments;
    private final Collection<UniformlyDistributedLoad> uniformlyDistributedLoads;

    public static Loading empty() {
        return new Loading(List.of(), List.of(), List.of(), List.of());
    }

    public Loading(
            Collection<VerticalPointLoad> verticalPointLoads,
            Collection<HorizontalPointLoad> horizontalPointLoads,
            Collection<BendingMoment> bendingMoments,
            Collection<UniformlyDistributedLoad> uniformlyDistributedLoads) {
        this.verticalPointLoads = List.copyOf(Objects.requireNonNull(verticalPointLoads));
        this.horizontalPointLoads = List.copyOf(Objects.requireNonNull(horizontalPointLoads));
        this.bendingMoments = List.copyOf(Objects.requireNonNull(bendingMoments));
        this.uniformlyDistributedLoads = List.copyOf(Objects.requireNonNull(uniformlyDistributedLoads));
    }

    public List<VerticalPointLoad> verticalPointLoads() {
        return List.copyOf(verticalPointLoads);
    }

    public List<HorizontalPointLoad> horizontalPointLoads() {
        return List.copyOf(horizontalPointLoads);
    }

    public List<BendingMoment> bendingMoments() {
        return List.copyOf(bendingMoments);
    }

    public Collection<UniformlyDistributedLoad> uniformlyDistributedLoads() {
        return uniformlyDistributedLoads;
    }
}