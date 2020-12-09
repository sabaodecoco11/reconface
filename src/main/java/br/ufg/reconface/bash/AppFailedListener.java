package br.ufg.reconface.bash;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;


public final class AppFailedListener implements ApplicationListener<ApplicationFailedEvent> {

  @Override
  public void onApplicationEvent(ApplicationFailedEvent event) {
    StringWriter output = new StringWriter();
    getMostSpecificCause(event.getException()).printStackTrace(new PrintWriter(output));
    Arquivo.FAILED.criar(output.toString());
  }
}
