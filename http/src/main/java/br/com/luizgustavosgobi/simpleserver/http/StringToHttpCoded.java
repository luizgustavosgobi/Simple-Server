package br.com.luizgustavosgobi.simpleServer.http;

import br.com.luizgustavosgobi.simpleServer.core.converter.ChannelHandler;
import br.com.luizgustavosgobi.simpleServer.core.converter.DataPipelineContext;
import br.com.luizgustavosgobi.simpleServer.http.entities.RequestEntity;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.http.parser.HttpParser;

public class StringToHttpCoded implements ChannelHandler {

    @Override
    public RequestEntity<?> channelRead(DataPipelineContext ctx, Object msg) throws Exception {
        if (!(msg instanceof String))
            throw new IllegalArgumentException("The StringToHttpCoded Read expected an String as input, but got " + msg.getClass().getTypeName());

        String s = (String) msg;
        return HttpParser.parse(s);
    }

    @Override
    public String channelWrite(DataPipelineContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ResponseEntity<?>))
            throw new IllegalArgumentException("The StringToHttpCoded Write expected an ResponseEntity<?> as input, but got " + msg.getClass().getTypeName());

        ResponseEntity<?> response = (ResponseEntity<?>) msg;
        return response.toString();
    }
}
