package com.app.controllers;

import com.app.models.Period;

public class ShowPeriodController implements DataInitializable<Period> {
    private Period shownPeriod;

    @Override
    public void initializeWithData(Period info) {
        shownPeriod = info;
    }
}
