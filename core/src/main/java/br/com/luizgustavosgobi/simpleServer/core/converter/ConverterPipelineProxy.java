package br.com.luizgustavosgobi.simpleServer.core.converter;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;

import java.util.ArrayList;
import java.util.List;

public class ConverterPipelineProxy {
    protected final List<ConverterPipeline> pipelines;

    public ConverterPipelineProxy(List<ConverterPipeline> pipelines) {
        this.pipelines = pipelines;
    }

    public ConverterPipelineProxy() {
        this.pipelines = new ArrayList<>();
    }

    public Object doDecode(Client client, Object data) throws Exception {
        ConverterContext context = createContext(client);

        ConverterPipeline pipeline = getMatchingPipeline(context);
        if (pipeline != null)
            return pipeline.decode(data, context);

        return null;
    }

    public Object doEncode(Client client, Object data) throws Exception {
        ConverterContext context = createContext(client);

        ConverterPipeline pipeline = getMatchingPipeline(context);
        if (pipeline != null)
            return pipeline.encode(data, context);

        return null;
    }

    protected ConverterContext createContext(Client client) {
        return new ConverterContext(client);
    }

    protected ConverterPipeline getMatchingPipeline(ConverterContext context) {
        return pipelines.isEmpty() ? null : pipelines.getLast();
    }

    public List<ConverterPipeline> getPipelines() {
        return pipelines;
    }

    public int getPipelineCount() {
        return pipelines.size();
    }

    public void addPipeline(ConverterPipeline pipeline) {
        pipelines.add(pipeline);
    }
}
