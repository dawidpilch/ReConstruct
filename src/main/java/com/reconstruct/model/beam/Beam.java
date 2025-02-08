package com.reconstruct.model.beam;

import com.reconstruct.model.beam.loading.Loading;
import com.reconstruct.model.beam.loading.moment.BendingMoment;
import com.reconstruct.model.beam.loading.point.HorizontalPointLoad;
import com.reconstruct.model.beam.loading.point.VerticalPointLoad;
import com.reconstruct.model.beam.value.Position;

import java.util.List;
import java.util.Map;

public interface Beam {
    LoadingAnalysis loadingAnalysis(Loading loading);
}
