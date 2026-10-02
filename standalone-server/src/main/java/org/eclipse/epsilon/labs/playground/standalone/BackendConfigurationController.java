package org.eclipse.epsilon.labs.playground.standalone;

import org.eclipse.epsilon.labs.playground.fn.emfatic2graph.Emfatic2GraphController;
import org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml.Emfatic2PlantUMLController;
import org.eclipse.epsilon.labs.playground.fn.flexmi2graph.Flexmi2GraphController;
import org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml.Flexmi2PlantUMLController;
import org.eclipse.epsilon.labs.playground.fn.run.RunEpsilonController;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller
public class BackendConfigurationController {

    public static final String PATH = "backend.json";

    protected static final String DEFAULT_LIVE_SHARE_URL = "wss://demos.yjs.dev/ws";
    protected static final String DEFAULT_KROKI_URL = "https://kroki.io";

    @Value("${playground.epsilon.url:`" + RunEpsilonController.PATH + "`}")
    private String runEpsilonUrl;

    @Value("${playground.flexmi2plantuml.url:`" + Flexmi2PlantUMLController.PATH + "`}")
    private String flexmi2PlantUMLUrl;

    @Value("${playground.emfatic2plantuml.url:`" + Emfatic2PlantUMLController.PATH + "`}")
    private String emfatic2PlantUMLUrl;

    @Value("${playground.flexmi2graph.url:`" + Flexmi2GraphController.PATH + "`}")
    private String flexmi2GraphUrl;

    @Value("${playground.emfatic2graph.url:`" + Emfatic2GraphController.PATH + "`}")
    private String emfatic2GraphUrl;

    @Value("${playground.short.url:`" + ShortURLController.PATH + "`}")
    private String shortenerUrl;

    @Value("${playground.yjs.url:`" + DEFAULT_LIVE_SHARE_URL + "`}")
    private String liveShareUrl;

    @Value("${playground.kroki.url:`" + DEFAULT_KROKI_URL + "`}")
    private String krokiUrl;

    @Get(BackendConfigurationController.PATH)
    public BackendConfiguration getConfig() {
        BackendConfiguration config = new BackendConfiguration();
        createService(config, "RunEpsilonFunction", runEpsilonUrl);
        createService(config, "FlexmiToPlantUMLFunction", flexmi2PlantUMLUrl);
        createService(config, "EmfaticToPlantUMLFunction", emfatic2PlantUMLUrl);
        createService(config, "FlexmiToGraphFunction", flexmi2GraphUrl);
        createService(config, "EmfaticToGraphFunction", emfatic2GraphUrl);
        createService(config, "ShortURLFunction", shortenerUrl);
        createService(config, "Yjs", liveShareUrl);
        createService(config, "Kroki", krokiUrl);
        return config;
    }

    private void createService(BackendConfiguration config, String name, String url) {
        BackendService service = new BackendService();
        service.setName(name);
        service.setUrl(url);
        config.getServices().add(service);
    }

}
