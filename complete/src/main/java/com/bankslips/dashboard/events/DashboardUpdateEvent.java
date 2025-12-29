package com.bankslips.dashboard.events;

import org.springframework.context.ApplicationEvent;

public class DashboardUpdateEvent extends ApplicationEvent {

    private static final long serialVersionUID = 462072936095534642L;

	public DashboardUpdateEvent(Object source) {
        super(source);
    }
}
