package br.ufg.reconface.bash;

import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;


public final class PortListener implements ApplicationListener<ServletWebServerInitializedEvent> {

    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        Arquivo.PORT.criar(Integer.toString(event.getWebServer().getPort()));
    }
}
