package br.edu.ufrgs.inf.bpm.rest.bpmnVerification;

import br.edu.ufrgs.inf.bpm.wrapper.StardogWrapper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class InitializeListener implements ServletContextListener {

    @Override
    public final void contextInitialized(final ServletContextEvent sce) {
        StardogWrapper.StartStardog();
    }

    @Override
    public final void contextDestroyed(final ServletContextEvent sce) {
        StardogWrapper.FinishStardog();
    }

}
